package dev.ithundxr.plutonium.mixin.fsc;

import net.minecraft.network.Varint21LengthFieldPrepender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Varint21LengthFieldPrepender.class)
public class MixinVarint21LengthFieldPrepender {
    @ModifyConstant(constant = @Constant(intValue = 3), method = "encode(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Lio/netty/buffer/ByteBuf;)V")
    public int plutonium$liftPacketSizeLimit(int orig) {
        return 5;
    }
}