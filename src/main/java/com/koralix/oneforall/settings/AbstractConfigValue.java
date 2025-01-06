package com.koralix.oneforall.settings;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractConfigValue<T> implements ConfigValue<T> {
    private final T nominalValue;
    private final Codec<T> codec;
    private final ConfigValidator<T> validator;

    public AbstractConfigValue(T nominalValue, Codec<T> codec, ConfigValidator<T> validator) {
        this.nominalValue = nominalValue;
        this.codec = codec;
        this.validator = validator;
    }

    @Override
    public T nominalValue() {
        return nominalValue;
    }

    @Override
    public Codec<T> codec() {
        return codec;
    }

    @Override
    public Optional<Text> validate(T value, Consumer<T> action) {
        Text result = validator.test(value);
        if (result != null) return Optional.of(result);
        action.accept(value);
        return Optional.empty();
    }
}
