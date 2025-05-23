package com.koralix.oneforall.config.registry;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.ConfigValue;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConfigRegistry {
    public static final RegistryKey<Registry<ConfigRegistry>> REGISTRY_KEY = RegistryKey.ofRegistry(OneForAll.id("config_registry"));
    public static final Registry<ConfigRegistry> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
    private static final VersionedIdentifierMap<ConfigRegistry> VERSIONED = VersionedIdentifierMap.create();

    private final RegistryEntry.Reference<ConfigRegistry> entry;
    private final Map<Identifier, ConfigEntry<?>> configValues;
    private final VersionedIdentifierMap<ConfigEntry<?>> versioned;

    ConfigRegistry(
            @NotNull Identifier id,
            @NotNull List<VersionedIdentifier> ids,
            @NotNull Map<Identifier, ConfigEntry<?>> configValues,
            @NotNull VersionedIdentifierMap<ConfigEntry<?>> versioned
    ) {
        this.entry = Registry.registerReference(REGISTRY, id, this);
        this.configValues = configValues;
        this.versioned = versioned;
        for (VersionedIdentifier vId : ids) {
            VERSIONED.put(vId.version(), vId.identifier(), this);
        }
    }

    @Contract("_, _ -> new")
    public static @NotNull ConfigRegistryBuilder builder(@NotNull Version version, @NotNull Identifier identifier) {
        return new ConfigRegistryBuilder(version, identifier);
    }

    public static @NotNull ConfigRegistryBuilder builder(@NotNull String version, @NotNull Identifier identifier) {
        try {
            return builder(Version.parse(version), identifier);
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void freeze() {
        REGISTRY.freeze();
        VERSIONED.freeze();
    }

    public static @NotNull Optional<ConfigValue<?>> which(Version version, Identifier registryId, Identifier configId) {
        return ConfigRegistry
                .getConfigRegistry(version, registryId)
                .flatMap(registry -> registry.getConfigValue(version, configId));
    }

    public static @NotNull Optional<ConfigRegistry> getConfigRegistry(@NotNull Identifier id) {
        return Optional.ofNullable(REGISTRY.get(id));
    }

    public static @NotNull Optional<ConfigRegistry> getConfigRegistry(Version version, Identifier id) {
        return VERSIONED.get(version, id);
    }

    public @NotNull Optional<ConfigValue<?>> getConfigValue(Identifier id) {
        return Optional.ofNullable(this.configValues.get(id)).map(ConfigEntry::configValue);
    }

    public @NotNull Optional<ConfigValue<?>> getConfigValue(Version version, Identifier id) {
        return this.versioned.get(version, id).map(ConfigEntry::configValue);
    }
}
