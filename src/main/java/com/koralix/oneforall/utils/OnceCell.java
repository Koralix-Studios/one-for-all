package com.koralix.oneforall.utils;

import java.util.function.Supplier;

public class OnceCell<T> {
    private T value;
    private boolean filled;

    public OnceCell() {
        this.filled = false;
    }

    public T get() {
        if (!filled) throw new IllegalStateException("Cell is empty");
        return value;
    }

    public void set(T value) {
        if (filled) throw new IllegalStateException("Cell is already filled");
        this.value = value;
        this.filled = true;
    }

    public T getOrSet(Supplier<T> supplier) {
        if (filled) return get();
        T value = supplier.get();
        set(value);
        return value;
    }

    public boolean filled() {
        return filled;
    }
}
