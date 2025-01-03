package com.koralix.oneforall.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public final class ClientCommands {
    private ClientCommands() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }


    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        ClientOfaCommand.register(dispatcher, registryAccess);
    }
}
