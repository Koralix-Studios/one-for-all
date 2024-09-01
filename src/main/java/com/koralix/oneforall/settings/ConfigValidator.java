package com.koralix.oneforall.settings;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ConfigValidator<T> {
    Text test(T value);

    default ConfigValidator<T> and(@NotNull ConfigValidator<T> other) {
        return value -> {
            Text result = test(value);
            return result == null ? other.test(value) : result;
        };
    }

    default ConfigValidator<T> or(@NotNull ConfigValidator<T> other) {
        return value -> {
            Text a = test(value);
            if (a == null) return null;
            Text b = other.test(value);
            return b == null ? null : Text.literal("- ").append(a).append("\n- ").append(b);
        };
    }
}
