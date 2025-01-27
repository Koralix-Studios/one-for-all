package com.koralix.oneforall.settings;

public interface MultiConfigValue<K, T> extends ConfigValue<T> {
    /**
     * Get the config value view for the given key.
     *
     * @param key the key of the config value view
     * @return the config value view for the given key
     */
    ConfigValueView<T> view(K key);
}
