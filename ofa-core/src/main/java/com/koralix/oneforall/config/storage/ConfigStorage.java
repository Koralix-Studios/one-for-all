package com.koralix.oneforall.config.storage;

import com.koralix.oneforall.config.ConfigRegistry;
import com.koralix.oneforall.config.backend.ConfigBundle;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface ConfigStorage<K, T> {
    void save(@NotNull K backend) throws IOException;

    @NotNull ConfigBundle<K> load(@NotNull K backend, @NotNull ConfigRegistry<K> registry) throws IOException;
}
