package com.koralix.oneforall.settings;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public final class SettingsManager {
    private static final Map<SettingsRegistry.Env, Set<Identifier>> REGISTRIES = new HashMap<>();
    private static final Map<Identifier, Map<SettingEntry<?>, ConfigValue<?>>> SETTINGS = new HashMap<>();

    private SettingsManager() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static @NotNull SettingsRegistry registry(@NotNull Class<?> clazz) {
        SettingsRegistry registry = clazz.getAnnotation(SettingsRegistry.class);
        if (registry == null) throw new IllegalArgumentException("Class is not annotated with @SettingsRegistry");
        return registry;
    }

    public static <T> Identifier identifier(@NotNull SettingsRegistry registry) {
        return Identifier.of(registry.namespace(), registry.id());
    }

    public static <T> Identifier identifier(@NotNull Class<T> clazz) {
        return identifier(registry(clazz));
    }

    /**
     * Register an instance of a class with settings
     * @param clazz the class to register
     * @param <T> the type of the class
     */
    public static <T> void register(@NotNull Class<?> clazz) {
        SettingsRegistry registry = registry(clazz);
        Identifier registryId = identifier(registry);
        REGISTRIES.computeIfAbsent(registry.env(), k -> new HashSet<>()).add(registryId);

        for (var field : clazz.getDeclaredFields()) {
            if (!ConfigValue.class.isAssignableFrom(field.getType())) continue;

            try {
                @SuppressWarnings("unchecked")
                ConfigValue<T> value = (ConfigValue<T>) field.get(null);
                SettingEntry<T> entry = new SettingEntry<>(registry, registryId.withPath(field.getName().toLowerCase()), value);
                if (value instanceof ConfigValueWrapper<T> wrapper) {
                    if (wrapper.entry == null) wrapper.entry = entry;
                    entry = wrapper.entry;
                } else if (value instanceof PlayerConfigValueWrapper<T> wrapper) {
                    if (wrapper.entry == null) wrapper.entry = entry;
                    entry = wrapper.entry;
                }
                register(entry);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Field '" + field.getName() + "' is not static");
            }
        }
    }

    private static <T> void register(@NotNull SettingEntry<T> entry) {
        if (SETTINGS.computeIfAbsent(entry.registryId(), k -> new HashMap<>()).putIfAbsent(entry, entry.setting()) == null) return;
        throw new IllegalArgumentException("Setting with id " + entry + " is already registered");
    }

    /**
     * Get a setting by its registry and entry
     * @param registry the registry of the setting
     * @param entry the entry of the setting
     * @return the setting
     */
    @SuppressWarnings("unchecked")
    public static <T> ConfigValue<T> get(Identifier registry, SettingEntry<T> entry) {
        return (ConfigValue<T>) SETTINGS.getOrDefault(registry, Map.of()).get(entry);
    }

    /**
     * Iterate over all settings
     */
    public static void forEach(BiConsumer<SettingEntry<?>, ConfigValue<?>> action) {
        SETTINGS.forEach((registry, settings) -> settings.forEach(action));
    }

    /**
     * Iterate over all settings in a registry
     */
    public static void forEach(Identifier registry, BiConsumer<SettingEntry<?>, ConfigValue<?>> action) {
        SETTINGS.getOrDefault(registry, Map.of()).forEach(action);
    }

    /**
     * Iterate over all settings of a specific environment
     */
    public static void forEach(SettingsRegistry.Env env, BiConsumer<SettingEntry<?>, ConfigValue<?>> action) {
        REGISTRIES.getOrDefault(env, Set.of()).forEach(registry -> forEach(registry, action));
    }
}
