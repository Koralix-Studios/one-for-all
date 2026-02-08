package com.koralix.oneforall.config.impl;

import com.koralix.oneforall.config.*;
import com.koralix.oneforall.config.storage.MultiplexedConfigStorage;
import com.koralix.oneforall.config.storage.NbtFileConfigStorage;
import com.koralix.oneforall.entry.OneForAll;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ServerConfigValue<T, B> extends AbstractConfigValue<MinecraftServer, T, B> {
    public static final ConfigRegistry<MinecraftServer> REGISTRY = new ConfigRegistry<>(
            OneForAll.id("server_settings"),
            new MultiplexedConfigStorage<>(server -> new NbtFileConfigStorage<>(
                    server.getSavePath(WorldSavePath.ROOT).resolve(OneForAll.MOD_ID).resolve("server_settings.nbt")
            ))
    );

    public ServerConfigValue(
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

    public T get() {
        return OneForAll.server().map(this::get).orElse(this.nominalValue());
    }

    public static class Builder<T, B> extends AbstractConfigValue.Builder<MinecraftServer, T, B, ServerConfigValue<T, B>> {
        public Builder(
                Version version,
                Identifier id,
                T nominalValue,
                ConfigCodec<T, B> codec
        ) {
            super(version, id, nominalValue, codec);
        }

        @Override
        public ServerConfigValue<T, B> create() {
            return new ServerConfigValue<>(
                    this.metadata(),
                    this.nominalValue,
                    this.codec,
                    this.test()
            );
        }
    }
}
