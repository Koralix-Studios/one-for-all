package com.koralix.oneforall.config.feature;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FeatureRegistry {
    public static final FeatureRegistry INSTANCE = new FeatureRegistry();
    private @NotNull Map<String, Feature<?>> features = new HashMap<>();

    private FeatureRegistry() {}

    public static @NotNull FeatureRegistrar registrar() {
        return new FeatureRegistrar();
    }

    void register(@NotNull FeatureRegistrar registrar) {
        for (Map.Entry<String, Feature<?>> entry : registrar.features.entrySet()) {
            if (this.features.containsKey(entry.getKey())) {
                throw new IllegalArgumentException("Feature with id " + entry.getKey() + " is already registered.");
            }
            this.features.put(entry.getKey(), entry.getValue());
        }
    }

    public void freeze() {
        this.features = Map.copyOf(this.features);
    }

    public void forEach(BiConsumer<String, Feature<?>> consumer) {
        this.features.forEach(consumer);
    }
}
