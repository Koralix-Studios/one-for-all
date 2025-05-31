package com.koralix.oneforall.command;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.*;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.DataResult;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.argument;
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
    private static final DynamicCommandExceptionType CONFIG_TEST_EXCEPTION = new DynamicCommandExceptionType(
            text -> (Text) text
    );
    private static final SimpleCommandExceptionType INVALID_CONFIG_TYPE_EXCEPTION = new SimpleCommandExceptionType(
            Text.stringifiedTranslatable("command." + OneForAll.id() + ".config.unsupported_type")
    );

    private OfaCommand() {
        // Prevent instantiation
    }

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment
    ) {
        LiteralArgumentBuilder<ServerCommandSource> ofa = literal("ofa");

        ofa.then(
                argument("registry", RegistryKeyArgumentType.registryKey(ConfigRegistry.REGISTRY_KEY)).then(
                        argument("config", IdentifierArgumentType.identifier()).suggests(
                                (context, builder) -> {
                                    getConfigRegistry(context).forEach(entry -> {
                                        builder.suggest(entry.key().configId().toString());
                                    });
                                    return builder.buildFuture();
                                }
                        ).then(
                                argument("value", NbtElementArgumentType.nbtElement()).executes(OfaCommand::selfSet)
                        )
                )
        );

        dispatcher.register(ofa);
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
    private static <T> int selfSet(@NotNull ConfigValue<T, ?, ?> configValue, ServerCommandSource source, NbtElement nbt) throws CommandSyntaxException {
        DataResult<T> result = configValue.codec().parse(NbtOps.INSTANCE, nbt);
        if (result.isError()) throw INVALID_NBT_EXCEPTION.create(result.error().orElseThrow());
        T value = result.result().orElseThrow();
        if (configValue instanceof MonoConfigValue<T, ?, ?> monoConfigValue) {
            return setMono(monoConfigValue, source, value);
        } else if (PlayerConfigValue.class.isAssignableFrom(configValue.getClass())) {
            return setMulti((PlayerConfigValue<T, ?>) configValue, source, source.getPlayerOrThrow().getUuid(), value);
        }
        throw INVALID_CONFIG_TYPE_EXCEPTION.create();
    }

    private static int selfSet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ConfigRegistry registry = getConfigRegistry(context);
        Identifier configId = IdentifierArgumentType.getIdentifier(context, "config");
        ConfigValue<?, ?, ?> configValue = registry.getConfigValue(configId).orElseThrow(() -> UNKNOWN_CONFIG_EXCEPTION.create(configId));
        NbtElement nbt = NbtElementArgumentType.getNbtElement(context, "value");
        return selfSet(configValue, context.getSource(), nbt);
    }

    private static @NotNull ConfigRegistry getConfigRegistry(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        RegistryKey<ConfigRegistry> registryKey = RegistryKeyArgumentType.getKey(context, "registry", ConfigRegistry.REGISTRY_KEY, INVALID_CONFIG_REGISTRY_EXCEPTION);
        ConfigRegistry registry = ConfigRegistry.REGISTRY.get(registryKey);
        if (registry == null) throw INVALID_CONFIG_REGISTRY_EXCEPTION.create(registryKey);
        return registry;
    }
}
