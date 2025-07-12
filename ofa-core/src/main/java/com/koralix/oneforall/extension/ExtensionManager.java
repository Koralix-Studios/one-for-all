package com.koralix.oneforall.extension;

import com.google.common.collect.ImmutableMap;
import com.koralix.oneforall.OFA;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ExtensionManager {
    private final Map<String, OFA> extensions;

    private ExtensionManager(Map<String, OFA> extensions) {
        this.extensions = ImmutableMap.copyOf(extensions);
    }

    public void forEach(Consumer<OFA> consumer) {
        extensions.values().forEach(consumer);
    }

    public int count() {
        return extensions.size();
    }

    public static class Builder {
        private final Map<String, OFA> extensions = new HashMap<>();

        public Builder register(@NotNull OFA ofa) {
            final String name = ofa.id();
            if (extensions.containsKey(name)) {
                throw new IllegalArgumentException("Extension with name '" + name + "' is already registered.");
            }
            extensions.put(name, ofa);
            return this;
        }

        public ExtensionManager build() {
            return new ExtensionManager(extensions);
        }
    }
}
