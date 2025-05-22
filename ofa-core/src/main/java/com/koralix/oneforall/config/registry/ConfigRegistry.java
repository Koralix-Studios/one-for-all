package com.koralix.oneforall.config.registry;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.ConfigValue;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.loader.api.Version;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ConfigRegistry {
    RegistryKey<Registry<ConfigRegistry>> REGISTRY_KEY = RegistryKey.ofRegistry(OneForAll.id("config_registry"));
    Registry<ConfigRegistry> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
    VersionedIdentifierMap<ConfigRegistry> VERSIONED = VersionedIdentifierMap.create();

    static @NotNull Optional<ConfigValue<?>> which(Version version, Identifier registryId, Identifier configId) {
        return ConfigRegistry
                .getConfigRegistry(version, registryId)
                .flatMap(registry -> registry.getConfigValue(version, configId));
    }

    static @NotNull Optional<ConfigRegistry> getConfigRegistry(@NotNull Identifier id) {
        return Optional.ofNullable(REGISTRY.get(id));
    }

    static @NotNull Optional<ConfigRegistry> getConfigRegistry(Version version, Identifier id) {
        return VERSIONED.get(version, id);
    }

    @NotNull Optional<ConfigValue<?>> getConfigValue(Identifier id);

    @NotNull Optional<ConfigValue<?>> getConfigValue(Version version, Identifier id);
}
