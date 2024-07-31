package com.koralix.oneforall.settings;

import com.koralix.oneforall.OneForAll;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class SettingsManager {
    private static final Map<Identifier, Class<?>> CLASSES = new HashMap<>();
    private static final Map<Identifier, Map<Identifier, ConfigValue<?>>> SETTINGS = new HashMap<>();

    private SettingsManager() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Register an instance of a class with settings
     * @param clazz the class to register
     * @param <T> the type of the class
     */
    public static <T> void register(@NotNull Class<?> clazz) {
        SettingsRegistry registry = clazz.getAnnotation(SettingsRegistry.class);
        if (registry == null) throw new IllegalArgumentException("Class is not annotated with @SettingsRegistry");

        Identifier registryId = Identifier.of(OneForAll.MOD_ID, registry.id());
        CLASSES.put(registryId, clazz);

        for (var field : clazz.getDeclaredFields()) {
            if (!ConfigValue.class.isAssignableFrom(field.getType())) continue;

            try {
                @SuppressWarnings("unchecked")
                ConfigValue<T> value = (ConfigValue<T>) field.get(null);
                register(registryId, Identifier.of(OneForAll.MOD_ID, field.getName().toLowerCase()), value);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Field '" + field.getName() + "' is not static");
            }
        }
    }

    private static <T> void register(Identifier registryId, Identifier id, ConfigValue<T> value) {
        if (value instanceof ConfigValueWrapper<T> wrapper) {
            if (wrapper.registry == null) wrapper.registry = registryId;
            if (wrapper.id == null) wrapper.id = id;
        }
        if (SETTINGS.computeIfAbsent(registryId, k -> new HashMap<>()).putIfAbsent(id, value) == null) return;
        throw new IllegalArgumentException("Setting with id " + id + " is already registered");
    }

    /**
     * Get a setting by its registry and id
     * @param registry the registry of the setting
     * @param id the id of the setting
     * @return the setting
     */
    public static ConfigValue<?> get(Identifier registry, Identifier id) {
        return SETTINGS.getOrDefault(registry, Map.of()).get(id);
    }

    /**
     * Get a registry by its identifier
     * @param registry the identifier of the registry
     * @return the registry
     */
    public static Class<?> registry(Identifier registry) {
        return CLASSES.get(registry);
    }
}
