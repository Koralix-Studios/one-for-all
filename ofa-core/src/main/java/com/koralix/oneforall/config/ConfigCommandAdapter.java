package com.koralix.oneforall.config;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiConsumer;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public interface ConfigCommandAdapter<T> {
    @NotNull ConfigCommandAdapter<Boolean> BOOLEAN = new ConfigCommandAdapter<>() {
        @Override
        public <S> void adapt(
                @NotNull String name,
                @NotNull ArgumentBuilder<S, ?> parent,
                @NotNull BiConsumer<ArgumentBuilder<S, ?>, ArgumentValueGetter<Boolean>> consumer
        ) {
            RequiredArgumentBuilder<S, Boolean> argument = argument(name, BoolArgumentType.bool());
            consumer.accept(argument, BoolArgumentType::getBool);
            parent.then(argument);
        }
    };

    static @NotNull ConfigCommandAdapter<Integer> integer(int minValue, int maxValue) {
        return new ConfigCommandAdapter<>() {
            @Override
            public <S> void adapt(
                    @NotNull String name,
                    @NotNull ArgumentBuilder<S, ?> parent,
                    @NotNull BiConsumer<ArgumentBuilder<S, ?>, ArgumentValueGetter<Integer>> consumer
            ) {
                RequiredArgumentBuilder<S, Integer> argument = argument(name, IntegerArgumentType.integer(minValue, maxValue));
                consumer.accept(argument, IntegerArgumentType::getInteger);
                parent.then(argument);
            }
        };
    }

    @Contract("_ -> new")
    static <E extends Enum<E> & StringIdentifiable> @NotNull ConfigCommandAdapter<E> ofEnum(Class<E> enumClass) {
        return new ConfigCommandAdapter<>() {
            @Override
            public <S> void adapt(
                    @NotNull String name,
                    @NotNull ArgumentBuilder<S, ?> parent,
                    @NotNull BiConsumer<ArgumentBuilder<S, ?>, ArgumentValueGetter<E>> consumer
            ) {
                for (E constant : enumClass.getEnumConstants()) {
                    LiteralArgumentBuilder<S> literal = literal(constant.asString());
                    consumer.accept(literal, ArgumentValueGetter.unit(constant));
                    parent.then(literal);
                }
            }
        };
    }

    <S> void adapt(
            @NotNull String name,
            @NotNull ArgumentBuilder<S, ?> parent,
            @NotNull BiConsumer<ArgumentBuilder<S, ?>, ArgumentValueGetter<T>> consumer
    );

    default @NotNull ConfigCommandAdapter<Optional<T>> optional() {
        return new ConfigCommandAdapter<>() {
            @Override
            public <S> void adapt(
                    @NotNull String name,
                    @NotNull ArgumentBuilder<S, ?> parent,
                    @NotNull BiConsumer<ArgumentBuilder<S, ?>, ArgumentValueGetter<Optional<T>>> consumer
            ) {
                LiteralArgumentBuilder<S> some = literal("some");
                ConfigCommandAdapter.this.adapt(name, some, (builder, getter) -> consumer.accept(builder, getter.optional()));
                LiteralArgumentBuilder<S> none = literal("none");
                consumer.accept(none, ArgumentValueGetter.unit(Optional.empty()));
                parent.then(some).then(none);
            }
        };
    }

    @FunctionalInterface
    interface ArgumentValueGetter<T> {
        @Contract(pure = true)
        static <T> @NotNull ArgumentValueGetter<T> unit(T constant) {
            return (context, name) -> constant;
        }

        T get(CommandContext<?> context, String name) throws CommandSyntaxException;

        default ArgumentValueGetter<Optional<T>> optional() {
            return (context, name) -> Optional.of(get(context, name));
        }
    }

    @FunctionalInterface
    interface Getter<S extends CommandSource, K, T, B> {
        int get(CommandContext<S> context, ConfigValue<K, T, B> configValue, K backend) throws CommandSyntaxException;
    }

    @FunctionalInterface
    interface Setter<S extends CommandSource, K, T, B> {
        int set(CommandContext<S> context, ConfigValue<K, T, B> configValue, K backend, T value) throws CommandSyntaxException;
    }
}
