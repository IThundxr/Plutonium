package dev.ithundxr.plutonium.mixin.fsc;

import dev.ithundxr.plutonium.Plutonium;
import dev.ithundxr.plutonium.mixinsupport.IOUringSupport;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.incubator.channel.uring.IOUring;
import io.netty.incubator.channel.uring.IOUringServerSocketChannel;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.util.LazyLoadedValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerConnectionListener.class)
public class MixinServerConnectionListener {

    @ModifyConstant(constant=@Constant(stringValue="Using epoll channel type"), method="startTcpServerListener")
    public String plutonium$fixLogMessage(String orig) {
        if (IOUringSupport.ENABLED) {
            if (IOUring.isAvailable()) {
                return "Using io_uring channel type";
            } else {
                Plutonium.LOGGER.error("Could not enable io_uring", IOUring.unavailabilityCause());
                return "Using epoll channel type (io_uring requested, but not available)";
            }
        }
        return orig;
    }

    @ModifyVariable(method="startTcpServerListener", ordinal=0, at = @At(value="INVOKE", target="org/slf4j/Logger.info(Ljava/lang/String;)V", ordinal=0))
    public Class<? extends ServerSocketChannel> plutonium$useIoUringClass(Class<? extends ServerSocketChannel> orig) {
        if (IOUringSupport.ENABLED && IOUring.isAvailable()) {
            return IOUringServerSocketChannel.class;
        }
        return orig;
    }

    @ModifyVariable(method="startTcpServerListener", ordinal=0, at = @At(value="INVOKE", target="org/slf4j/Logger.info(Ljava/lang/String;)V", ordinal=0))
    public LazyLoadedValue<? extends EventLoopGroup> plutonium$useIoUringGroup(LazyLoadedValue<? extends EventLoopGroup> orig) {
        if (IOUringSupport.ENABLED && IOUring.isAvailable()) {
            return IOUringSupport.CHANNEL;
        }
        return orig;
    }
}
