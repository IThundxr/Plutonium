package dev.ithundxr.plutonium.mixin.fsc;

import com.github.luben.zstd.ZstdOutputStream;
import dev.ithundxr.plutonium.Plutonium;
import dev.ithundxr.plutonium.mixinsupport.FSCConnection;
import dev.ithundxr.plutonium.utils.ReassignableOutputStream;
import dev.ithundxr.plutonium.utils.ZstdDecoder;
import dev.ithundxr.plutonium.utils.ZstdEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Mixin(Connection.class)
public class MixinConnection implements FSCConnection {

    @Shadow
    private Channel channel;

    @Shadow
    private void sendPacket(Packet<?> packet, PacketSendListener callbacks) { throw new AbstractMethodError(); }

    @Unique private final LinkedBlockingQueue<Plutonium.QueuedPacket> plutonium$queue = Plutonium.getNextQueue();
    @Unique private boolean plutonium$fsc = false;
    @Unique private boolean plutonium$fscStarted = false;

    /**
     * With a lot of connections, simply the act of writing packets becomes slow.
     * Doing this on the server thread reduces TPS for no good reason.
     * <p>
     * The client already does networking roughly like this, so the protocol stack is already
     * designed to expect this behavior.
     */
    @Redirect(at = @At(value="INVOKE", target="Lnet/minecraft/network/Connection;sendPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V"),
            method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V")
    public void plutonium$asyncPacketSending(Connection instance, Packet<?> packet, PacketSendListener sendListener) {
        if (packet instanceof ClientboundLoginPacket && plutonium$fsc && !plutonium$fscStarted) {
            plutonium$enableFSCNow();
        }
        if (channel.attr(Connection.ATTRIBUTE_PROTOCOL).get() == ConnectionProtocol.PLAY) {
            plutonium$queue.add(new Plutonium.QueuedPacket(instance, packet, sendListener));
        } else {
            sendPacket(packet, sendListener);
        }
    }

    @Inject(at=@At("HEAD"), method="setupCompression", cancellable=true)
    public void plutonium$handleCompression(int threshold, boolean check, CallbackInfo ci) {
        if (plutonium$fscStarted) {
            ci.cancel();
        }
    }

    @Inject(at=@At("HEAD"), method="setProtocol")
    public void plutonium$handleFSC(ConnectionProtocol state, CallbackInfo ci) {
        if (state == ConnectionProtocol.PLAY && plutonium$fsc && !plutonium$fscStarted) {
            plutonium$enableFSCNow();
        }
    }

    @Unique
    private void plutonium$enableFSCNow() {
        plutonium$fscStarted = true;
        ChannelPipeline pipeline = channel.pipeline();
        Connection self = (Connection) (Object) this;
        try {
            boolean client = self.getReceiving() == PacketFlow.CLIENTBOUND;
            ReassignableOutputStream ros = new ReassignableOutputStream();
            ZstdOutputStream zos = new ZstdOutputStream(ros);
            zos.setLevel(client ? 6 : 4);
            zos.setLong(client ? 27 : 22);
            zos.setCloseFrameOnFlush(false);
            ZstdEncoder enc = new ZstdEncoder(ros, zos, TimeUnit.MILLISECONDS.toNanos(client ? 0 : 40));
            ZstdDecoder dec = new ZstdDecoder();
            pipeline.remove("compress");
            pipeline.remove("decompress");
            pipeline.addBefore("prepender", "plutonium:fsc_enc", enc);
            pipeline.addBefore("splitter", "plutonium:fsc_dec", dec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void plutonium$enableFullStreamCompression() {
        plutonium$fsc = true;
    }
}
