package com.koralix.oneforall.settings;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;

import java.util.Optional;

public abstract class AbstractMultiConfigValue<K, T> extends AbstractConfigValue<T> implements MultiConfigValue<K, T> {
    public AbstractMultiConfigValue(T nominalValue, Codec<T> codec, ConfigValidator<T> validator) {
        super(nominalValue, codec, validator);
    }

    /**
     * Get the default id for the given id.
     *
     * @param key the id of the config id view
     * @return the default id for the given id
     */
    public T defaultValue(K key) {
        return view(key).defaultValue();
    }

    /**
     * Set the default id for the given id.
     *
     * @param key the id of the config id view
     * @param value the default id to set
     * @return an Optional containing the error message if the id is invalid, otherwise empty
     */
    public Optional<Text> defaultValue(K key, T value) {
        return view(key).defaultValue(value);
    }

    /**
     * Get the id for the given id.
     *
     * @param key the id of the config id view
     * @return the id for the given id
     */
    public T value(K key) {
        return view(key).value();
    }

    /**
     * Set the id for the given id.
     *
     * @param key the id of the config id view
     * @param value the id to set
     * @return an Optional containing the error message if the id is invalid, otherwise empty
     */
    public Optional<Text> value(K key, T value) {
        return view(key).value(value);
    }
}
