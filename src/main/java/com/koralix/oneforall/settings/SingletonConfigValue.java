package com.koralix.oneforall.settings;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Predicate;

public class SingletonConfigValue<T> extends AbstractConfigValue<T> implements MonoConfigValue<T> {
    private T defaultValue;
    private T value;

    public SingletonConfigValue(
            T nominalValue,
            Codec<T> codec,
            ConfigValueAdapter.Command<T, ?> command,
            ConfigValidator<T> validator,
            Predicate<CommandSource> permission
    ) {
        super(nominalValue, codec, command, validator, permission);
        this.defaultValue = nominalValue;
        this.value = nominalValue;
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

    @Override
    public ConfigValueView<T> view(CommandContext<? extends CommandSource> context) {
        return this;
    }

    public static final class Builder<T> extends AbstractConfigValueBuilder<T, SingletonConfigValue<T>> {
        public Builder(T nominalValue, Codec<T> codec, ConfigValueAdapter.Command<T, ?> command) {
            super(nominalValue, codec, command);
        }

        @Override
        public SingletonConfigValue<T> build() {
            return new SingletonConfigValue<>(nominalValue, codec, command, validator, permission);
        }
    }
}
