package com.koralix.oneforall.settings;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ConfigValueAdapter<T, F, I> {
    I into(T value);

    T from(F value);

    interface Command<T, A> extends ConfigValueAdapter<T, CommandContext<? extends CommandSource>, ArgumentType<A>> {
        interface ArgumentFactory<S, A> extends BiFunction<String, ArgumentType<A>, RequiredArgumentBuilder<S, A>> {}

        Command<Boolean, Boolean> BOOLEAN = of(
                BoolArgumentType::bool,
                BoolArgumentType::getBool
        );

        <S extends CommandSource> RequiredArgumentBuilder<S, A> argument(
                ArgumentFactory<S, A> factory
        );

        static <T extends Enum<T>> Command<T, String> enumOf(Class<T> clazz) {
            return enumOf(
                    clazz.getEnumConstants(),
                    t -> t.name().toLowerCase(),
                    s -> Enum.valueOf(clazz, s.toUpperCase())
            );
        }

        static <T extends Enum<T>> Command<T, String> enumOf(
                T[] values,
                Function<T, String> encode,
                Function<String, T> decode
        ) {
            return of(
                    StringArgumentType::word,
                    StringArgumentType::getString,
                    arg -> arg.suggests((context, builder) -> {
                        for (T value : values) {
                            builder.suggest(encode.apply(value));
                        }
                        return builder.buildFuture();
                    }),
                    decode
            );
        }

        static <T> Command<T, T> of(
                Supplier<ArgumentType<T>> argument,
                BiFunction<CommandContext<?>, String, T> reader
        ) {
            return of(argument, reader, Function.identity());
        }

        static <T, A> Command<T, A> of(
                Supplier<ArgumentType<A>> argument,
                BiFunction<CommandContext<?>, String, A> reader,
                Function<A, T> decode
        ) {
            return of(argument, reader, arg -> {
            }, decode);
        }

        static <T, A> Command<T, A> of(
                Supplier<ArgumentType<A>> argument,
                BiFunction<CommandContext<?>, String, A> reader,
                Consumer<RequiredArgumentBuilder<?, A>> adapter,
                Function<A, T> decode
        ) {
            return new Command<>() {
                @Override
                public <S extends CommandSource> RequiredArgumentBuilder<S, A> argument(
                        ArgumentFactory<S, A> factory
                ) {
                    RequiredArgumentBuilder<S, A> arg = factory.apply("value", argument.get());
                    adapter.accept(arg);
                    return arg;
                }

                @Override
                public ArgumentType<A> into(T value) {
                    return argument.get();
                }

                @Override
                public T from(CommandContext<? extends CommandSource> value) {
                    return decode.apply(reader.apply(value, "value"));
                }
            };
        }
    }
}
