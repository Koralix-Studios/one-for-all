package com.koralix.oneforall.config.registry;

import com.koralix.oneforall.config.ConfigValue;
import com.koralix.oneforall.config.MonoConfigValue;
import com.koralix.oneforall.config.SingletonConfigValue;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigRegistrar {
    private final @NotNull Identifier id;
    private final @NotNull List<VersionedIdentifier> ids;
    private final Map<Identifier, ConfigEntry<?>> configValues = new HashMap<>();
    private final VersionedIdentifierMap<ConfigEntry<?>> versioned = VersionedIdentifierMap.create();

    ConfigRegistrar(@NotNull List<VersionedIdentifier> ids) {
        this.id = ids.getLast().identifier();
        this.ids = ids;
    }

    public <V> ConfigValueBuilder<V, MonoConfigValue<V>> mono(@NotNull VersionedIdentifier id, @NotNull V nominal, @NotNull Codec<V> codec, @NotNull PacketCodec<? extends ByteBuf, V> packetCodec) {
        return new ConfigValueBuilder<>(id, this, nominal, codec, packetCodec, SingletonConfigValue::new);
    }

    public <V, C extends ConfigValue<V>> ConfigEntry<V> register(@NotNull List<VersionedIdentifier> ids, @NotNull C configValue) {
        ConfigEntry<V> entry = new ConfigEntry<>(new ConfigKey(id, ids.getLast().identifier()), configValue);
        this.versioned.putAll(ids, entry);
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
