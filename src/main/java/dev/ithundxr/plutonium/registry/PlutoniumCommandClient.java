package dev.ithundxr.plutonium.registry;

import com.mojang.brigadier.CommandDispatcher;
import dev.ithundxr.plutonium.command.ZstdRatioCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class PlutoniumCommandClient {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("plutonium")
            .then(
                ClientCommandManager.literal("client").then(ZstdRatioCommand.registerClient())
            )
        );
    }
}
