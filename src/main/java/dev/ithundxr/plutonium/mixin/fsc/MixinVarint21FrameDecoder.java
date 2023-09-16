package dev.ithundxr.plutonium.mixin.fsc;

import net.minecraft.network.Varint21FrameDecoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Varint21FrameDecoder.class)
public class MixinVarint21FrameDecoder {
    @ModifyConstant(constant = @Constant(intValue = 3), method = "decode(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Ljava/util/List;)V")
    public int plutonium$liftPacketSizeLimit(int orig) {
        return 5;
    }
}