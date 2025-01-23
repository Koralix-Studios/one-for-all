package com.koralix.oneforall.settings;

import com.mojang.serialization.Codec;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.function.Predicate;

public abstract class AbstractConfigValueBuilder<T, C extends ConfigValue<T>> {
    protected final T nominalValue;
    protected final Codec<T> codec;
    protected final ConfigValueAdapter.Command<T, ?> command;

    protected ConfigValidator<T> validator = value -> null;
    protected Predicate<CommandSource> permission = source -> true;

    public AbstractConfigValueBuilder(T nominalValue, Codec<T> codec, ConfigValueAdapter.Command<T, ?> command) {
        this.nominalValue = nominalValue;
        this.codec = codec;
        this.command = command;
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
        return test(predicate, Text.of("Invalid value"));
    }

    public AbstractConfigValueBuilder<T, C> permission(Predicate<CommandSource> predicate) {
        this.permission = this.permission.and(predicate);
        return this;
    }

    public abstract C build();
}
