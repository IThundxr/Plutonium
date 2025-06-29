package dev.ithundxr.plutonium;

import com.github.luben.zstd.ZstdOutputStreamNoFinalizer;
import dev.ithundxr.railwaystweaks.utils.IOUtilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class ZstdIOUtilities {
	public static void writeNbtCompressed(Path targetPath, CompoundTag compoundTag, int level) throws IOException {
		IOUtilities.atomicWrite(targetPath, output -> {
			try (ZstdOutputStreamNoFinalizer z = new ZstdOutputStreamNoFinalizer(output)) {
				z.setChecksum(true);
				z.setLevel(level);
				NbtIo.write(compoundTag, new DataOutputStream(z));
			}
		});
	}
}
