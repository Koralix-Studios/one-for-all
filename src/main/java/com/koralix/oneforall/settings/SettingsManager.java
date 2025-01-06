package com.koralix.oneforall.settings;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.settings.registry.ConfigValueRegistry;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public final class SettingsManager {
    public static final RegistryKey<Registry<ConfigValueRegistry>> SETTINGS_REGISTRY_KEY = RegistryKey.ofRegistry(OneForAll.id("settings"));
    private static final MutableRegistry<ConfigValueRegistry> SETTINGS_REGISTRY = FabricRegistryBuilder
            .createSimple(SETTINGS_REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    private SettingsManager() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static void register(Identifier registryId, ConfigValueRegistry registry) {
        RegistryKey<ConfigValueRegistry> key = RegistryKey.of(SETTINGS_REGISTRY_KEY, registryId);
        SETTINGS_REGISTRY.add(key, registry, Lifecycle.stable());
    }

    public static void register(Identifier registryId, Class<?> clazz) {
        ConfigValueRegistry registry = new ConfigValueRegistry(registryId);
        for (Field field : clazz.getDeclaredFields()) {
            if (ConfigValue.class.isAssignableFrom(field.getType())) {
                try {
                    ConfigValue<?> value = (ConfigValue<?>) field.get(null);
                    registry.register(field.getName().toLowerCase(), value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        register(registryId, registry);
    }

    public static void freeze() {
        SETTINGS_REGISTRY.forEach(ConfigValueRegistry::freeze);
        SETTINGS_REGISTRY.freeze();
    }

    public static void forEach(Consumer<ConfigValue<?>> action) {
        SETTINGS_REGISTRY.forEach(registry -> registry.forEach(action));
    }
}
