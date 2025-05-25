package com.koralix.oneforall.config;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface ConfigTest<T> {
    @Contract(value = "_, _, _ -> new", pure = true)
    static <T> @NotNull ConfigTest<T> of(
            @NotNull Predicate<ConfigActor> canObserve,
            @NotNull BiPredicate<T, T> canChangeValue,
            @NotNull Predicate<ConfigActor> canChangeKey
    ) {
        return new ConfigTest<T>() {
            @Override
            public boolean canObserve(ConfigActor actor) {
                return canObserve.test(actor);
            }

            @Override
            public boolean canChange(T oldValue, T newValue) {
                return canChangeValue.test(oldValue, newValue);
            }

            @Override
            public boolean canChange(ConfigActor actor) {
                return canChangeKey.test(actor);
            }
        };
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <T> @NotNull ConfigTest<T> of(
            @NotNull Predicate<T> canChangeValue,
            @NotNull Predicate<ConfigActor> canChangeKey
    ) {
        return of(actor -> true, (o, n) -> canChangeValue.test(n), canChangeKey);
    }

    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull ConfigTest<T> of(
            @NotNull Predicate<T> canChangeValue
    ) {
        return of(canChangeValue, actor -> true);
    }

    @Contract(value = " -> new", pure = true)
    static <T> @NotNull ConfigTest<T> tauto() {
        return of(actor -> true, (o, n) -> true, actor -> true);
    }

    boolean canObserve(ConfigActor actor);
    boolean canChange(T oldValue, T newValue);
    boolean canChange(ConfigActor actor);

    default ConfigTest<T> and(ConfigTest<T> other) {
        return of(
                actor -> this.canObserve(actor) && other.canObserve(actor),
                (o, n) -> this.canChange(o, n) && other.canChange(o, n),
                actor -> this.canChange(actor) && other.canChange(actor)
        );
    }
}
