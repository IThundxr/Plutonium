package dev.ithundxr.plutonium;

import dev.ithundxr.plutonium.compat.BluemapCompat;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class PlutoniumDedicatedServerModInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        if (FabricLoader.getInstance().isModLoaded("bluemap")) {
            BluemapCompat.init();
        }
    }
}
