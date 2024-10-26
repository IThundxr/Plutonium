package dev.ithundxr.plutonium.compat;

import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdInputStreamNoFinalizer;
import com.github.luben.zstd.ZstdOutputStream;
import com.github.luben.zstd.ZstdOutputStreamNoFinalizer;
import de.bluecolored.bluemap.core.storage.compression.BufferedCompression;
import de.bluecolored.bluemap.core.storage.compression.Compression;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.mca.region.MCARegion;

public class BluemapCompat {
    public static Compression ZSTD_WITH_FINALIZER = new BufferedCompression(Key.bluemap("zstd_with_finalizer"), "zstd_with_finalizer", ".zst", ZstdOutputStream::new, ZstdInputStream::new);
    public static Compression ZSTD_NO_FINALIZER = new BufferedCompression(Key.bluemap("zstd_no_finalizer"), "zstd_no_finalizer", ".zst", ZstdOutputStreamNoFinalizer::new, ZstdInputStreamNoFinalizer::new);
    
    public static void init() {
        Compression.REGISTRY.register(ZSTD_WITH_FINALIZER);
        Compression.REGISTRY.register(ZSTD_NO_FINALIZER);

        Compression compression;
        
        try {
            compression = ZstdCompressionTypes.valueOf(System.getProperty("plutonium.bluemap.compressionType")).compression;
        } catch (Throwable ignored) {
            compression = ZSTD_NO_FINALIZER;
        }
        
        MCARegion.CHUNK_COMPRESSION_MAP[53] = compression;
    }

    private enum ZstdCompressionTypes {
        ZSTD(Compression.ZSTD),
        ZSTD_WITH_FINALIZER(BluemapCompat.ZSTD_WITH_FINALIZER),
        ZSTD_NO_FINALIZER(BluemapCompat.ZSTD_NO_FINALIZER);

        private final Compression compression;

        ZstdCompressionTypes(Compression compression) {
            this.compression = compression;
        }
    }
}
