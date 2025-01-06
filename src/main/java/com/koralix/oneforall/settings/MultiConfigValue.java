package com.koralix.oneforall.settings;

public interface MultiConfigValue<K, T, C extends MultiConfigValue<K, T, C>> extends ConfigValue<T> {
    /**
     * Get the config id view for the given id.
     *
     * @param key the id of the config id view
     * @return the config id view for the given id
     */
    ConfigValueView<T, C> view(K key);
}
