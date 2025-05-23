package com.koralix.oneforall.config.registry;

import com.koralix.oneforall.config.ConfigValue;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.fabricmc.loader.api.Version;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ConfigValueBuilder<V, C extends ConfigValue<V>> {
    private final ConfigRegistrar registrar;
    private final V nominal;
    private final Codec<V> codec;
    private final PacketCodec<? extends ByteBuf, V> packetCodec;
    private final ConfigValueFactory<V, C> factory;
    private final List<VersionedIdentifier> ids = new ArrayList<>();

    public ConfigValueBuilder(
            @NotNull VersionedIdentifier id,
            @NotNull ConfigRegistrar registrar,
            @NotNull V nominal,
            @NotNull Codec<V> codec,
            @NotNull PacketCodec<? extends ByteBuf, V> packetCodec,
            @NotNull ConfigValueFactory<V, C> factory
    ) {
        this.ids.add(id);
        this.registrar = registrar;
        this.nominal = nominal;
        this.codec = codec;
        this.packetCodec = packetCodec;
        this.factory = factory;
    }

    public ConfigValueBuilder<V, C> id(@NotNull VersionedIdentifier id) {
        if (this.ids.getLast().compareTo(id) >= 0) {
            throw new IllegalArgumentException("VersionedIdentifier must be in ascending order");
        }
        this.ids.add(id);
        return this;
    }

    public ConfigValueBuilder<V, C> id(@NotNull Version version, @NotNull Identifier id) {
        return id(new VersionedIdentifier(version, id));
    }

    public C build() {
        return this.factory.create(
                c -> this.registrar.register(this.ids, c),
                this.nominal,
                this.codec,
                this.packetCodec
        );
    }

    @FunctionalInterface
    public interface ConfigValueFactory<V, C extends ConfigValue<V>> {
        @NotNull C create(
                @NotNull Function<C, ConfigEntry<V>> registerFn,
                @NotNull V nominal,
                @NotNull Codec<V> codec,
                @NotNull PacketCodec<? extends ByteBuf, V> packetCodec
        );
    }
}
