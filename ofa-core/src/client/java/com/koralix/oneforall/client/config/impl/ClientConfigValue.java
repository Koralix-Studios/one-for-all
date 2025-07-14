package com.koralix.oneforall.client.config.impl;

import com.koralix.oneforall.config.*;
import com.koralix.oneforall.config.storage.NbtFileConfigStorage;
import com.koralix.oneforall.init.Initializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ClientConfigValue<T, B> extends AbstractConfigValue<MinecraftClient, T, B> {
    public static final ConfigRegistry<MinecraftClient> REGISTRY = new ConfigRegistry<>(
            Initializer.id("client_settings"),
            new NbtFileConfigStorage<>(
                    FabricLoader.getInstance().getConfigDir()
                            .resolve(Initializer.COMMON_ID)
                            .resolve("client_settings.nbt")
            )
    );

    public ClientConfigValue(
            @NotNull ConfigMetadata metadata,
            @NotNull T nominalValue,
            @NotNull ConfigCodec<T, B> codec,
            @NotNull ConfigTest<T> test
    ) {
        super(REGISTRY, metadata, nominalValue, codec, test);
    }

    public T get() {
        return this.get(MinecraftClient.getInstance());
    }

    public void set(T value) {
        this.set(MinecraftClient.getInstance(), value);
    }

    @Contract("_, _, _, _ -> new")
    public static <T, B> @NotNull Builder<T, B> create(
            @NotNull String versionString,
            @NotNull Identifier id,
            @NotNull T nominalValue,
            @NotNull ConfigCodec<T, B> codec
    ) {
        Version version;
        try {
            version = Version.parse(versionString);
        } catch (VersionParsingException e) {
            throw new IllegalArgumentException("Invalid version string: " + versionString, e);
        }
        return new Builder<>(version, id, nominalValue, codec);
    }

    public static class Builder<T, B> extends AbstractConfigValue.Builder<MinecraftClient, T, B, ClientConfigValue<T, B>> {
        public Builder(
                Version version,
                Identifier id,
                T nominalValue,
                ConfigCodec<T, B> codec
        ) {
            super(version, id, nominalValue, codec);
        }

        @Override
        public ClientConfigValue<T, B> create() {
            return new ClientConfigValue<>(
                    this.metadata(),
                    this.nominalValue,
                    this.codec,
                    this.test()
            );
        }
    }
}
