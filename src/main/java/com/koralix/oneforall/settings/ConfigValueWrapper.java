package com.koralix.oneforall.settings;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * A class to store a config value, with a nominal value, a default value, and a current value.
 * The nominal value is the value set by default.
 * The default value is the value that the config value will be set to if the user resets it.
 * The value is the current value of the config value.
 * @param <T> the type of the config value
 */
public class ConfigValueWrapper<T> implements ConfigValue<T> {
    private final Class<T> clazz;
    private final T nominalValue;
    private final Predicate<T> validator;
    private final Predicate<ServerCommandSource> permission;

    Identifier registry;
    Identifier id;

    private T defaultValue;
    private T value;

    ConfigValueWrapper(
            @NotNull Identifier registry,
            @NotNull Identifier id,
            @NotNull Class<T> clazz,
            T nominalValue,
            @NotNull Predicate<T> validator,
            @NotNull Predicate<ServerCommandSource> permission
    ) {
        if (!validator.test(nominalValue)) throw new IllegalArgumentException("Invalid nominal value");

        this.clazz = clazz;
        this.nominalValue = nominalValue;
        this.validator = validator;
        this.permission = permission;

        this.registry = registry;
        this.id = id;

        this.defaultValue = nominalValue;
        this.value = nominalValue;
    }

    @Override
    public Identifier registry() {
        return this.registry;
    }

    @Override
    public Identifier id() {
        return this.id;
    }

    @Override
    public boolean permission(ServerCommandSource source) {
        return this.permission.test(source);
    }

    @Override
    public T nominalValue() {
        return this.nominalValue;
    }

    @Override
    public T defaultValue() {
        return this.defaultValue;
    }

    @Override
    public boolean defaultValue(T value) {
        if (!this.test(value)) return false;
        this.defaultValue = value;
        return true;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public boolean value(T value) {
        if (!this.test(value)) return false;
        this.value = value;
        return true;
    }

    @Override
    public Class<T> clazz() {
        return this.clazz;
    }

    private boolean test(T value) {
        return this.validator.test(value);
    }
}
