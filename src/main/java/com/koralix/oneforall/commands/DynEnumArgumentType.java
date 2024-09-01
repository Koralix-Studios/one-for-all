package com.koralix.oneforall.commands;

import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DynEnumArgumentType {
    @Contract("_ -> new")
    public static <T extends Enum<T> & StringIdentifiable> @NotNull EnumArgumentType<T> create(T[] values) {
        Supplier<T[]> supplier = () -> values;
        return new EnumArgumentType<>(StringIdentifiable.createCodec(supplier), supplier);
    }

    @Contract("_ -> new")
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & StringIdentifiable> @NotNull EnumArgumentType<T> create(@NotNull Class<?> clazz) {
        return create((T[]) clazz.getEnumConstants());
    }
}
