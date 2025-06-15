package com.koralix.oneforall.config.feature;

import java.util.HashMap;
import java.util.Map;

public class FeatureRegistrar {
    final Map<String, Feature<?>> features = new HashMap<>();

    FeatureRegistrar() {}

    public <T, F extends Feature<T>> F register(String id, F feature) {
        if (this.features.containsKey(id)) {
            throw new IllegalArgumentException("Feature with id " + id + " is already registered.");
        }
        this.features.put(id, feature);
        return feature;
    }

    public <T> void complete() {
        FeatureRegistry.INSTANCE.register(this);
    }
}
