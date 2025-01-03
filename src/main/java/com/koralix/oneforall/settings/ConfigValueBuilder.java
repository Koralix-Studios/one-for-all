package com.koralix.oneforall.settings;

import com.mojang.serialization.Codec;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ConfigValueBuilder<T> {
    private final Class<T> clazz;
    private final T value;
    private final Codec<T> codec;
    private ConfigValidator<T> validator = null;
    private Predicate<ServerCommandSource> permission = null;

    @SuppressWarnings("unchecked")
    ConfigValueBuilder(@NotNull T value, @NotNull Codec<T> codec) {
        this((Class<T>) value.getClass(), value, codec);
    }

    ConfigValueBuilder(@NotNull Class<T> clazz, @NotNull Codec<T> codec) {
        this(clazz, null, codec);
    }

    private ConfigValueBuilder(@NotNull Class<T> clazz, @Nullable T value, @NotNull Codec<T> codec) {
        this.clazz = clazz;
        this.value = value;
        this.codec = codec;
    }

    public ConfigValueBuilder<T> test(ConfigValidator<T> validator) {
        if (validator == null) return this;

        if (this.validator == null) {
            this.validator = validator;
        } else {
            this.validator = this.validator.and(validator);
        }

        return this;
    }

    public ConfigValueBuilder<T> test(Predicate<T> validator, Text message) {
        ConfigValidator<T> test = value -> validator.test(value) ? null : message;
        return test(test);
    }

    public ConfigValueBuilder<T> test(Predicate<T> validator) {
        return test(validator, Text.of("Invalid value"));
    }

    public ConfigValueBuilder<T> permission(Predicate<ServerCommandSource> permission) {
        this.permission = permission;
        return this;
    }

    public ConfigValue<T> build() {
        return new ConfigValueWrapper<>(
                clazz,
                value,
                validator == null ? t -> null : validator,
                permission == null ? t -> true : permission,
                codec
        );
    }

    public PlayerConfigValueWrapper<T> player() {
        return new PlayerConfigValueWrapper<>(
                clazz,
                value,
                validator == null ? t -> null : validator,
                permission == null ? t -> true : permission,
                codec
        );
    }
}
