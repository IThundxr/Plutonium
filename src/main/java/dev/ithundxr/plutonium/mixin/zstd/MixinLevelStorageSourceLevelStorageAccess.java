package dev.ithundxr.plutonium.mixin.zstd;

import com.mojang.datafixers.DataFixer;
import dev.ithundxr.plutonium.mixinsupport.ZstdPlayerDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelStorageSource.LevelStorageAccess.class)
public class MixinLevelStorageSourceLevelStorageAccess {
    @Redirect(method = "createPlayerStorage", at = @At(value = "NEW", target = "net/minecraft/world/level/storage/PlayerDataStorage"))
    public PlayerDataStorage plutonium$useZstd(LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer) {
        return new ZstdPlayerDataStorage(levelStorageAccess, dataFixer);
    }
}
