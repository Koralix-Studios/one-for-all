package com.koralix.oneforall.config.loader;

public final class Storages {
    private Storages() {
        // Prevent instantiation
    }

    public static final DynamicConfigStorage UNIVERSAL = DynamicConfigStorage.create();
    public static final DynamicConfigStorage SERVER = DynamicConfigStorage.create();
}
