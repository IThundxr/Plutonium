package dev.ithundxr.plutonium;

import com.chocohead.mm.api.ClassTinkerers;
import com.github.luben.zstd.ZstdInputStreamNoFinalizer;
import com.github.luben.zstd.ZstdOutputStreamNoFinalizer;
import de.bluecolored.shadow.querz.nbt.mca.ExceptionFunction;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        if (FabricLoader.getInstance().isModLoaded("bluemap")) {
        ClassTinkerers.enumBuilder("de.bluecolored.shadow.querz.nbt.mca.CompressionType", int.class, ExceptionFunction.class, ExceptionFunction.class)
                .addEnum("ZSTD", () -> { // wrap up safely to prevent premature classloading
                    Supplier<Supplier<Object[]>> supplier = (() -> () ->
                            new Object[] {
                                    53,
                                    // Haven't done manual review, but Bluemap would be insane *not* to close these streams
                                    (ExceptionFunction<OutputStream, ? extends OutputStream, IOException>) ZstdOutputStreamNoFinalizer::new,
                                    (ExceptionFunction<InputStream, ? extends InputStream, IOException>) ZstdInputStreamNoFinalizer::new
                            }
                    );
                    return supplier.get().get();
                }).build();
        }
    }
}
