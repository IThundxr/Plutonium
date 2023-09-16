package dev.ithundxr.plutonium.mixin.zstd;

import com.github.luben.zstd.ZstdInputStream;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;

import java.io.*;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

@Mixin(DimensionDataStorage.class)
public class MixinDimensionDataStorage {
    @Shadow @Final private static Logger LOGGER;

    @Shadow @Final private File dataFolder;
    @Shadow @Final private DataFixer fixerUpper;

    @Shadow private File getDataFile(String id) { throw new AbstractMethodError(); }

    @Unique private File getZstdFile(String id) {
        return new File(dataFolder, id+".zat");
    }

    /**
     * @author Una
     * @reason Don't check file before calling readNbt
     */
    @Overwrite
    @Nullable
    private <T extends SavedData> T readSavedData(Function<CompoundTag, T> reader, String id) {
        try {
            CompoundTag cmp = readTagFromDisk(id, SharedConstants.getCurrentVersion().getDataVersion().getVersion());
            if (cmp == null) return null;
            return reader.apply(cmp.getCompound("data"));
        } catch (Exception e) {
            LOGGER.error("Error loading saved data: {}", id, e);
        }

        return null;
    }

    /**
     * @author Una
     * @reason Zstd support, code cleanup
     */
    @Overwrite
    public CompoundTag readTagFromDisk(String id, int dataVersion) throws IOException {
        InputStream in;
        File zstd = getZstdFile(id);
        if (zstd.exists()) {
            in = new FastBufferedInputStream(new ZstdInputStream(new FileInputStream(zstd)));
        } else {
            File vanilla = getDataFile(id);
            if (vanilla.exists()) {
                in = new FastBufferedInputStream(new GZIPInputStream(new FileInputStream(vanilla)));
            } else {
                return null;
            }
        }

        try (in) {
            DataInputStream dis = new DataInputStream(in);
            CompoundTag compoundTag = NbtIo.read(dis);
            int version = NbtUtils.getDataVersion(compoundTag, 1343);
            compoundTag = DataFixTypes.SAVED_DATA.update(fixerUpper, compoundTag, version, dataVersion);
            return compoundTag;
        }
    }
}
