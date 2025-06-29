package dev.ithundxr.plutonium.mixin.zstd;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ithundxr.plutonium.ZstdIOUtilities;
import dev.ithundxr.railwaystweaks.utils.IOUtilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.io.File;
import java.io.IOException;

@Mixin(SavedData.class)
public class MixinSavedData {
    @Shadow @Final private static Logger LOGGER;

    @WrapOperation(method = "save(Ljava/io/File;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtIo;writeCompressed(Lnet/minecraft/nbt/CompoundTag;Ljava/io/File;)V"))
    public void plutonium$writeZstd(CompoundTag compoundTag, File file, Operation<Void> original) throws IOException {
        String path = file.getPath();
        if (path.endsWith(".dat")) {
            File zstd = new File(path.substring(0, path.length() - 4) + ".zat");
            IOUtilities.withIOWorker(() -> {
				try {
					ZstdIOUtilities.writeNbtCompressed(zstd.toPath(), compoundTag, 4);
				} catch (IOException e) {
                    LOGGER.error("Could not save data {}", this, e);
				}
			});
            file.delete();
        } else {
            // oookay, I don't know what you want. have fun.
            original.call(compoundTag, file);
        }
    }
}
