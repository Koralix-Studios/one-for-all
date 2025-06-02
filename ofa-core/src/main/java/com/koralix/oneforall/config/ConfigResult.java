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

    @Contract("_ -> new")
    static <T> @NotNull ConfigResult<T> unchanged(T value) {
        return new Unchanged<>(value);
    }

    Optional<T> get();

    boolean isError();
    default boolean isOk() {
        return !isError();
    }
    boolean isChange();

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
        public boolean isChange() {
            return false;
        }

        @Override
        public @NotNull Message message() {
            return Text.stringifiedTranslatable("command." + OneForAll.id() + ".config.observe.ok", value);
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
        public boolean isChange() {
            return true;
        }

        @Override
        public @NotNull Message message() {
            return Text.stringifiedTranslatable("command." + OneForAll.id() + ".config.change.valid", oldValue, newValue);
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
        public boolean isChange() {
            return false;
        }

        @Override
        public @NotNull Message message() {
            return Text.stringifiedTranslatable("command." + OneForAll.id() + ".config.change.invalid", oldValue, newValue);
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
        public boolean isChange() {
            return false;
        }

        @Override
        public @NotNull Message message() {
            return Text.stringifiedTranslatable("command." + OneForAll.id() + ".config.observe.forbidden", actor);
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
        public boolean isChange() {
            return false;
        }

        @Override
        public @NotNull Message message() {
            return Text.stringifiedTranslatable("command." + OneForAll.id() + ".config.change.forbidden", actor);
        }
    }

    record AllowedChange<T>(
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
        public boolean isChange() {
            return false;
        }

        @Override
        public @NotNull Message message() {
            return Text.stringifiedTranslatable("command." + OneForAll.id() + ".config.change.allowed", actor);
        }
    }

    record Unchanged<T>(T value) implements ConfigResult<T> {
        @Contract(pure = true)
        @Override
        public @NotNull Optional<T> get() {
            return Optional.of(value);
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public boolean isChange() {
            return false;
        }

        @Override
        public @NotNull Message message() {
            return Text.stringifiedTranslatable("command." + OneForAll.id() + ".config.unchanged", value);
        }
    }
}
