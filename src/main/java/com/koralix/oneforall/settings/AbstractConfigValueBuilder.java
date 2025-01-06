package com.koralix.oneforall.settings;

import com.mojang.serialization.Codec;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Predicate;

public abstract class AbstractConfigValueBuilder<T, C extends ConfigValue<T>> {
    protected final T nominalValue;
    protected final Codec<T> codec;

    protected ConfigValidator<T> validator = value -> null;
    protected Predicate<CommandSource> permission = source -> true;

    public AbstractConfigValueBuilder(T nominalValue, Codec<T> codec) {
        this.nominalValue = nominalValue;
        this.codec = codec;
    }

    public AbstractConfigValueBuilder<T, C> test(ConfigValidator<T> validator) {
        this.validator = this.validator.and(validator);
        return this;
    }

    public AbstractConfigValueBuilder<T, C> test(Predicate<T> predicate, Text errorMessage) {
        ConfigValidator<T> validator = value -> predicate.test(value) ? null : errorMessage;
        return test(validator);
    }

    public AbstractConfigValueBuilder<T, C> test(Predicate<T> predicate) {
        return test(predicate, Text.of("Invalid id"));
    }

    public AbstractConfigValueBuilder<T, C> permission(Predicate<CommandSource> predicate) {
        this.permission = this.permission.and(predicate);
        return this;
    }

    public AbstractConfigValueBuilder<T, C> permission(String permission) {
        throw new NotImplementedException("Not implemented yet");
    }

    public abstract C build();
}
