package dev.ithundxr.plutonium.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Connection.class)
public interface AccessorConnection {
    @Invoker("sendPacket")
    void plutonium$sendImmediately(Packet<?> packet, PacketSendListener callbacks);

    @Accessor("channel")
    Channel plutonium$getChannel();
}