package com.koralix.oneforall.config.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfigLoaderProvider<C extends ConfigLoader> {
    private final String name;
    private final ConfigLoaderFactory<C> factory;
    private final Consumer<C> register;
    private final List<C> loaders = new ArrayList<>();

    public ConfigLoaderProvider(String name, ConfigLoaderFactory<C> factory, Consumer<C> register) {
        this.name = name;
        this.factory = factory;
        this.register = register;
    }

    public C create(String modId) {
        C loader = this.factory.create(this.name, modId);
        this.loaders.add(loader);
        this.register.accept(loader);
        return loader;
    }

    public void load() {
        for (C loader : loaders) {
            try {
                loader.load();
            } catch (Exception e) {
                throw new RuntimeException("Failed to load config loader: " + loader.getClass().getSimpleName(), e);
            }
        }
    }

    @FunctionalInterface
    public interface ConfigLoaderFactory<C extends ConfigLoader> {
        C create(String name, String modId);
    }
}
