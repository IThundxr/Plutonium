package dev.ithundxr.plutonium.mixin.zstd;

import dev.ithundxr.plutonium.mixinsupport.ChunkStreamVersionExt;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RegionFile.class)
public class MixinRegionFile {
    @Redirect(method="<init>(Ljava/nio/file/Path;Ljava/nio/file/Path;Z)V", at = @At(value = "FIELD", target="Lnet/minecraft/world/level/chunk/storage/RegionFileVersion;VERSION_DEFLATE:Lnet/minecraft/world/level/chunk/storage/RegionFileVersion;"))
    private static RegionFileVersion plutonium$useZstd() {
        RegionFileVersion.isValidVersion(0); // initialize class
        return ChunkStreamVersionExt.ZSTD;
    }
}
