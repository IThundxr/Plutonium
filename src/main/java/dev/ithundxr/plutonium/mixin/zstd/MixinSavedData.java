package dev.ithundxr.plutonium.mixin.zstd;

import com.github.luben.zstd.ZstdOutputStreamNoFinalizer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.saveddata.SavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Mixin(SavedData.class)
public class MixinSavedData {
    @Redirect(at=@At(value="INVOKE", target="Lnet/minecraft/nbt/NbtIo;writeCompressed(Lnet/minecraft/nbt/CompoundTag;Ljava/io/File;)V"),
            method= "save(Ljava/io/File;)V")
    public void plutonium$writeZstd(CompoundTag compoundTag, File vanilla) throws IOException {
        String path = vanilla.getPath();
        if (path.endsWith(".dat")) {
            File zstd = new File(path.substring(0, path.length() - 4) + ".zat");
            // We don't need a finalizer, we're putting it in a try-with-resources clause anyway
            try (ZstdOutputStreamNoFinalizer z = new ZstdOutputStreamNoFinalizer(new FileOutputStream(zstd))) {
                z.setChecksum(true);
                z.setLevel(4);
                NbtIo.write(compoundTag, new DataOutputStream(z));
            }
            vanilla.delete();
        } else {
            // oookay, I don't know what you want. have fun.
            NbtIo.writeCompressed(compoundTag, vanilla);
        }
    }
}
