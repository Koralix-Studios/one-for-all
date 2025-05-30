package com.koralix.oneforall.config;

import com.koralix.oneforall.OneForAll;
import com.mojang.brigadier.Message;
import net.minecraft.text.Text;
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

    @NotNull Message message();

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

        @Override
        public @NotNull Message message() {
            return Text.stringifiedTranslatable("command." + OneForAll.MOD_ID + ".config.observe.ok", value);
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

        @Override
        public @NotNull Message message() {
            return Text.stringifiedTranslatable("command." + OneForAll.MOD_ID + ".config.change.valid", oldValue, newValue);
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

        @Override
        public @NotNull Message message() {
            return Text.stringifiedTranslatable("command." + OneForAll.MOD_ID + ".config.change.invalid", oldValue, newValue);
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

        @Override
        public @NotNull Message message() {
            return Text.stringifiedTranslatable("command." + OneForAll.MOD_ID + ".config.observe.forbidden", actor);
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

        @Override
        public @NotNull Message message() {
            return Text.stringifiedTranslatable("command." + OneForAll.MOD_ID + ".config.change.forbidden", actor);
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

        @Override
        public @NotNull Message message() {
            return Text.stringifiedTranslatable("command." + OneForAll.MOD_ID + ".config.change.ok", actor);
        }
    }
}
