package com.koralix.oneforall.commands;

import com.koralix.oneforall.settings.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.network.PacketByteBuf;
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
        ofa.then(settings(CommandManager::literal, CommandManager::argument, ConfigValueAccessor::server, SettingsRegistry.Env.SERVER));
        dispatcher.register(ofa);
    }

    public static <S extends CommandSource> LiteralArgumentBuilder<S> settings(
            @NotNull Function<String, LiteralArgumentBuilder<S>> ofaLiteral,
            @NotNull BiFunction<String, ArgumentType<?>, RequiredArgumentBuilder<S, ?>> ofaArgument,
            @NotNull ConfigValueAccessorFactory<S> factory,
            SettingsRegistry.Env @NotNull ... envs
    ) {
        LiteralArgumentBuilder<S> settings = ofaLiteral.apply("settings").executes(context -> listSettings(context, factory));
        LiteralArgumentBuilder<S> def = ofaLiteral.apply("default");
        LiteralArgumentBuilder<S> restore = ofaLiteral.apply("restore");
        for (SettingsRegistry.Env env : envs) {
            SettingsManager.forEach(env, (entry, configValue) ->
                    settings.then(setting(ofaLiteral, ofaArgument, entry, def, restore, factory))
            );
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
            @NotNull LiteralArgumentBuilder<S> restore,
            @NotNull ConfigValueAccessorFactory<S> factory
    ) {
        ConfigValueAccessor<S, T> accessor = factory.create(entry);

        def.then(ofaLiteral.apply(entry.id().toString())
                .requires(source -> !(source instanceof ServerCommandSource scs) || entry.setting().permission(scs))
                .executes(context -> setSetting(context, entry, accessor))
                .then(ArgumentTypeHelper.createSettingArg(ofaArgument, entry.setting())
                        .executes(context -> setDefaultSetting(context, entry, accessor))));
        restore
                .requires(source -> !(source instanceof ServerCommandSource scs) || entry.setting().permission(scs))
                .executes(context -> setDefaultSetting(context, entry, accessor));
        return ofaLiteral.apply(entry.id().toString())
                .requires(source -> !(source instanceof ServerCommandSource scs) || entry.setting().permission(scs))
                .executes(context -> getSetting(context, entry, accessor))
                .then(ArgumentTypeHelper.createSettingArg(ofaArgument, entry.setting())
                        .executes(context -> setSetting(context, entry, accessor)))
                .then(ofaLiteral.apply("test").executes(context -> {
                    PacketByteBuf buf = PacketByteBufs.create();
                    entry.setting().serialize(buf);
                    entry.setting().deserialize(buf);
                    return 1;
                }));
    }

    private static <S extends CommandSource> int listSettings(
            @NotNull CommandContext<S> context,
            @NotNull ConfigValueAccessorFactory<S> factory
    ) {
        int[] result = {0}; // int* result;
        SettingsManager.forEach(SettingsRegistry.Env.SERVER, (entry, configValue) -> {
            result[0] += getSetting(context, entry, factory.create(entry));
        });
        return result[0];
    }

    private static <S extends CommandSource> int getSetting(
            @NotNull CommandContext<S> context,
            @NotNull SettingEntry<?> entry,
            @NotNull ConfigValueAccessor<S, ?> accessor
    ) {
        if (context.getSource() instanceof ServerCommandSource scs && !entry.setting().permission(scs)) return 0;

        Method method = null;
        final Text text = Text.translatable(entry.translation())
                .append(":\n")
                .append("Value: ")
                .append(accessor.get(context.getSource()).toString())
                .append("\n")
                .append("Default: ")
                .append(accessor.getDefault(context.getSource()).toString())
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
            @NotNull SettingEntry<T> entry,
            @NotNull ConfigValueAccessor<S, T> accessor
    ) {
        try {
            T value = ArgumentTypeHelper.getSettingValue(context, "value", entry.setting());
            Text text = accessor.set(context.getSource(), value);
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
            @NotNull SettingEntry<T> entry,
            @NotNull ConfigValueAccessor<S, T> accessor
    ) {
        try {
            T value = ArgumentTypeHelper.getSettingValue(context, "value", entry.setting());
            Text text = accessor.setDefault(context.getSource(), value);
            if (text != null && context.getSource() instanceof ServerCommandSource scs) {
                scs.sendError(text);
                return 0;
            }
        } catch (IllegalArgumentException e) {
            entry.setting().restore();
        }

        return 1;
    }
}
