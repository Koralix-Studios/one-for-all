package com.koralix.oneforall.util.wrap;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class OnceCell<T> {
    private boolean initialized;
    private T value;

    public OnceCell() {
        this.initialized = false;
        this.value = null;
    }

    public T get() {
        if (!initialized) {
            throw new IllegalStateException("Value has not been initialized yet.");
        }
        return value;
    }

    public void set(@NotNull T value) {
        if (initialized) {
            throw new IllegalStateException("Value has already been initialized.");
        }
        this.value = value;
        this.initialized = true;
    }

    public T getOrSet(@NotNull T value) {
        if (!initialized) {
            this.value = value;
            this.initialized = true;
        }
        return this.value;
    }

    public Optional<T> optional() {
        return Optional.ofNullable(value);
    }
}
