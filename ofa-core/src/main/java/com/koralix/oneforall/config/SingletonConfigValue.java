package com.koralix.oneforall.config;

import com.koralix.oneforall.config.registry.ConfigEntry;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class SingletonConfigValue<T, B extends ByteBuf> extends AbstractConfigValue<T, B, MonoConfigValue.MonoConfigObserver<T>> implements MonoConfigValue<T, B> {
    private T value = null;

    public SingletonConfigValue(
            @NotNull Function<ConfigValue<T, B>, ConfigEntry<T>> registerFn,
            @NotNull T nominal,
            @NotNull Codec<T> codec,
            @NotNull PacketCodec<B, T> packetCodec,
            @NotNull ConfigTest<T> test
    ) {
        super(registerFn, nominal, codec, packetCodec, test);
    }

    @Override
    public @NotNull T value() {
        return this.value == null ? this.nominal : this.value;
    }

    @Override
    public @NotNull ConfigResult<T> value(@Nullable T value) {
        ConfigResult<T> result;
        if (value == null || this.nominal.equals(value)) {
            result = this.value == null ? ConfigResult.ok(this.nominal) : ConfigResult.ok(this.value, this.nominal);
            T oldValue = this.value;
            this.value = null;
            if (oldValue != null) this.observers.forEach(observer -> observer.onChange(this, oldValue, this.nominal));
        } else {
            result = this.test.canChange(this.value(), value);
            if (result.isOk()) {
                T oldValue = this.value;
                this.value = value;
                this.observers.forEach(observer -> observer.onChange(this, oldValue, value));
            }
        }
        return result;
    }

    @Override
    public @NotNull ConfigResult<T> value(@NotNull ConfigActor actor) {
        return this.test.canObserve(actor).replace(this.value());
    }

    @Override
    public @NotNull ConfigResult<T> value(@NotNull ConfigActor actor, @Nullable T value) {
        return this.test.canChange(actor).and(this.value(value));
    }
}
