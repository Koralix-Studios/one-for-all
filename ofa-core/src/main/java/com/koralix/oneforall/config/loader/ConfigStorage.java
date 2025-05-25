package com.koralix.oneforall.config.loader;

import com.koralix.oneforall.config.registry.ConfigKey;
import net.fabricmc.loader.api.Version;

import java.util.Map;

public class ConfigStorage {
    final Version version;
    final Map<ConfigKey, Object> entries;

    ConfigStorage(Version version, Map<ConfigKey, Object> entries) {
        this.version = version;
        this.entries = entries;
    }
}
