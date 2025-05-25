package com.koralix.oneforall.config.loader;

import com.koralix.oneforall.config.ConfigValue;
import com.koralix.oneforall.config.registry.ConfigKey;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.koralix.oneforall.util.CustomCodecs;
import com.koralix.oneforall.util.Functions;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.loader.api.Version;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class ConfigStorageCodec implements Codec<ConfigStorage> {
    @Override
    public <T> @NotNull DataResult<Pair<ConfigStorage, T>> decode(@NotNull DynamicOps<T> ops, T input) {
        return ops.getMap(input).flatMap(map -> {
            Optional<Version> versionOpt = ops.getStringValue(map.get("version")).map(Functions.tryCatch(Version::parse)).result();
            if (versionOpt.isEmpty()) return DataResult.error(() -> "Missing or invalid 'version' field");
            Version version = versionOpt.get();

            Codec<Map<ConfigKey, Object>> codec = codec(version);

            return codec.decode(ops, map.get("entries"))
                    .map(pair -> pair.mapFirst(map1 -> new ConfigStorage(version, map1)));
        });
    }

    @Override
    public <T> DataResult<T> encode(@NotNull ConfigStorage input, DynamicOps<T> ops, T prefix) {
        Optional<T> versionOpt = CustomCodecs.VERSION.encode(input.version, ops, prefix).result();
        if (versionOpt.isEmpty()) return DataResult.error(() -> "Failed to encode version: " + input.version);
        T version = versionOpt.get();

        Codec<Map<ConfigKey, Object>> codec = codec(input.version);

        Optional<T> entriesOpt = codec.encode(input.entries, ops, prefix).result();
        if (entriesOpt.isEmpty()) return DataResult.error(() -> "Failed to encode entries for version: " + input.version);
        T entries = entriesOpt.get();

        return DataResult.success(ops.createMap(Map.of(
                ops.createString("version"), version,
                ops.createString("entries"), entries
        )));
    }

    private static @NotNull Codec<Map<ConfigKey, Object>> codec(@NotNull Version version) {
        return Codec.dispatchedMap(
                ConfigKey.CODEC,
                key -> ConfigRegistry.which(version, key.registryId(), key.configId())
                        .map(ConfigValue::codec)
                        .orElseThrow()
        );
    }
}
