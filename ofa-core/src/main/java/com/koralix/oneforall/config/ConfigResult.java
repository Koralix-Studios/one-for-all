package com.koralix.oneforall.config;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ConfigResult<T> {
    @Contract("_ -> new")
    static <T> @NotNull ConfigResult<T> ok(T value) {
        return new OkObserve<>(value);
    }

    @Contract("_, _ -> new")
    static <T> @NotNull ConfigResult<T> ok(T oldValue, T newValue) {
        return new ValidChange<>(oldValue, newValue);
    }

    Optional<T> get();

    boolean isError();
    default boolean isOk() {
        return !isError();
    }

    default ConfigResult<T> and(ConfigResult<T> other) {
        if (isError()) return this;
        if (other.isError()) return other;
        return this.get().isPresent() ? this : other.get().isPresent() ? other : this;
    }

    default ConfigResult<T> replace(T value) {
        if (isError()) return this;
        return new OkObserve<>(value);
    }

    record OkObserve<T>(
            T value
    ) implements ConfigResult<T> {
        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull Optional<T> get() {
            return Optional.of(value);
        }

        @Override
        public boolean isError() {
            return false;
        }
    }

    record ValidChange<T>(
            T oldValue,
            T newValue
    ) implements ConfigResult<T> {
        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull Optional<T> get() {
            return Optional.of(newValue);
        }

        @Override
        public boolean isError() {
            return false;
        }
    }

    record InvalidChange<T>(
            T oldValue,
            T newValue
    ) implements ConfigResult<T> {
        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull Optional<T> get() {
            return Optional.of(oldValue);
        }

        @Override
        public boolean isError() {
            return true;
        }
    }

    record ForbidObserve<T>(
            ConfigActor actor
    ) implements ConfigResult<T> {
        @Contract(pure = true)
        @Override
        public @NotNull Optional<T> get() {
            return Optional.empty();
        }

        @Override
        public boolean isError() {
            return true;
        }
    }

    record ForbidChange<T>(
            ConfigActor actor
    ) implements ConfigResult<T> {
        @Contract(pure = true)
        @Override
        public @NotNull Optional<T> get() {
            return Optional.empty();
        }

        @Override
        public boolean isError() {
            return true;
        }
    }

    record OkChange<T>(
            ConfigActor actor
    ) implements ConfigResult<T> {
        @Contract(pure = true)
        @Override
        public @NotNull Optional<T> get() {
            return Optional.empty();
        }

        @Override
        public boolean isError() {
            return false;
        }
    }
}
