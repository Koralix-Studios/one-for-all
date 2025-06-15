package com.koralix.oneforall.config.adapter;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiConsumer;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public interface CommandAdapter<T> {
    @Contract("_ -> new")
    static <E extends Enum<E> & StringIdentifiable> @NotNull CommandAdapter<E> ofEnum(Class<E> enumClass) {
        return new CommandAdapter<>() {
            @Override
            public <S> void adapt(String name, ArgumentBuilder<S, ?> parent, BiConsumer<ArgumentBuilder<S, ?>, CommandAdapterGetter<E>> consumer) {
                for (E constant : enumClass.getEnumConstants()) {
                    LiteralArgumentBuilder<S> literal = literal(constant.asString());
                    consumer.accept(literal, CommandAdapterGetter.unit(constant));
                    parent.then(literal);
                }
            }
        };
    }

    static @NotNull CommandAdapter<Boolean> bool() {
        return new CommandAdapter<>() {
            @Override
            public <S> void adapt(String name, ArgumentBuilder<S, ?> parent, BiConsumer<ArgumentBuilder<S, ?>, CommandAdapterGetter<Boolean>> consumer) {
                RequiredArgumentBuilder<S, Boolean> argument = argument(name, BoolArgumentType.bool());
                consumer.accept(argument, BoolArgumentType::getBool);
                parent.then(argument);
            }
        };
    }

    <S> void adapt(String name, ArgumentBuilder<S, ?> parent, BiConsumer<ArgumentBuilder<S, ?>, CommandAdapterGetter<T>> consumer);

    default @NotNull CommandAdapter<Optional<T>> optional() {
        return new CommandAdapter<>() {
            @Override
            public <S> void adapt(String name, ArgumentBuilder<S, ?> parent, BiConsumer<ArgumentBuilder<S, ?>, CommandAdapterGetter<Optional<T>>> consumer) {
                LiteralArgumentBuilder<S> some = literal("some");
                CommandAdapter.this.adapt(name, some, (builder, getter) -> consumer.accept(builder, getter.optional()));
                LiteralArgumentBuilder<S> none = literal("none");
                consumer.accept(none, CommandAdapterGetter.unit(Optional.empty()));
                parent.then(some).then(none);
            }
        };
    }

    @FunctionalInterface
    interface CommandAdapterGetter<T> {
        @Contract(pure = true)
        static <T> @NotNull CommandAdapterGetter<T> unit(T constant) {
            return (context, name) -> constant;
        }

        T get(CommandContext<?> context, String name) throws CommandSyntaxException;

        default CommandAdapterGetter<Optional<T>> optional() {
            return (context, name) -> Optional.of(get(context, name));
        }
    }
}
