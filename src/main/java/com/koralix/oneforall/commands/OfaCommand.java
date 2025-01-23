package com.koralix.oneforall.commands;

import com.koralix.oneforall.settings.ConfigValue;
import com.koralix.oneforall.settings.ConfigValueAdapter;
import com.koralix.oneforall.settings.ConfigValueView;
import com.koralix.oneforall.settings.SettingsManager;
import com.koralix.oneforall.settings.registry.ConfigValueRegistry;
import com.koralix.oneforall.utils.IntoText;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class OfaCommand {
    private OfaCommand() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    @FunctionalInterface
    public interface SendFeedback<S> {
        void send(S source, Text message, boolean ops);
    }

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment
    ) {
        LiteralArgumentBuilder<ServerCommandSource> ofa = CommandManager.literal("ofa");

        ofa.then(settings(
                CommandManager::literal,
                CommandManager::argument,
                registry -> registry.environment().server(),
                (source, message, ops) -> source.sendFeedback(() -> message, ops),
                ServerCommandSource::sendError
        ));
        dispatcher.register(ofa);
    }

    public static <S extends CommandSource, A> LiteralArgumentBuilder<S> settings(
            Function<String, LiteralArgumentBuilder<S>> literal,
            ConfigValueAdapter.Command.ArgumentFactory<S, A> argument,
            Predicate<ConfigValueRegistry> predicate,
            SendFeedback<S> sendFeedback,
            BiConsumer<S, Text> sendError
    ) {
        LiteralArgumentBuilder<S> settings = literal.apply("settings");
        LiteralArgumentBuilder<S> def = literal.apply("default");
        settings.then(def);

        SettingsManager.forEach(predicate, configValue -> {
            @SuppressWarnings("unchecked")
            ConfigValueAdapter.Command<?, A> command = (ConfigValueAdapter.Command<?, A>) configValue.command();

            LiteralArgumentBuilder<S> setting = setting(literal, argument, configValue, command, sendFeedback, sendError, true, arg -> {
                arg.executes(context -> set(context, configValue, sendFeedback, sendError));
            });
            settings.then(setting);

            setting = setting(literal, argument, configValue, command, sendFeedback, sendError, false, arg -> {
                arg.executes(context -> setDefault(context, configValue, sendFeedback, sendError));
            });
            def.then(setting);
        });

        return settings;
    }

    private static <S extends CommandSource, A> LiteralArgumentBuilder<S> setting(
            Function<String, LiteralArgumentBuilder<S>> literal,
            ConfigValueAdapter.Command.ArgumentFactory<S, A> argument,
            ConfigValue<?> configValue,
            ConfigValueAdapter.Command<?, A> command,
            SendFeedback<S> sendFeedback,
            BiConsumer<S, Text> sendError,
            boolean status,
            Consumer<RequiredArgumentBuilder<S, A>> consumer
    ) {
        LiteralArgumentBuilder<S> setting = literal.apply(configValue.toString());
        RequiredArgumentBuilder<S, A> arg = command.argument(argument);
        consumer.accept(arg);
        setting.then(arg);
        if (status) setting.executes(context -> status(context, configValue, sendFeedback, sendError));
        return setting;
    }

    private static <S extends CommandSource, T> int status(
            CommandContext<S> context,
            ConfigValue<T> configValue,
            SendFeedback<S> sendFeedback,
            BiConsumer<S, Text> sendError
    ) {
        ConfigValueView<T> view = configValue.view(context);
        Text nominal = IntoText.into(configValue.nominalValue());
        Text def = IntoText.into(view.defaultValue());
        Text value = IntoText.into(view.value());
        sendFeedback.send(
                context.getSource(),
                Text.translatable("commands.oneforall.settings.status", configValue.toText(), nominal, def, value),
                false
        );
        return 1;
    }

    private static <S extends CommandSource, T> int set(
            CommandContext<S> context,
            ConfigValue<T> configValue,
            SendFeedback<S> sendFeedback,
            BiConsumer<S, Text> sendError
    ) {
        ConfigValueView<T> view = configValue.view(context);
        T value = configValue.command().from(context);
        Optional<Text> error = view.value(value);
        if (error.isPresent()) {
            sendError.accept(context.getSource(), error.get());
            return 0;
        }
        sendFeedback.send(
                context.getSource(),
                Text.translatable("commands.oneforall.settings.set", configValue.toText(), IntoText.into(value)),
                true
        );
        return 1;
    }

    private static <S extends CommandSource, T> int setDefault(
            CommandContext<S> context,
            ConfigValue<T> configValue,
            SendFeedback<S> sendFeedback,
            BiConsumer<S, Text> sendError
    ) {
        ConfigValueView<T> view = configValue.view(context);
        T value = configValue.command().from(context);
        Optional<Text> error = view.defaultValue(value);
        if (error.isPresent()) {
            sendError.accept(context.getSource(), error.get());
            return 0;
        }
        sendFeedback.send(
                context.getSource(),
                Text.translatable("commands.oneforall.settings.set_default", configValue.toText(), IntoText.into(value)),
                true
        );
        return 1;
    }
}
