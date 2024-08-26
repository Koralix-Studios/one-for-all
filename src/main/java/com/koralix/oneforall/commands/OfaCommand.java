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
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class OfaCommand {
    private OfaCommand() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static void register(
            @NotNull CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment
    ) {
        LiteralArgumentBuilder<ServerCommandSource> ofa = CommandManager.literal("ofa");
        ofa.then(settings(CommandManager::literal, CommandManager::argument, SettingsRegistry.Env.SERVER));
        dispatcher.register(ofa);
    }

    public static <S extends CommandSource> LiteralArgumentBuilder<S> settings(
            @NotNull Function<String, LiteralArgumentBuilder<S>> ofaLiteral,
            @NotNull BiFunction<String, ArgumentType<?>, RequiredArgumentBuilder<S, ?>> ofaArgument,
            SettingsRegistry.Env @NotNull ... envs
    ) {
        LiteralArgumentBuilder<S> settings = ofaLiteral.apply("settings").executes(OfaCommand::listSettings);
        LiteralArgumentBuilder<S> def = ofaLiteral.apply("default");
        LiteralArgumentBuilder<S> restore = ofaLiteral.apply("restore");
        for (SettingsRegistry.Env env : envs) {
            SettingsManager.forEach(env, (entry, configValue) -> {
                settings.then(setting(ofaLiteral, ofaArgument, entry, def, restore));
            });
        }
        settings.then(def);
        settings.then(restore);
        return settings;
    }

    private static <S extends CommandSource, T> LiteralArgumentBuilder<S> setting(
            @NotNull Function<String, LiteralArgumentBuilder<S>> ofaLiteral,
            @NotNull BiFunction<String, ArgumentType<?>, RequiredArgumentBuilder<S, ?>> ofaArgument,
            @NotNull SettingEntry<T> entry,
            @NotNull LiteralArgumentBuilder<S> def,
            @NotNull LiteralArgumentBuilder<S> restore
    ) {
        def.then(ofaLiteral.apply(entry.id().toString())
                .requires(source -> !(source instanceof ServerCommandSource scs) || entry.setting().permission(scs))
                .executes(context -> setSetting(context, entry))
                .then(settingArgument(ofaArgument, entry.setting())
                        .executes(context -> setDefaultSetting(context, entry))));
        restore
                .requires(source -> !(source instanceof ServerCommandSource scs) || entry.setting().permission(scs))
                .executes(context -> setDefaultSetting(context, entry));
        return ofaLiteral.apply(entry.id().toString())
                .executes(context -> getSetting(context, entry))
                .then(settingArgument(ofaArgument, entry.setting())
                        .executes(context -> setSetting(context, entry)));
    }

    private static <S extends CommandSource> int listSettings(@NotNull CommandContext<S> context) {
        int[] result = {0}; // int* result;
        SettingsManager.forEach(SettingsRegistry.Env.SERVER, (entry, configValue) ->
                result[0] += getSetting(context, entry)
        );
        return result[0];
    }

    private static <S extends CommandSource> int getSetting(
            @NotNull CommandContext<S> context,
            @NotNull SettingEntry<?> entry
    ) {
        if (context.getSource() instanceof ServerCommandSource scs && !entry.setting().permission(scs)) return 0;

        Method method = null;
        final Text text = Text.translatable(entry.translation())
                .append(":\n")
                .append("Value: ")
                .append(entry.setting().value().toString())
                .append("\n")
                .append("Default: ")
                .append(entry.setting().defaultValue().toString())
                .append("\n")
                .append("Nominal: ")
                .append(entry.setting().nominalValue().toString());
        Object arg1 = text;
        try {
            method = context.getSource().getClass().getDeclaredMethod("sendFeedback", Supplier.class, boolean.class);
            arg1 = (Supplier<?>) () -> text;
            method.invoke(context.getSource(), arg1, false);
        } catch (NoSuchMethodException e1) {
            try {
                method = context.getSource().getClass().getDeclaredMethod("sendFeedback", Text.class);
                method.invoke(context.getSource(), arg1);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e2) {
                throw new UnsupportedOperationException("Unsupported command source: " + context.getSource().getClass().getName());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new UnsupportedOperationException("Failed to invoke method: " + method.getName());
        }

        return 1;
    }

    private static <S extends CommandSource, T> int setSetting(
            @NotNull CommandContext<S> context,
            @NotNull SettingEntry<T> entry
    ) {
        try {
            T value = context.getArgument("value", entry.setting().clazz());
            Text text = entry.setting().value(value);
            if (text != null && context.getSource() instanceof ServerCommandSource scs) {
                scs.sendError(text);
                return 0;
            }
        } catch (IllegalArgumentException e) {
            entry.setting().reset();
        }

        return 1;
    }

    private static <S extends CommandSource, T> int setDefaultSetting(
            @NotNull CommandContext<S> context,
            @NotNull SettingEntry<T> entry
    ) {
        try {
            T value = context.getArgument("value", entry.setting().clazz());
            Text text = entry.setting().defaultValue(value);
            if (text != null && context.getSource() instanceof ServerCommandSource scs) {
                scs.sendError(text);
                return 0;
            }
        } catch (IllegalArgumentException e) {
            entry.setting().restore();
        }

        return 1;
    }

    @SuppressWarnings("unchecked")
    private static <S extends CommandSource, T> @NotNull RequiredArgumentBuilder<S, T> settingArgument(
            @NotNull BiFunction<String, ArgumentType<?>, RequiredArgumentBuilder<S, ?>> ofaArgument,
            @NotNull ConfigValue<T> setting
    ) {
        return (RequiredArgumentBuilder<S, T>) ofaArgument.apply("value", settingArgumentType(setting.clazz()));
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
