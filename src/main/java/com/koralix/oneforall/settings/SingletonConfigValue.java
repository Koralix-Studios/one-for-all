package com.koralix.oneforall.settings;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;

import java.util.Optional;

public class SingletonConfigValue<T> extends AbstractConfigValue<T> implements MonoConfigValue<T> {
    private T defaultValue;
    private T value;

    public SingletonConfigValue(T nominalValue, Codec<T> codec, ConfigValidator<T> validator) {
        super(nominalValue, codec, validator);
    }

    @Override
    public T defaultValue() {
        return defaultValue;
    }

    @Override
    public Optional<Text> defaultValue(T value) {
        return validate(value, v -> this.defaultValue = v);
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public Optional<Text> value(T value) {
        return validate(value, v -> this.value = v);
    }

    public static final class Builder<T> extends AbstractConfigValueBuilder<T, SingletonConfigValue<T>> {
        public Builder(T nominalValue, Codec<T> codec) {
            super(nominalValue, codec);
        }

        @Override
        public SingletonConfigValue<T> build() {
            return new SingletonConfigValue<>(nominalValue, codec, validator);
        }
    }
}
