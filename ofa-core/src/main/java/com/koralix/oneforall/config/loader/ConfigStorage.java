package com.koralix.oneforall.config.loader;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.ConfigValue;
import com.koralix.oneforall.config.MonoConfigValue;
import com.koralix.oneforall.config.registry.ConfigKey;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.koralix.oneforall.util.CustomCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.Version;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigStorage {
    public static final Codec<ConfigStorage> CODEC = CustomCodecs.VERSION.dispatch(
            "version",
            configStorage -> configStorage.version,
            version -> RecordCodecBuilder.mapCodec(instance -> instance.group(
                    codec(version).xmap(
                            map -> {
                                Map<ConfigKey, Object> flattened = new HashMap<>();
                                map.forEach((registryId, configMap) -> configMap.forEach((configId, value) -> {
                                    ConfigKey key = new ConfigKey(registryId, configId);
                                    flattened.put(key, value);
                                }));
                                return flattened;
                            },
                            map -> {
                                Map<Identifier, Map<Identifier, Object>> reversed = new HashMap<>();
                                map.forEach((key, value) -> {
                                    reversed.computeIfAbsent(key.registryId(), k -> new HashMap<>())
                                            .put(key.configId(), value);
                                });
                                return reversed;
                            }
                    ).fieldOf("entries").forGetter(configStorage -> configStorage.entries)
            ).apply(instance, map -> new ConfigStorage(version, map)))
    );

    private final Version version;
    private final Map<ConfigKey, Object> entries;

    private ConfigStorage(Version version, Map<ConfigKey, Object> entries) {
        this.version = version;
        this.entries = entries;
    }

    private static @NotNull Codec<Map<Identifier, Map<Identifier, Object>>> codec(@NotNull Version version) {
        return Codec.dispatchedMap(
                Identifier.CODEC,
                registryId -> codec(version, registryId)
        );
    }

    private static @NotNull Codec<Map<Identifier, Object>> codec(@NotNull Version version, @NotNull Identifier registryId) {
        return Codec.dispatchedMap(
                Identifier.CODEC,
                configId -> ConfigRegistry.which(version, registryId, configId)
                        .map(ConfigValue::codec)
                        .orElse(
                                CustomCodecs.error("Unknown config value (" + version + "): " + registryId + "/" + configId)
                        )
        );
    }

    @Contract("_ -> new")
    public static @NotNull ConfigStorage create(@NotNull Collection<MonoConfigValue<?, ?>> configs) {
        return new ConfigStorage(
                OneForAll.VERSION,
                configs.stream().collect(Collectors.toMap(
                        ConfigValue::key,
                        MonoConfigValue::value
                ))
        );
    }

    @Contract("_ -> new")
    public static @NotNull ConfigStorage create(MonoConfigValue<?, ?>... configs) {
        return create(List.of(configs));
    }
}
