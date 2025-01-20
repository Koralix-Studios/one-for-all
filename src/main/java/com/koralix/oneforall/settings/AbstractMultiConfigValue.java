package com.koralix.oneforall.settings;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;

import java.util.Optional;

public abstract class AbstractMultiConfigValue<K, T> extends AbstractConfigValue<T> implements MultiConfigValue<K, T> {
    public AbstractMultiConfigValue(T nominalValue, Codec<T> codec, ConfigValidator<T> validator) {
        super(nominalValue, codec, validator);
    }

    /**
     * Get the default value for the given key.
     *
     * @param key the key of the config value view
     * @return the default value for the given key
     */
    public T defaultValue(K key) {
        return view(key).defaultValue();
    }

    /**
     * Set the default value for the given key.
     *
     * @param key the key of the config value view
     * @param value the default value to set
     * @return an Optional containing the error message if the default value is invalid, otherwise empty
     */
    public Optional<Text> defaultValue(K key, T value) {
        return view(key).defaultValue(value);
    }

    /**
     * Get the value for the given key.
     *
     * @param key the key of the config value view
     * @return the value for the given key
     */
    public T value(K key) {
        return view(key).value();
    }

    /**
     * Set the value for the given key.
     *
     * @param key the key of the config value view
     * @param value the value to set
     * @return an Optional containing the error message if the value is invalid, otherwise empty
     */
    public Optional<Text> value(K key, T value) {
        return view(key).value(value);
    }
}
