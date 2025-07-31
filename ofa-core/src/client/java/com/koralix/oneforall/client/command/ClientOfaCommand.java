package com.koralix.oneforall.client.command;

import com.koralix.oneforall.client.config.impl.ClientConfigValue;
import com.koralix.oneforall.command.OfaCommand;
import com.koralix.oneforall.config.ConfigValue;
import com.koralix.oneforall.entry.OneForAll;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClientOfaCommand {
    public static void register(
            @NotNull CommandDispatcher<FabricClientCommandSource> dispatcher,
            @NotNull CommandRegistryAccess registryAccess
    ) {
        LiteralArgumentBuilder<FabricClientCommandSource> ofa = ClientCommandManager.literal("cofa").then(
                ClientCommandManager.literal("settings")
                        .then(client(ClientCommandManager.literal("client")))
        );

        dispatcher.register(ofa);
    }

    private static @NotNull LiteralArgumentBuilder<FabricClientCommandSource> client(
            @NotNull LiteralArgumentBuilder<FabricClientCommandSource> root
    ) {
        ClientConfigValue.REGISTRY.registry.forEach(configValue -> client(root, configValue));
        return root;
    }

    private static <T, B> LiteralArgumentBuilder<FabricClientCommandSource> client(
            @NotNull LiteralArgumentBuilder<FabricClientCommandSource> root,
            ConfigValue<MinecraftClient, T, B> configValue
    ) {
        return OfaCommand.create(
                configValue,
                root,
                context -> context.getSource().getClient(),
                ClientOfaCommand::get,
                ClientOfaCommand::set
        );
    }

    private static <K, T, B> int get(
            @NotNull CommandContext<FabricClientCommandSource> context,
            @NotNull ConfigValue<K, T, B> configValue,
            @NotNull K backend
    ) {
        T value = configValue.get(backend);
        context.getSource().sendFeedback(Text.stringifiedTranslatable(
                "command." + OneForAll.MOD_ID + ".config.get",
                Text.translatable(configValue.translationKey() + ".name"),
                value.toString()
        ));
        return 1;
    }

    private static <K, T, B> int set(
            @NotNull CommandContext<FabricClientCommandSource> context,
            @NotNull ConfigValue<K, T, B> configValue,
            @NotNull K backend,
            @Nullable T value
    ) {
        if (configValue.set(backend, value)) {
            context.getSource().sendFeedback(Text.stringifiedTranslatable(
                    "command." + OneForAll.MOD_ID + ".config." + (value == null ? "reset" : "set"),
                    Text.translatable(configValue.translationKey() + ".name"),
                    configValue.get(backend).toString()
            ));
        } else {
            context.getSource().sendError(Text.translatable("command." + OneForAll.MOD_ID + ".config.set.fail",
                    Text.translatable(configValue.translationKey() + ".name"),
                    value.toString()
            ));
            return 0;
        }
        return 1;
    }
}
