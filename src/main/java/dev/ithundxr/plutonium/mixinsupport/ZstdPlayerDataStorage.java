package dev.ithundxr.plutonium.mixinsupport;

import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.client.User;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.slf4j.Logger;

import java.io.*;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class ZstdPlayerDataStorage extends PlayerDataStorage {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final File playerDataDir;

    public ZstdPlayerDataStorage(LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer) {
        super(levelStorageAccess, dataFixer);
        this.playerDataDir = levelStorageAccess.getLevelPath(LevelResource.PLAYER_DATA_DIR).toFile();
    }

    @Override
    public CompoundTag load(Player player) {
        try {
            InputStream in;
            File zstd = new File(playerDataDir, player.getStringUUID() + ".zat");
            if (zstd.isFile()) {
                in = new FastBufferedInputStream(new ZstdInputStream(new FileInputStream(zstd)));
            } else {
                File vanilla = new File(playerDataDir, player.getStringUUID() + ".dat");
                if (vanilla.isFile()) {
                    in = new FastBufferedInputStream(new GZIPInputStream(new FileInputStream(zstd)));
                } else {
                    return null;
                }
            }
            try (in) {
                CompoundTag compoundTag = NbtIo.read(new DataInputStream(in));
                int ver = NbtUtils.getDataVersion(compoundTag, -1);
                player.load(DataFixTypes.PLAYER.updateToCurrentVersion(super.fixerUpper, compoundTag, ver));
                return compoundTag;
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to load player data for {}", player.getName().getString());
            return null;
        }
    }

    @Override
    public void save(Player player) {
        try {
            CompoundTag compoundTag = player.saveWithoutId(new CompoundTag());
            File tmp = File.createTempFile(player.getStringUUID() + "-", ".zat", playerDataDir);
            try (ZstdOutputStream z = new ZstdOutputStream(new FileOutputStream(tmp))) {
                z.setChecksum(true);
                z.setLevel(6);
                NbtIo.write(compoundTag, new DataOutputStream(z));
            }
            File tgt = new File(playerDataDir, player.getStringUUID() + ".zat");
            File backup = new File(playerDataDir, player.getStringUUID() + ".zat_old");
            Util.safeReplaceFile(tgt, tmp, backup);
            File oldTgt = new File(playerDataDir, player.getStringUUID() + ".dat");
            File oldBackup = new File(playerDataDir, player.getStringUUID() + ".dat_old");
            oldTgt.delete();
            if (backup.exists()) oldBackup.delete();
        } catch (Exception e) {
            LOGGER.warn("Failed to save player data for {}", player.getName().getString());
        }
    }

    @Override
    public String[] getSeenPlayers() {
        if (playerDataDir.isDirectory()) {
            Set<String> set = new ObjectOpenHashSet<>();
            for (String s : playerDataDir.list()) {
                if (s.endsWith(".dat") || s.endsWith(".zat")) {
                    set.add(s.substring(0, s.length()-4));
                }
            }
            return set.toArray(String[]::new);
        }
        return new String[0];
    }

}
