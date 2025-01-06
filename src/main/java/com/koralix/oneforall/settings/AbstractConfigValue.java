package com.koralix.oneforall.settings;

import com.koralix.oneforall.settings.registry.ConfigValueEntry;
import com.koralix.oneforall.utils.OnceCell;
import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractConfigValue<T> implements ConfigValue<T> {
    private final OnceCell<ConfigValueEntry<T>> entry = new OnceCell<>();
    private final T nominalValue;
    private final Codec<T> codec;
    private final ConfigValidator<T> validator;

    public AbstractConfigValue(T nominalValue, Codec<T> codec, ConfigValidator<T> validator) {
        this.nominalValue = nominalValue;
        this.codec = codec;
        this.validator = validator;
    }

    @Override
    public ConfigValueEntry<T> entry() {
        return entry.get();
    }

    @ApiStatus.Internal
    public void entry(ConfigValueEntry<T> entry) {
        this.entry.set(entry);
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
