package com.koralix.oneforall.settings;

import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ConfigValueBuilder<T> {
    private final Class<T> clazz;
    private final T value;
    private Predicate<T> validator = null;
    private Predicate<ServerCommandSource> permission = null;

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

    public ConfigValueBuilder<T> permission(Predicate<ServerCommandSource> permission) {
        this.permission = permission;
        return this;
    }

    public ConfigValue<T> build() {
        return new ConfigValueWrapper<>(
                clazz,
                value,
                validator == null ? t -> true : validator,
                permission == null ? t -> true : permission
        );
    }
}
