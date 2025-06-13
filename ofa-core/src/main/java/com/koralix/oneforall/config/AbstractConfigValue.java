package com.koralix.oneforall.config;

import com.koralix.oneforall.config.adapter.CommandAdapter;
import com.koralix.oneforall.config.registry.ConfigEntry;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractConfigValue<T, B extends ByteBuf, O, S> implements ConfigValue<T, B, S> {
    protected final @NotNull ConfigEntry<T> entry;
    protected final @NotNull T nominal;
    protected final @NotNull Codec<T> codec;
    protected final @NotNull PacketCodec<B, T> packetCodec;
    protected final @NotNull Codec<S> saveCodec;
    protected final @NotNull ConfigTest<T> test;
    protected final @NotNull List<O> observers = new ArrayList<>();
    protected final @NotNull List<Function<S, Boolean>> dataObservers = new ArrayList<>();
    private final @NotNull CommandAdapter<T> commandAdapter;

    public AbstractConfigValue(
            @NotNull Function<ConfigValue<T, B, S>, ConfigEntry<T>> registerFn,
            @NotNull T nominal,
            @NotNull Codec<T> codec,
            @NotNull PacketCodec<B, T> packetCodec,
            @NotNull Codec<S> saveCodec,
            @NotNull ConfigTest<T> test,
            @NotNull CommandAdapter<T> commandAdapter
    ) {
        this.entry = registerFn.apply(this);
        this.nominal = nominal;
        this.codec = codec;
        this.packetCodec = packetCodec;
        this.saveCodec = saveCodec;
        this.test = test;
        this.commandAdapter = commandAdapter;
    }

    @Override
    public @NotNull ConfigEntry<T> entry() {
        return this.entry;
    }

    @Override
    public @NotNull T nominal() {
        return this.nominal;
    }

    @Override
    public @NotNull Codec<T> codec() {
        return this.codec;
    }

    @Override
    public @NotNull PacketCodec<B, T> packetCodec() {
        return this.packetCodec;
    }

    @Override
    public @NotNull Codec<S> saveCodec() {
        return this.saveCodec;
    }

    @Override
    public void onChange(@NotNull Function<S, Boolean> observer) {
        this.dataObservers.add(observer);
    }

    @Override
    public void onChange(@NotNull Consumer<S> observer) {
        this.dataObservers.add(s -> {
            observer.accept(s);
            return true;
        });
    }

    @Override
    public @NotNull CommandAdapter<T> commandAdapter() {
        return this.commandAdapter;
    }

    public void onChange(@NotNull O observer) {
        this.observers.add(observer);
    }

    protected void notifyDataObservers() {
        this.dataObservers.removeIf(observer -> !observer.apply(saveData().orElse(null)));
    }
}
