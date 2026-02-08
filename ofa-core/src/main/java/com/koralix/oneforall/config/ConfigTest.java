package com.koralix.oneforall.config;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public record ConfigTest<T>(
        Predicate<T> test,
        Predicate<ConfigActor> read,
        Predicate<ConfigActor> write
) {
    @Contract("_, _, _ -> new")
    public static <T> @NotNull ConfigTest<T> of(
            Predicate<T> test,
            Predicate<ConfigActor> read,
            Predicate<ConfigActor> write
    ) {
        return new ConfigTest<>(
                test == null ? t -> true : test,
                read == null ? actor -> true : read,
                write == null ? actor -> true : write
        );
    }

    public boolean test(@NotNull T value) {
        return test.test(value);
    }

    public boolean read(@NotNull ConfigActor actor) {
        return read.test(actor);
    }

    public boolean write(@NotNull ConfigActor actor) {
        return write.test(actor);
    }
}
