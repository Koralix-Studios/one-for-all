package com.koralix.oneforall.command;

import com.koralix.oneforall.config.ConfigActor;
import com.koralix.oneforall.config.ConfigCommandAdapter;
import com.koralix.oneforall.config.ConfigValue;
import com.koralix.oneforall.config.impl.PlayerConfigValue;
import com.koralix.oneforall.config.impl.ServerConfigValue;
import com.koralix.oneforall.entry.OneForAll;
import com.koralix.oneforall.session.Session;
import com.koralix.oneforall.session.SessionHolder;
import com.koralix.oneforall.util.Functions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OfaCommand {
    private OfaCommand() {
        // Prevent instantiation
    }

    public static void register(
            @NotNull CommandDispatcher<ServerCommandSource> dispatcher,
            @NotNull CommandRegistryAccess registryAccess,
            @NotNull CommandManager.RegistrationEnvironment environment
    ) {
        LiteralArgumentBuilder<ServerCommandSource> ofa = CommandManager.literal("ofa").then(
                CommandManager.literal("settings")
                        .then(server(CommandManager.literal("server")))
                        .then(player(CommandManager.literal("player")))
        );

        dispatcher.register(ofa);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> server(
            @NotNull LiteralArgumentBuilder<ServerCommandSource> root
    ) {
        ServerConfigValue.REGISTRY.registry.forEach(configValue -> server(root, configValue));
        return root;
    }

    private static <T, B> LiteralArgumentBuilder<ServerCommandSource> server(
            @NotNull LiteralArgumentBuilder<ServerCommandSource> root,
            @NotNull ConfigValue<MinecraftServer, T, B> configValue
    ) {
        return create(
                configValue,
                root,
                context -> context.getSource().getServer(),
                OfaCommand::get,
                OfaCommand::set
        );
    }

    private static LiteralArgumentBuilder<ServerCommandSource> player(
            @NotNull LiteralArgumentBuilder<ServerCommandSource> root
    ) {
        PlayerConfigValue.REGISTRY.registry.forEach(configValue -> root.then(player(
                configValue,
                LiteralArgumentBuilder.literal("self"),
                true
        )));
        LiteralArgumentBuilder<ServerCommandSource> other = LiteralArgumentBuilder
                .literal("other");
        PlayerConfigValue.REGISTRY.registry.forEach(configValue -> other.then(player(
                configValue,
                RequiredArgumentBuilder.argument("player", EntityArgumentType.player()),
                false
        )));
        return root.then(other);
    }

    private static <A extends ArgumentBuilder<ServerCommandSource, A>, T, B> A player(
            @NotNull ConfigValue<ServerPlayerEntity, T, B> configValue,
            @NotNull A root,
            boolean self
    ) {
        return create(
                configValue,
                root,
                self
                        ? context -> context.getSource().getPlayerOrThrow()
                        : context -> EntityArgumentType.getPlayer(context, "player"),
                OfaCommand::get,
                OfaCommand::set
        );
    }

    public static <S extends CommandSource, A extends ArgumentBuilder<S, A>, K, T, B> A create(
            @NotNull ConfigValue<K, T, B> configValue,
            @NotNull A root,
            @NotNull Functions.FallibleFunction<CommandContext<S>, K, CommandSyntaxException> backendGetter,
            @NotNull ConfigCommandAdapter.Getter<S, K, T, B> getter,
            @NotNull ConfigCommandAdapter.Setter<S, K, T, B> setter
    ) {
        LiteralArgumentBuilder<S> entry = LiteralArgumentBuilder.literal(configValue.id().toString());

        entry = entry
                .requires(source -> configValue.test().read((ConfigActor) source))
                .executes(context -> getter.get(context, configValue, backendGetter.apply(context)));

        configValue.codec().commandAdapter().adapt(
                "value",
                entry,
                (argument, arg) -> {
                    argument
                            .requires(source -> configValue.test().write((ConfigActor) source))
                            .executes(context -> setter.set(context, configValue, backendGetter.apply(context), arg.get(context, "value")));
                }
        );

        return root.then(entry);
    }

    private static <K, T, B> int get(
            @NotNull CommandContext<ServerCommandSource> context,
            @NotNull ConfigValue<K, T, B> configValue,
            @NotNull K backend
    ) {
        T value = configValue.get(backend);
        context.getSource().sendFeedback(() -> Text.stringifiedTranslatable(
                "command." + OneForAll.MOD_ID + ".config.get",
                Text.translatable(configValue.translationKey() + ".name"),
                value.toString()
        ), false);
        return 1;
    }

    private static <K, T, B> int set(
            @NotNull CommandContext<ServerCommandSource> context,
            @NotNull ConfigValue<K, T, B> configValue,
            @NotNull K backend,
            @Nullable T value
    ) {
        if (configValue.set(backend, value)) {
            context.getSource().sendFeedback(() -> Text.stringifiedTranslatable(
                    "command." + OneForAll.MOD_ID + ".config." + (value == null ? "reset" : "set"),
                    Text.translatable(configValue.translationKey() + ".name"),
                    configValue.get(backend).toString()
            ), true);
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
