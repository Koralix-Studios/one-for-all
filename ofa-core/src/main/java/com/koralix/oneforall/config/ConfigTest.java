package com.koralix.oneforall.config;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ConfigTest<T> {
    @Contract(value = "_, _, _ -> new", pure = true)
    static <T> @NotNull ConfigTest<T> create(
            @NotNull Function<ConfigActor, ConfigResult<T>> canObserve,
            @NotNull BiFunction<T, T, ConfigResult<T>> canChangeValue,
            @NotNull Function<ConfigActor, ConfigResult<T>> canChangeKey
    ) {
        return new ConfigTest<T>() {
            @Override
            public ConfigResult<T> canObserve(ConfigActor actor) {
                return canObserve.apply(actor);
            }

            @Override
            public ConfigResult<T> canChange(T oldValue, T newValue) {
                return canChangeValue.apply(oldValue, newValue);
            }

            @Override
            public ConfigResult<T> canChange(ConfigActor actor) {
                return canChangeKey.apply(actor);
            }
        };
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static <T> @NotNull ConfigTest<T> of(
            @NotNull Predicate<ConfigActor> canObserve,
            @NotNull BiPredicate<T, T> canChangeValue,
            @NotNull Predicate<ConfigActor> canChangeKey
    ) {
        return create(
                actor -> canObserve.test(actor) ?
                        new ConfigResult.OkObserve<>(null) :
                        new ConfigResult.ForbidObserve<>(actor),
                (oldValue, newValue) -> canChangeValue.test(oldValue, newValue) ?
                        new ConfigResult.ValidChange<>(oldValue, newValue) :
                        new ConfigResult.InvalidChange<>(oldValue, newValue),
                actor -> canChangeKey.test(actor) ?
                        new ConfigResult.AllowedChange<>(actor) :
                        new ConfigResult.ForbidChange<>(actor)
        );
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

    ConfigResult<T> canObserve(ConfigActor actor);

    ConfigResult<T> canChange(T oldValue, T newValue);

    ConfigResult<T> canChange(ConfigActor actor);

    default ConfigTest<T> and(ConfigTest<T> other) {
        return create(
                actor -> this.canObserve(actor).and(other.canObserve(actor)),
                (o, n) -> this.canChange(o, n).and(other.canChange(o, n)),
                actor -> this.canChange(actor).and(other.canChange(actor))
        );
    }
}
