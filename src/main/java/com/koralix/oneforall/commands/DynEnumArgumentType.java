package com.koralix.oneforall.commands;

import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DynEnumArgumentType<T extends Enum<T> & StringIdentifiable> extends EnumArgumentType<T> {
    protected DynEnumArgumentType(Supplier<T[]> valuesSupplier) {
        super(StringIdentifiable.createCodec(valuesSupplier), valuesSupplier);
    }

    @Contract("_ -> new")
    public static <T extends Enum<T> & StringIdentifiable> @NotNull DynEnumArgumentType<T> create(T[] values) {
        return new DynEnumArgumentType<>(() -> values);
    }

    @Contract("_ -> new")
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & StringIdentifiable> @NotNull DynEnumArgumentType<T> create(@NotNull Class<?> clazz) {
        return new DynEnumArgumentType<>(() -> (T[]) clazz.getEnumConstants());
    }
}
