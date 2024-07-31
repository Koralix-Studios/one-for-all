package com.koralix.oneforall.settings;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ConfigValueBuilder<T> {
    private final Class<T> clazz;
    private final T value;
    private Predicate<T> validator = null;
    private Identifier registry = null;
    private Identifier id = null;

    @SuppressWarnings("unchecked")
    ConfigValueBuilder(@NotNull T value) {
        this.clazz = (Class<T>) value.getClass();
        this.value = value;
    }

    ConfigValueBuilder(@NotNull Class<T> clazz) {
        this.clazz = clazz;
        this.value = null;
    }

    public ConfigValueBuilder<T> test(Predicate<T> validator) {
        if (validator == null) return this;

        if (this.validator == null) {
            this.validator = validator;
        } else {
            this.validator = this.validator.and(validator);
        }

        return this;
    }

    public ConfigValueBuilder<T> registry(@NotNull Identifier registry) {
        this.registry = registry;
        return this;
    }

    public ConfigValueBuilder<T> id(@NotNull Identifier id) {
        this.id = id;
        return this;
    }

    public ConfigValue<T> build() {
        return new ConfigValueWrapper<>(
                registry,
                id,
                clazz,
                value,
                validator == null ? t -> true : validator
        );
    }
}
