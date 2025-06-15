package com.koralix.oneforall.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public final class Functions {
    private Functions() {
        // Prevent instantiation
    }

    @Contract(pure = true)
    public static <T, R, E extends Throwable> @NotNull Function<T, R> tryCatch(FallibleFunction<T, R, E> function) {
        return (T t) -> {
            try {
                return function.apply(t);
            } catch (Throwable e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else if (e instanceof Error) {
                    throw (Error) e;
                } else {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @FunctionalInterface
    public interface FallibleFunction<T, R, E extends Throwable> {
        R apply(T value) throws E;
    }
}
