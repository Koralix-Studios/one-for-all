package com.koralix.oneforall.settings;

public interface ConfigValueChange<T> {
    ConfigValue<T> configValue();
    T old();
    T value();
    
    interface Multi<K, T> extends ConfigValueChange<T> {
        K key();
    }
    
    interface OnChange<T> {
        void onChange(ConfigValueChange<T> change);
    }

    record MonoChange<T>(SingletonConfigValue<T> configValue, T old, T value) implements ConfigValueChange<T> {
    }

    record MultiChange<K, T>(MultiConfigValue<K, T> configValue, K key, T old, T value) implements Multi<K, T> {
    }
}
