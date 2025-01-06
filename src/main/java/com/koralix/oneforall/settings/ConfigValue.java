package com.koralix.oneforall.settings;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Consumer;

public interface ConfigValue<T> {
    /**
     * The nominal id of this config id.
     * This id is used when the config id is not present in the config file.
     *
     * @return the nominal id of this config id
     */
    T nominalValue();

    /**
     * The codec of this config id.
     * This codec is used to serialize and deserialize the config id.
     *
     * @return the codec of this config id
     */
    Codec<T> codec();

    /**
     * Validate the given id.
     * If the id is valid, the id is accepted and the action is performed.
     * If the id is invalid, the id is rejected and the error message is returned.
     *
     * @param value the id to validate
     * @param action the action to perform if the id is valid
     * @return the error message if the id is invalid, otherwise empty
     */
    Optional<Text> validate(T value, Consumer<T> action);

    static <T> SingletonConfigValue.Builder<T> singleton(T nominalValue, Codec<T> codec) {
        return new SingletonConfigValue.Builder<>(nominalValue, codec);
    }

    static <T> PlayerConfigValue.Builder<T> player(T nominalValue, Codec<T> codec) {
        return new PlayerConfigValue.Builder<>(nominalValue, codec);
    }
}
