package com.koralix.oneforall.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public final class Commands {
    private Commands() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment
    ) {
        common(dispatcher, registryAccess, environment);
        if (environment.integrated) integrated(dispatcher, registryAccess);
        else if (environment.dedicated) dedicated(dispatcher, registryAccess);
        else throw new UnsupportedOperationException("Unknown environment");
    }

    private static void integrated(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess
    ) {
        // Register integrated commands here
    }

    private static void dedicated(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess
    ) {
        // Register dedicated commands here
    }

    private static void common(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment
    ) {
        OfaCommand.register(dispatcher, registryAccess, environment);
    }
}
