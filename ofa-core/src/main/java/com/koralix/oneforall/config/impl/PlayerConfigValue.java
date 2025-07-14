package com.koralix.oneforall.config.impl;

import com.koralix.oneforall.config.*;
import com.koralix.oneforall.config.storage.MultiplexedConfigStorage;
import com.koralix.oneforall.config.storage.NbtFileConfigStorage;
import com.koralix.oneforall.init.Initializer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PlayerConfigValue<T, B> extends AbstractConfigValue<ServerPlayerEntity, T, B> {
    public static final ConfigRegistry<ServerPlayerEntity> REGISTRY = new ConfigRegistry<>(
            Initializer.id("player_settings"),
            new MultiplexedConfigStorage<>(player -> new NbtFileConfigStorage<>(
                    player.getServer().getSavePath(WorldSavePath.ROOT).resolve(Initializer.COMMON_ID).resolve("player_settings")
                            .resolve(player.getUuidAsString() + ".nbt")
            ))
    );

    public PlayerConfigValue(
            @NotNull ConfigMetadata metadata,
            @NotNull T nominalValue,
            @NotNull ConfigCodec<T, B> codec,
            @NotNull ConfigTest<T> test
    ) {
        super(REGISTRY, metadata, nominalValue, codec, test);
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

    public static class Builder<T, B> extends AbstractConfigValue.Builder<ServerPlayerEntity, T, B, PlayerConfigValue<T, B>> {
        public Builder(
                Version version,
                Identifier id,
                T nominalValue,
                ConfigCodec<T, B> codec
        ) {
            super(version, id, nominalValue, codec);
        }

        @Override
        public PlayerConfigValue<T, B> create() {
            return new PlayerConfigValue<>(
                    this.metadata(),
                    this.nominalValue,
                    this.codec,
                    this.test()
            );
        }
    }
}
