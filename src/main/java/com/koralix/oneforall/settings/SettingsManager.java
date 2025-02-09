package com.koralix.oneforall.settings;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.settings.registry.ConfigValueEnvironment;
import com.koralix.oneforall.settings.registry.ConfigValueRegistry;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class SettingsManager {
    public static final RegistryKey<Registry<ConfigValueRegistry>> SETTINGS_REGISTRY_KEY = RegistryKey.ofRegistry(OneForAll.id("settings"));
    private static final MutableRegistry<ConfigValueRegistry> SETTINGS_REGISTRY = FabricRegistryBuilder
            .createSimple(SETTINGS_REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
    public static final Registry<ConfigValueRegistry> REGISTRIES = SETTINGS_REGISTRY;

    private SettingsManager() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static void register(Identifier registryId, ConfigValueRegistry registry) {
        RegistryKey<ConfigValueRegistry> key = RegistryKey.of(SETTINGS_REGISTRY_KEY, registryId);
        SETTINGS_REGISTRY.add(key, registry, Lifecycle.stable());
    }

    public static void register(Identifier registryId, Class<?> clazz, ConfigValueEnvironment environment) {
        ConfigValueRegistry registry = new ConfigValueRegistry(registryId, environment);
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

    public static void forEach(Predicate<ConfigValueRegistry> predicate, Consumer<ConfigValue<?>> action) {
        SETTINGS_REGISTRY.forEach(registry -> {
            if (predicate.test(registry)) {
                registry.forEach(action);
            }
        });
    }

    public static void load(ConfigValueEnvironment environment) {
        load(environment, environment.server() ? ConfigValueEnvironment::server : ConfigValueEnvironment.CLIENT::equals);
    }

    public static void load(ConfigValueEnvironment environment, Predicate<ConfigValueEnvironment> predicate) {
        if (!predicate.test(environment)) throw new IllegalArgumentException("Environment does not match predicate");
        File file = environment.file(false);
        try {
            NbtCompound compound = NbtIo.readCompressed(file);
            Version version = Version.parse(compound.getString("version"));
            forEach(
                    configValueRegistry -> predicate.test(configValueRegistry.environment()),
                    configValue -> load(configValue, compound, version)
            );
        } catch (FileNotFoundException ignored) {
        } catch (IOException | VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> void load(ConfigValue<T> configValue, NbtCompound compound, Version version) {
        if (version.compareTo(configValue.since()) < 0) {
            OneForAll.LOGGER.warn("Skipping config value {} because it was saved before it was added ({} < {})", configValue.entry().key().toString(), version, configValue.since());
            return;
        }
        configValue.read(compound);
    }

    public static void save(ConfigValueEnvironment environment) {
        save(environment, environment.server() ? ConfigValueEnvironment::server : ConfigValueEnvironment.CLIENT::equals);
    }

    public static void save(ConfigValueEnvironment environment, Predicate<ConfigValueEnvironment> predicate) {
        if (!predicate.test(environment)) throw new IllegalArgumentException("Environment does not match predicate");
        File file = environment.file(true);
        NbtCompound compound = new NbtCompound();
        compound.putString("version", OneForAll.MOD_VERSION);
        forEach(
                configValueRegistry -> predicate.test(configValueRegistry.environment()),
                configValue -> save(configValue, compound)
        );
        try {
            NbtIo.writeCompressed(compound, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> void save(ConfigValue<T> configValue, NbtCompound compound) {
        configValue.write(compound);
    }
}
