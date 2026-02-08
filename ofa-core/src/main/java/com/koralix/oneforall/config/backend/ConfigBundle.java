package com.koralix.oneforall.config.backend;

import com.koralix.oneforall.config.ConfigRegistry;
import com.koralix.oneforall.config.ConfigValue;
import com.koralix.oneforall.util.CustomCodecs;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.fabricmc.loader.api.Version;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConfigBundle<K> implements ConfigBackend<K> {
    private final @NotNull Codec<ConfigBundle<K>> codec;
    private final @NotNull Map<Identifier, Object> entries;

    private ConfigBundle(@NotNull Map<Identifier, Object> entries, @NotNull ConfigRegistry<K> registry) {
        this.entries = entries;
        this.codec = codec(registry);
    }

    public ConfigBundle(@NotNull ConfigRegistry<K> registry) {
        this(new HashMap<>(), registry);
    }

    private static <K> @NotNull Codec<ConfigBundle<K>> codec(ConfigRegistry<K> registry) {
        return entriesCodec(registry).comapFlatMap(
                entries -> {
                    Map<Identifier, Object> entryMap = new HashMap<>();
                    for (Entry<?> entry : entries) {
                        Optional<? extends ConfigValue<K, ?, ?>> configValueOpt = entry.configValue(registry);
                        if (configValueOpt.isEmpty()) {
                            return DataResult.error(() -> "No ConfigValue found for identifier: " + entry.id +
                                    " in version: " + entry.version.getFriendlyString());
                        }
                        ConfigValue<K, ?, ?> configValue = configValueOpt.get();
                        entryMap.put(configValue.id(), entry.value);
                    }
                    return DataResult.success(new ConfigBundle<K>(entryMap, registry));
                },
                bundle -> bundle.entries.entrySet().stream()
                        .map(entry -> registry.get(entry.getKey())
                                .map(configValue -> new Entry<>(
                                        configValue.metadata().version(),
                                        configValue.id(),
                                        entry.getValue()
                                )).orElseThrow()
                        )
                        .toList()
        );
    }

    private static <K> @NotNull Codec<List<Entry<Object>>> entriesCodec(ConfigRegistry<K> registry) {
        return Codec.list(Entry.codec(registry));
    }

    public static <K, T> @NotNull ConfigBundle<K> deserialize(
            @NotNull DynamicOps<T> ops,
            @NotNull T nbt,
            @NotNull ConfigRegistry<K> registry
    ) {
        DataResult<Pair<ConfigBundle<K>, T>> result = codec(registry).decode(ops, nbt);
        if (result.error().isPresent()) {
            throw new RuntimeException("Failed to deserialize ConfigBundle: " + result.error().get().message());
        }
        return result.result().orElseThrow().getFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, B> @NotNull T get(@NotNull ConfigValue<K, T, B> configValue) {
        return (T) entries.getOrDefault(configValue.id(), configValue.nominalValue());
    }

    @Override
    public <T, B> void set(@NotNull ConfigValue<K, T, B> configValue, @Nullable T value) {
        Identifier id = configValue.id();
        if (value == null || configValue.nominalValue().equals(value)) {
            entries.remove(id);
            return;
        }
        entries.put(id, value);
    }

    @Override
    public @NotNull ConfigBundle<K> bundle() {
        return this;
    }

    public <T> @NotNull T serialize(DynamicOps<T> ops) {
        DataResult<T> result = codec.encodeStart(ops, this);
        if (result.error().isPresent()) {
            throw new RuntimeException("Failed to serialize ConfigBundle: " + result.error().get().message());
        }
        return result.result().orElseThrow();
    }

    private record Entry<T>(
            @NotNull Version version,
            @NotNull Identifier id,
            @NotNull T value
    ) {
        public static <K> @NotNull Codec<Entry<Object>> codec(ConfigRegistry<K> registry) {
            return Codec.of(
                    new Encoder<>() {
                        @Override
                        public <T> DataResult<T> encode(Entry<Object> input, DynamicOps<T> ops, T prefix) {
                            return ops.mapBuilder()
                                    .add("version", CustomCodecs.VERSION.encodeStart(ops, input.version))
                                    .add("id", Identifier.CODEC.encodeStart(ops, input.id))
                                    .add("value", input.encodeValue(ops, registry))
                                    .build(prefix);
                        }
                    },
                    new Decoder<>() {
                        @Override
                        public <T> DataResult<Pair<Entry<Object>, T>> decode(DynamicOps<T> ops, T input) {
                            return ops.getMap(input)
                                    .flatMap(map -> {
                                        DataResult<Pair<Version, T>> versionResult = CustomCodecs.VERSION.decode(ops, map.get("version"));
                                        DataResult<Pair<Identifier, T>> idResult = Identifier.CODEC.decode(ops, map.get("id"));

                                        return versionResult.flatMap(version ->
                                                idResult.flatMap(id ->
                                                        Entry.decodeValue(
                                                                ops,
                                                                map.get("value"),
                                                                version.getFirst(),
                                                                id.getFirst(),
                                                                registry
                                                        ).flatMap(value ->
                                                                ops.mapBuilder()
                                                                        .add("version", version.getSecond())
                                                                        .add("id", id.getSecond())
                                                                        .add("value", value.getSecond())
                                                                        .build((T) null).map(rest -> new Pair<>(
                                                                                new Entry<>(version.getFirst(), id.getFirst(), value.getFirst()),
                                                                                rest
                                                                        ))
                                                        )
                                                )
                                        );
                                    });
                        }
                    }
            );
        }

        @SuppressWarnings("unchecked")
        private static <K, T, I> @NotNull DataResult<Pair<T, I>> decodeValue(
                @NotNull DynamicOps<I> ops,
                @Nullable I value,
                @NotNull Version version,
                @NotNull Identifier id,
                @NotNull ConfigRegistry<K> registry
        ) {
            if (value == null) {
                return DataResult.error(() -> "Value for identifier: " + id + " in version: " + version.getFriendlyString() + " is null");
            }
            return registry
                    .get(version, id)
                    .map(configValue -> (ConfigValue<K, T, ?>) configValue)
                    .map(configValue -> configValue.codec().codec().decode(ops, value))
                    .orElseGet(() -> DataResult.error(
                            () -> "No ConfigValue found for identifier: " + id +
                                    " in version: " + version.getFriendlyString()
                    ));
        }

        @SuppressWarnings("unchecked")
        private <K> @NotNull Optional<ConfigValue<K, T, ?>> configValue(@NotNull ConfigRegistry<K> registry) {
            return registry
                    .get(version, id)
                    .map(configValue -> (ConfigValue<K, T, ?>) configValue);
        }

        private <K, I> @NotNull DataResult<I> encodeValue(
                @NotNull DynamicOps<I> ops,
                @NotNull ConfigRegistry<K> registry
        ) {
            return configValue(registry)
                    .map(configValue -> configValue.codec().codec().encodeStart(ops, value))
                    .orElseGet(() -> DataResult.error(
                            () -> "No ConfigValue found for identifier: " + id +
                                    " in version: " + version.getFriendlyString()
                    ));
        }
    }
}
