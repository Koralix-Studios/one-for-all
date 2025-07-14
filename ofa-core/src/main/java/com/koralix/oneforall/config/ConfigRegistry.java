package com.koralix.oneforall.config;

import com.koralix.oneforall.config.backend.ConfigBundle;
import com.koralix.oneforall.config.storage.ConfigStorage;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.loader.api.Version;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ConfigRegistry<K> {
    public final RegistryKey<Registry<ConfigValue<K, ?, ?>>> registryKey;
    public final Registry<ConfigValue<K, ?, ?>> registry;
    private final Map<Version, Map<Identifier, ConfigValue<K, ?, ?>>> versioned = new HashMap<>();
    private final ConfigStorage<K, NbtElement> storage;

    public ConfigRegistry(Identifier id, ConfigStorage<K, NbtElement> storage) {
        this.registryKey = RegistryKey.ofRegistry(id);
        this.registry = FabricRegistryBuilder.createSimple(this.registryKey).buildAndRegister();
        this.storage = storage;
    }

    @SuppressWarnings("unchecked")
    public <T, B> RegistryEntry.Reference<ConfigValue<K, T, B>> register(@NotNull ConfigValue<K, T, B> configValue) {
        Identifier id = configValue.metadata().id();
        configValue.metadata().forEachId((version, identifier) -> {
            versioned.computeIfAbsent(version, k -> new HashMap<>()).put(identifier, configValue);
        });
        return Registry.registerReference((Registry<ConfigValue<K, T, B>>) (Object) this.registry, id, configValue);
    }

    public @NotNull Optional<ConfigValue<K, ?, ?>> get(@NotNull Identifier id) {
        return this.registry.getOptionalValue(id);
    }

    public @NotNull Optional<ConfigValue<K, ?, ?>> get(@NotNull Version version, @NotNull Identifier id) {
        return Optional.ofNullable(versioned.getOrDefault(version, Map.of()).get(id));
    }

    public void save(K backend) throws IOException {
        storage.save(backend);
    }

    public @NotNull ConfigBundle<K> load(K backend) throws IOException {
        return storage.load(backend, this);
    }
}
