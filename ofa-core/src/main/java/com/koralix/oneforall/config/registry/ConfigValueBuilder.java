package com.koralix.oneforall.config.registry;

import com.koralix.oneforall.config.ConfigTest;
import com.koralix.oneforall.config.ConfigValue;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.fabricmc.loader.api.Version;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConfigValueBuilder<T, C extends ConfigValue<T, B, S>, B extends ByteBuf, S> {
    private final ConfigRegistrar registrar;
    private final T nominal;
    private final Codec<T> codec;
    private final PacketCodec<B, T> packetCodec;
    private final ConfigValueFactory<T, C, B, S> factory;
    private final List<VersionedIdentifier> ids = new ArrayList<>();
    private ConfigTest<T> test;

    public ConfigValueBuilder(
            @NotNull VersionedIdentifier id,
            @NotNull ConfigRegistrar registrar,
            @NotNull T nominal,
            @NotNull Codec<T> codec,
            @NotNull PacketCodec<B, T> packetCodec,
            @NotNull ConfigValueFactory<T, C, B, S> factory
    ) {
        this.ids.add(id);
        this.registrar = registrar;
        this.nominal = nominal;
        this.codec = codec;
        this.packetCodec = packetCodec;
        this.factory = factory;
    }

    public ConfigValueBuilder<T, C, B, S> id(@NotNull VersionedIdentifier id) {
        if (this.ids.getLast().compareTo(id) >= 0) {
            throw new IllegalArgumentException("VersionedIdentifier must be in ascending order");
        }
        this.ids.add(id);
        return this;
    }

    public ConfigValueBuilder<T, C, B, S> id(@NotNull Version version, @NotNull Identifier id) {
        return id(new VersionedIdentifier(version, id));
    }

    public ConfigValueBuilder<T, C, B, S> test(ConfigTest<T> test) {
        this.test = this.test == null ? test : this.test.and(test);
        return this;
    }

    public ConfigValueBuilder<T, C, B, S> test(Predicate<T> test) {
        return test(ConfigTest.of(test));
    }

    public C build() {
        return this.factory.create(
                c -> this.registrar.register(this.ids, c),
                this.nominal,
                this.codec,
                this.packetCodec,
                this.test == null ? ConfigTest.tauto() : this.test
        );
    }

    @FunctionalInterface
    public interface ConfigValueFactory<T, C extends ConfigValue<T, B, S>, B extends ByteBuf, S> {
        @NotNull C create(
                @NotNull Function<ConfigValue<T, B, S>, ConfigEntry<T>> registerFn,
                @NotNull T nominal,
                @NotNull Codec<T> codec,
                @NotNull PacketCodec<B, T> packetCodec,
                @NotNull ConfigTest<T> test
        );
    }
}
