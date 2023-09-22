package dev.ithundxr.plutonium.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import dev.ithundxr.plutonium.utils.ZstdStats;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
public class ZstdRatioCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> registerClient() {
        return ClientCommandManager.literal("zstdRatio").executes(ctx -> {
            ctx.getSource().sendFeedback(Component.literal("Zstd ratio past 5m: " + ZstdStats.getRatio()));
            return 1;
        });
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("zstdRatio").executes(ctx -> {
            ctx.getSource().sendSuccess(() -> Component.literal("Zstd ratio past 5m: " + ZstdStats.getRatio()), true);
            return 1;
        });
    }
}