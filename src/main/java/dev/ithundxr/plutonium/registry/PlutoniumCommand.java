package dev.ithundxr.plutonium.registry;

import com.mojang.brigadier.CommandDispatcher;
import dev.ithundxr.plutonium.command.ZstdRatioCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class PlutoniumCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("plutonium")
                .then(
                        Commands.literal("server").then(ZstdRatioCommand.register())
                )
        );
    }
}
