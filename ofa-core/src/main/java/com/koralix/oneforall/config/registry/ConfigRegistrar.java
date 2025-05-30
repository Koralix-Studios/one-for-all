package com.koralix.oneforall.config.registry;

import com.koralix.oneforall.config.*;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

@SuppressWarnings("Convert2Diamond")
public class ConfigRegistrar {
    private final @NotNull Identifier id;
    private final @NotNull List<VersionedIdentifier> ids;
    private final Map<Identifier, ConfigEntry<?>> configValues = new HashMap<>();
    private final VersionedIdentifierMap<ConfigEntry<?>> versioned = VersionedIdentifierMap.create();

    ConfigRegistrar(@NotNull List<VersionedIdentifier> ids) {
        this.id = ids.getLast().identifier();
        this.ids = ids;
    }

    public <T, B extends ByteBuf> ConfigValueBuilder<T, SingletonConfigValue<T, B>, B, SingletonConfigValue.SaveData<T>> mono(
            @NotNull VersionedIdentifier id,
            @NotNull T nominal,
            @NotNull Codec<T> codec,
            @NotNull PacketCodec<B, T> packetCodec
    ) {
        return new ConfigValueBuilder<T, SingletonConfigValue<T, B>, B, SingletonConfigValue.SaveData<T>>(id, this, nominal, codec, packetCodec, SingletonConfigValue::new);
    }

    public <K, T, B extends ByteBuf> ConfigValueBuilder<T, DefaultedMapConfigValue<K, T, B>, B, DefaultedMapConfigValue.SaveData<K, T>> multi(
            @NotNull VersionedIdentifier id,
            @NotNull T nominal,
            @NotNull Codec<K> keyCodec,
            @NotNull Codec<T> codec,
            @NotNull PacketCodec<B, T> packetCodec
    ) {
        return new ConfigValueBuilder<T, DefaultedMapConfigValue<K, T, B>, B, DefaultedMapConfigValue.SaveData<K, T>>(
                id,
                this,
                nominal,
                codec,
                packetCodec,
                (registerFn, nominal1, codec1, packetCodec1, test) -> new DefaultedMapConfigValue<>(
                        registerFn,
                        nominal1,
                        keyCodec,
                        codec1,
                        packetCodec1,
                        test
                )
        );
    }

    public <T, B extends ByteBuf> ConfigValueBuilder<T, PlayerConfigValue<T, B>, B, DefaultedMapConfigValue.SaveData<UUID, T>> player(
            @NotNull VersionedIdentifier id,
            @NotNull T nominal,
            @NotNull Codec<T> codec,
            @NotNull PacketCodec<B, T> packetCodec,
            @NotNull Predicate<ConfigActor> canObserveOthers,
            @NotNull Predicate<ConfigActor> canChangeOthers
    ) {
        return new ConfigValueBuilder<T, PlayerConfigValue<T, B>, B, DefaultedMapConfigValue.SaveData<UUID, T>>(
                id,
                this,
                nominal,
                codec,
                packetCodec,
                (registerFn, nominal1, codec1, packetCodec1, test1) -> new PlayerConfigValue<>(
                        registerFn,
                        nominal1,
                        codec1,
                        packetCodec1,
                        test1,
                        canObserveOthers,
                        canChangeOthers
                )
        );
    }

    public <T, C extends ConfigValue<T, B, S>, B extends ByteBuf, S> ConfigEntry<T> register(@NotNull List<VersionedIdentifier> ids, @NotNull C configValue) {
        ConfigEntry<T> entry = new ConfigEntry<>(new ConfigKey(id, ids.getLast().identifier()), configValue);
        this.versioned.putAll(ids, entry);
        this.configValues.put(entry.key().configId(), entry);
        return entry;
    }

    public @NotNull ConfigRegistry complete() {
        return new ConfigRegistry(
                this.id,
                this.ids,
                Map.copyOf(this.configValues),
                this.versioned.frozenCopy()
        );
    }
}
