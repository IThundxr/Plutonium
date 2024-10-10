package dev.ithundxr.plutonium.mixin.zstd;

import com.github.luben.zstd.ZstdInputStreamNoFinalizer;
import com.github.luben.zstd.ZstdOutputStreamNoFinalizer;
import dev.ithundxr.plutonium.mixinsupport.ChunkStreamVersionExt;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegionFileVersion.class)
public class MixinRegionFileVersion {
    @Shadow private static RegionFileVersion register(RegionFileVersion version) { throw new AbstractMethodError(); }

    @Inject(at=@At("TAIL"), method="<clinit>")
    private static void plutonium$addZstd(CallbackInfo ci) {
        register(ChunkStreamVersionExt.ZSTD = new RegionFileVersion(53, // chosen by fair dice roll. guaranteed to be random.
                in -> new FastBufferedInputStream(new ZstdInputStreamNoFinalizer(in)), // All users close the stream manually, so no need for finalizer
                out -> {
                    var z = new ZstdOutputStreamNoFinalizer(out); // All users close the stream manually, so no need for finalizer
                    z.setLevel(7);
                    z.setLong(18);
                    z.setChecksum(true);
                    return new FastBufferedOutputStream(z);
                }));
    }
}
