package com.koralix.oneforall.commands;

import com.koralix.oneforall.settings.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.jetbrains.annotations.NotNull;

import static com.koralix.oneforall.commands.OfaCommand.settings;

public final class ClientOfaCommand {
    private ClientOfaCommand() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }


    public static void register(
            @NotNull CommandDispatcher<FabricClientCommandSource> dispatcher,
            CommandRegistryAccess registryAccess
    ) {
        LiteralArgumentBuilder<FabricClientCommandSource> cofa = ClientCommandManager.literal("cofa");
        cofa.then(settings(ClientCommandManager::literal, ClientCommandManager::argument, ClientOfaCommand::client, SettingsRegistry.Env.CLIENT));
        dispatcher.register(cofa);
    }

    static <T> ConfigValueAccessor<FabricClientCommandSource, T> client(ConfigValue<T> config) {
        if (config instanceof ConfigValueWrapper<T> serverConfig) {
            return new ClientSettingAccessor<>(serverConfig);
        } else {
            throw new IllegalArgumentException("Config value is not a server or player config value");
        }
    }
}
