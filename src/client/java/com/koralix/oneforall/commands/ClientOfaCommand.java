package com.koralix.oneforall.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public final class ClientOfaCommand {
    private ClientOfaCommand() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static void register(
            CommandDispatcher<FabricClientCommandSource> dispatcher,
            CommandRegistryAccess registryAccess
    ) {
        LiteralArgumentBuilder<FabricClientCommandSource> cofa = ClientCommandManager.literal("cofa");
        cofa.then(OfaCommand.settings(
                ClientCommandManager::literal,
                ClientCommandManager::argument,
                registry -> registry.environment().server(),
                (source, message, ops) -> source.sendFeedback(message),
                FabricClientCommandSource::sendError
        ));
        dispatcher.register(cofa);
    }
}
