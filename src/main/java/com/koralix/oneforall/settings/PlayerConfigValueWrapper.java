package com.koralix.oneforall.settings;

import com.mojang.serialization.Codec;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * A class to store a config value, with a nominal value, a default value, and a current value.
 * The nominal value is the value set by default.
 * The default value is the value that the config value will be set to if the user resets it.
 * The value is the current value of the config value.
 * @param <T> the type of the config value
 */
public class PlayerConfigValueWrapper<T> implements ConfigValue<T> {
    private final Class<T> clazz;
    private final T nominalValue;
    private final ConfigValidator<T> validator;
    private final Predicate<ServerCommandSource> permission;
    private final Codec<T> codec;

    SettingEntry<T> entry = null;

    private final Map<UUID, T> defaultValue = new HashMap<>();
    private final Map<UUID, T> value = new HashMap<>();

    PlayerConfigValueWrapper(
            @NotNull Class<T> clazz,
            T nominalValue,
            @NotNull ConfigValidator<T> validator,
            @NotNull Predicate<ServerCommandSource> permission,
            Codec<T> codec
    ) {
        Text err = validator.test(nominalValue);
        if (err != null) throw new IllegalArgumentException(err.getString());

        this.clazz = clazz;
        this.nominalValue = nominalValue;
        this.validator = validator;
        this.permission = permission;
        this.codec = codec;
    }

    @Override
    public SettingsRegistry registry() {
        return this.entry.registry();
    }

    @Override
    public SettingEntry<T> entry() {
        return this.entry;
    }

    @Override
    public boolean permission(ServerCommandSource source) {
        return this.permission.test(source);
    }

    @Override
    public void reset() {
        this.value.clear();
    }

    @Override
    public void restore() {
        this.value.clear();
        this.defaultValue.clear();
    }

    @Override
    public T nominalValue() {
        return this.nominalValue;
    }

    @Override
    public T defaultValue() {
        throw new UnsupportedOperationException("Cannot get default value for player config value");
    }

    public T defaultValue(UUID source) {
        return this.defaultValue.getOrDefault(source, this.nominalValue);
    }

    @Override
    public Text defaultValue(T value) {
        throw new UnsupportedOperationException("Cannot set default value for player config value");
    }

    public Text defaultValue(UUID source, T value) {
        if (value == this.nominalValue) {
            this.defaultValue.remove(source);
            return null;
        }
        return this.tested(value, () -> this.defaultValue.put(source, value));
    }

    @Override
    public T value() {
        throw new UnsupportedOperationException("Cannot get value for player config value");
    }

    public T value(UUID source) {
        return this.value.getOrDefault(source, this.defaultValue(source));
    }

    @Override
    public Text value(T value) {
        throw new UnsupportedOperationException("Cannot set value for player config value");
    }

    public Text value(UUID source, T value) {
        if (value == this.defaultValue(source)) {
            this.value.remove(source);
            return null;
        }
        return this.tested(value, () -> this.value.put(source, value));
    }

    @Override
    public Class<T> clazz() {
        return this.clazz;
    }

    @Override
    public Codec<T> codec() {
        return this.codec;
    }

    private @Nullable Text tested(T value, Runnable action) {
        Text err = this.validator.test(value);
        if (err != null) return err;
        action.run();
        return null;
    }
}
