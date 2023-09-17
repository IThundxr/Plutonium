package dev.ithundxr.plutonium.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.ithundxr.plutonium.utils.ZstdStats;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
public class ZstdRatioCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("zstdRatio").executes(ctx -> {
            ctx.getSource().sendFeedback(Component.literal("Zstd ratio past 5m: " + ZstdStats.getRatio()));
            return 1;
        });
    }
}