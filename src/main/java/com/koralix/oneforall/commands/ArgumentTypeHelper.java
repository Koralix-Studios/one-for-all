package com.koralix.oneforall.commands;

import com.koralix.oneforall.settings.ConfigValue;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class ArgumentTypeHelper {
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>, S extends CommandSource> RequiredArgumentBuilder<S, String> createEnumArg(
            @NotNull BiFunction<String, ArgumentType<?>, RequiredArgumentBuilder<S, ?>> argument,
            String name,
            T[] values
    ) {
        return (RequiredArgumentBuilder<S, String>) argument.apply(name, StringArgumentType.word()).suggests((context, builder) -> {
            for (T value : values) {
                builder.suggest(value.name().toLowerCase());
            }
            return builder.buildFuture();
        });
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>, S extends CommandSource> @NotNull RequiredArgumentBuilder<S, String> createEnumArg(
            @NotNull BiFunction<String, ArgumentType<?>, RequiredArgumentBuilder<S, ?>> argument,
            String name,
            @NotNull Class<?> clazz
    ) {
        return createEnumArg(argument, name, (T[]) clazz.getEnumConstants());
    }

    public static <T extends Enum<T>, S extends CommandSource> T getEnumValue(
            @NotNull CommandContext<S> context,
            @NotNull String name,
            @NotNull Class<T> clazz
    ) {
        return Enum.valueOf(clazz, StringArgumentType.getString(context, name).toUpperCase());
    }

    @SuppressWarnings("unchecked")
    public static <S extends CommandSource, T> @NotNull RequiredArgumentBuilder<S, T> createSettingArg(
            @NotNull BiFunction<String, ArgumentType<?>, RequiredArgumentBuilder<S, ?>> ofaArgument,
            @NotNull ConfigValue<T> setting
    ) {
        if (setting.clazz().isEnum())
            return (RequiredArgumentBuilder<S, T>) createEnumArg(ofaArgument, "value", setting.clazz());

        return (RequiredArgumentBuilder<S, T>) ofaArgument.apply("value", settingArgumentType(setting.clazz()));
    }

    public static <S extends CommandSource, T> T getSettingValue(
            @NotNull CommandContext<S> context,
            @NotNull String name,
            @NotNull ConfigValue<T> setting
    ) {
        if (setting.clazz().isEnum()) {
            Class<? extends Enum> clazz = setting.clazz().asSubclass(Enum.class);
            return (T) getEnumValue(context, name, clazz);
        }

        return context.getArgument(name, setting.clazz());
    }

    @SuppressWarnings("unchecked")
    private static <T> @NotNull ArgumentType<T> settingArgumentType(@NotNull Class<T> clazz) {
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
