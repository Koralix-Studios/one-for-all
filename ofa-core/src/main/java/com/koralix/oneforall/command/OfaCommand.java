package com.koralix.oneforall.command;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.*;
import com.koralix.oneforall.config.adapter.CommandAdapter;
import com.koralix.oneforall.config.feature.FeatureRegistry;
import com.koralix.oneforall.config.registry.ConfigEntry;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class OfaCommand {
    private static final DynamicCommandExceptionType INVALID_CONFIG_REGISTRY_EXCEPTION = new DynamicCommandExceptionType(
            key -> Text.stringifiedTranslatable("command." + OneForAll.id() + ".invalid_config_registry", key)
    );
    private static final DynamicCommandExceptionType UNKNOWN_CONFIG_EXCEPTION = new DynamicCommandExceptionType(
            key -> Text.stringifiedTranslatable("command." + OneForAll.id() + ".unknown_config", key)
    );
    private static final DynamicCommandExceptionType INVALID_NBT_EXCEPTION = new DynamicCommandExceptionType(
            nbt -> Text.stringifiedTranslatable("command." + OneForAll.id() + ".invalid_nbt", nbt)
    );
    public static final DynamicCommandExceptionType CONFIG_TEST_EXCEPTION = new DynamicCommandExceptionType(
            text -> (Text) text
    );
    private static final SimpleCommandExceptionType INVALID_CONFIG_TYPE_EXCEPTION = new SimpleCommandExceptionType(
            Text.stringifiedTranslatable("command." + OneForAll.id() + ".config.unsupported_type")
    );

    private OfaCommand() {
        // Prevent instantiation
    }

    public static void register(
            @NotNull CommandDispatcher<ServerCommandSource> dispatcher,
            @NotNull CommandRegistryAccess registryAccess,
            @NotNull CommandManager.RegistrationEnvironment environment
    ) {
        LiteralArgumentBuilder<ServerCommandSource> ofa = literal("ofa");

        LiteralArgumentBuilder<ServerCommandSource> settings = literal("settings");
        ConfigRegistry.REGISTRY.forEach(registry -> {
            LiteralArgumentBuilder<ServerCommandSource> registryLiteral = literal(registry.id().toString());

            registry.forEach(entry -> populate(entry, registryLiteral));

            settings.then(registryLiteral);
        });
        ofa.then(settings);

        LiteralArgumentBuilder<ServerCommandSource> features = literal("features");
        FeatureRegistry.INSTANCE.forEach((id, feature) -> {
            LiteralArgumentBuilder<ServerCommandSource> featureLiteral = literal(id);
            feature.command(featureLiteral, (source, text, bool) -> source.sendFeedback(() -> text, bool));
            features.then(featureLiteral);
        });
        ofa.then(features);

        dispatcher.register(ofa);
    }

    private static <T> void populate(@NotNull ConfigEntry<T> entry, @NotNull LiteralArgumentBuilder<ServerCommandSource> registryLiteral) {
        LiteralArgumentBuilder<ServerCommandSource> entryLiteral = literal(entry.key().configId().toString());

        entry.configValue().commandAdapter().adapt(
                "value",
                entryLiteral,
                (argument, getter) -> {
                    if (PlayerConfigValue.class.isAssignableFrom(entry.configValue().getClass())) {
                        RequiredArgumentBuilder<ServerCommandSource, ?> builder = argument("target", EntityArgumentType.player());
                        argument.then(builder.executes(context -> setOther(context, entry, getter)));
                    }
                    argument.executes(context -> selfSet(context, entry, getter));
                });
        entryLiteral.executes(context -> selfGet(context, entry));
        if (PlayerConfigValue.class.isAssignableFrom(entry.configValue().getClass())) {
            RequiredArgumentBuilder<ServerCommandSource, ?> builder = argument("target", EntityArgumentType.player());
            entryLiteral.then(builder.executes(context -> getOther(context, entry)));
        }

        registryLiteral.then(entryLiteral);
    }

    private static <T> int getMono(ServerCommandSource source, @NotNull MonoConfigValue<T, ?, ?> configValue, ConfigActor actor) throws CommandSyntaxException {
        ConfigResult<T> result = configValue.value(actor);
        if (result.isError()) throw CONFIG_TEST_EXCEPTION.create(result.message());
        source.sendFeedback(() -> Text.stringifiedTranslatable("command." + OneForAll.id() + ".config.get", Text.stringifiedTranslatable(configValue.translationKey() + ".name"), result.get().orElseThrow().toString()), false);
        return 1;
    }

    private static <T> int getMono(@NotNull MonoConfigValue<T, ?, ?> configValue, ServerCommandSource source) throws CommandSyntaxException {
        return getMono(source, configValue, (ConfigActor) source);
    }

    private static <K, T> int getMulti(ServerCommandSource source, @NotNull MultiConfigValue<K, T, ?, ?> configValue, ConfigActor actor, K key) throws CommandSyntaxException {
        ConfigResult<T> result = configValue.value(actor, key);
        if (result.isError()) throw CONFIG_TEST_EXCEPTION.create(result.message());
        source.sendFeedback(() -> Text.stringifiedTranslatable("command." + OneForAll.id() + ".config.get", Text.stringifiedTranslatable(configValue.translationKey() + ".name"), result.get().orElseThrow().toString()), false);
        return 1;
    }

    private static <K, T> int getMulti(@NotNull MultiConfigValue<K, T, ?, ?> configValue, ServerCommandSource source, K key) throws CommandSyntaxException {
        return getMulti(source, configValue, (ConfigActor) source, key);
    }

    private static int selfGet(CommandContext<ServerCommandSource> context, @NotNull ConfigEntry<?> entry) throws CommandSyntaxException {
        ConfigValue<?, ?, ?> configValue = entry.configValue();
        if (configValue instanceof MonoConfigValue<?, ?, ?> monoConfigValue) {
            return getMono(monoConfigValue, context.getSource());
        } else if (PlayerConfigValue.class.isAssignableFrom(configValue.getClass())) {
            return getMulti((PlayerConfigValue<?, ?>) configValue, context.getSource(), context.getSource().getPlayerOrThrow().getUuid());
        }
        throw INVALID_CONFIG_TYPE_EXCEPTION.create();
    }

    private static int getOther(CommandContext<ServerCommandSource> context, @NotNull ConfigEntry<?> entry) throws CommandSyntaxException {
        ConfigValue<?, ?, ?> configValue = entry.configValue();
        if (configValue instanceof MonoConfigValue<?, ?, ?> monoConfigValue) {
            return getMono(monoConfigValue, context.getSource());
        } else if (PlayerConfigValue.class.isAssignableFrom(configValue.getClass())) {
            return getMulti((PlayerConfigValue<?, ?>) configValue, context.getSource(), EntityArgumentType.getPlayer(context, "target").getUuid());
        }
        throw INVALID_CONFIG_TYPE_EXCEPTION.create();
    }

    private static <T> int setMono(ServerCommandSource source, @NotNull MonoConfigValue<T, ?, ?> configValue, ConfigActor actor, T value) throws CommandSyntaxException {
        ConfigResult<T> result = configValue.value(actor, value);
        if (result.isError()) throw CONFIG_TEST_EXCEPTION.create(result.message());
        source.sendFeedback(() -> Text.of(result.message()), true);
        return result instanceof ConfigResult.ValidChange<T> ? 1 : 0;
    }

    private static <T> int setMono(@NotNull MonoConfigValue<T, ?, ?> configValue, ServerCommandSource source, T value) throws CommandSyntaxException {
        return setMono(source, configValue, (ConfigActor) source, value);
    }

    private static <K, T> int setMulti(ServerCommandSource source, @NotNull MultiConfigValue<K, T, ?, ?> configValue, ConfigActor actor, K key, T value) throws CommandSyntaxException {
        ConfigResult<T> result = configValue.value(actor, key, value);
        if (result.isError()) throw CONFIG_TEST_EXCEPTION.create(result.message());
        source.sendFeedback(() -> Text.of(result.message()), true);
        return result instanceof ConfigResult.ValidChange<T> ? 1 : 0;
    }

    private static <K, T> int setMulti(@NotNull MultiConfigValue<K, T, ?, ?> configValue, ServerCommandSource source, K key, T value) throws CommandSyntaxException {
        return setMulti(source, configValue, (ConfigActor) source, key, value);
    }

    @SuppressWarnings("unchecked")
    private static <T> int selfSet(CommandContext<ServerCommandSource> context, @NotNull ConfigEntry<T> entry, @NotNull CommandAdapter.CommandAdapterGetter<T> getter) throws CommandSyntaxException {
        ConfigValue<T, ?, ?> configValue = entry.configValue();
        T value = getter.get(context, "value");
        ServerCommandSource source = context.getSource();
        if (configValue instanceof MonoConfigValue<T, ?, ?> monoConfigValue) {
            return setMono(monoConfigValue, source, value);
        } else if (PlayerConfigValue.class.isAssignableFrom(configValue.getClass())) {
            return setMulti((PlayerConfigValue<T, ?>) configValue, source, source.getPlayerOrThrow().getUuid(), value);
        }
        throw INVALID_CONFIG_TYPE_EXCEPTION.create();
    }

    @SuppressWarnings("unchecked")
    private static <T> int setOther(CommandContext<ServerCommandSource> context, @NotNull ConfigEntry<T> entry, @NotNull CommandAdapter.CommandAdapterGetter<T> getter) throws CommandSyntaxException {
        ConfigValue<T, ?, ?> configValue = entry.configValue();
        T value = getter.get(context, "value");
        ServerCommandSource source = context.getSource();
        if (configValue instanceof MonoConfigValue<T, ?, ?> monoConfigValue) {
            return setMono(monoConfigValue, source, value);
        } else if (PlayerConfigValue.class.isAssignableFrom(configValue.getClass())) {
            return setMulti((PlayerConfigValue<T, ?>) configValue, source, EntityArgumentType.getPlayer(context, "target").getUuid(), value);
        }
        throw INVALID_CONFIG_TYPE_EXCEPTION.create();
    }
}
