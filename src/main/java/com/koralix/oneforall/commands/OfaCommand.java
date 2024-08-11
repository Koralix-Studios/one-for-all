package com.koralix.oneforall.commands;

import com.koralix.oneforall.settings.ConfigValue;
import com.koralix.oneforall.settings.SettingEntry;
import com.koralix.oneforall.settings.SettingsManager;
import com.koralix.oneforall.settings.SettingsRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class OfaCommand {
    private OfaCommand() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment
    ) {
        LiteralArgumentBuilder<ServerCommandSource> ofa = literal("ofa");
        ofa.then(settings());
        dispatcher.register(ofa);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> settings() {
        LiteralArgumentBuilder<ServerCommandSource> settings = literal("settings").executes(OfaCommand::listSettings);
        SettingsManager.forEach(SettingsRegistry.Env.SERVER, (entry, configValue) ->
                settings.then(setting(entry))
        );
        return settings;
    }

    private static <T> LiteralArgumentBuilder<ServerCommandSource> setting(@NotNull SettingEntry<T> entry) {
        return literal(entry.id().toString())
                .executes(context -> getSetting(context, entry))
                .then(settingArgument(entry.setting())
                        .executes(context -> setSetting(context, entry)))
                .then(literal("default").then(settingArgument(entry.setting())
                        .executes(context -> setDefaultSetting(context, entry))))
                .then(literal("reset")
                        .executes(context -> setSetting(context, entry)))
                .then(literal("restore")
                        .executes(context -> setDefaultSetting(context, entry)));
    }

    private static int listSettings(@NotNull CommandContext<ServerCommandSource> context) {
        int[] result = {0}; // int* result;
        SettingsManager.forEach(SettingsRegistry.Env.SERVER, (entry, configValue) ->
                result[0] += getSetting(context, entry)
        );
        return result[0];
    }

    private static int getSetting(
            @NotNull CommandContext<ServerCommandSource> context,
            @NotNull SettingEntry<?> entry
    ) {
        context.getSource().sendFeedback(
                () -> Text.translatable(entry.translation())
                        .append(":\n")
                        .append("Value: ")
                        .append(entry.setting().value().toString())
                        .append("\n")
                        .append("Default: ")
                        .append(entry.setting().defaultValue().toString())
                        .append("\n")
                        .append("Nominal: ")
                        .append(entry.setting().nominalValue().toString()),
                false
        );
        return 1;
    }

    private static <T> int setSetting(
            @NotNull CommandContext<ServerCommandSource> context,
            @NotNull SettingEntry<T> entry
    ) {
        try {
            T value = context.getArgument("value", entry.setting().clazz());
            entry.setting().value(value);
        } catch (IllegalArgumentException e) {
            entry.setting().reset();
        }

        return 1;
    }

    private static <T> int setDefaultSetting(
            @NotNull CommandContext<ServerCommandSource> context,
            @NotNull SettingEntry<T> entry
    ) {
        try {
            T value = context.getArgument("value", entry.setting().clazz());
            entry.setting().defaultValue(value);
        } catch (IllegalArgumentException e) {
            entry.setting().restore();
        }

        return 1;
    }

    private static <T> @NotNull RequiredArgumentBuilder<ServerCommandSource, T> settingArgument(
            @NotNull ConfigValue<T> setting
    ) {
        return argument("value", settingArgumentType(setting.clazz()));
    }

    @SuppressWarnings("unchecked")
    private static <T> @NotNull ArgumentType<T> settingArgumentType(@NotNull Class<T> clazz) {
        if (clazz.isEnum())
            return (ArgumentType<T>) DynEnumArgumentType.create(clazz);
        if (clazz == Boolean.class)
            return (ArgumentType<T>) BoolArgumentType.bool();
        if (clazz == String.class)
            return (ArgumentType<T>) StringArgumentType.string();
        if (clazz == Byte.class)
            return (ArgumentType<T>) IntegerArgumentType.integer(Byte.MIN_VALUE, Byte.MAX_VALUE);
        if (clazz == Short.class)
            return (ArgumentType<T>) IntegerArgumentType.integer(Short.MIN_VALUE, Short.MAX_VALUE);
        if (clazz == Integer.class)
            return (ArgumentType<T>) IntegerArgumentType.integer();
        if (clazz == Long.class)
            return (ArgumentType<T>) LongArgumentType.longArg();
        if (clazz == Float.class)
            return (ArgumentType<T>) FloatArgumentType.floatArg();
        if (clazz == Double.class)
            return (ArgumentType<T>) DoubleArgumentType.doubleArg();
        throw new UnsupportedOperationException("Unsupported setting type: " + clazz);
    }
}
