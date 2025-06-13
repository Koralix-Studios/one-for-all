package com.koralix.oneforall.config;

import com.koralix.oneforall.config.adapter.CommandAdapter;
import com.koralix.oneforall.config.registry.ConfigEntry;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class SingletonConfigValue<T, B extends ByteBuf> extends AbstractConfigValue<T, B, MonoConfigValue.MonoConfigObserver<T>, SingletonConfigValue.SaveData<T>> implements MonoConfigValue<T, B, SingletonConfigValue.SaveData<T>> {
    private T value = null;

    public SingletonConfigValue(
            @NotNull Function<ConfigValue<T, B, SaveData<T>>, ConfigEntry<T>> registerFn,
            @NotNull T nominal,
            @NotNull Codec<T> codec,
            @NotNull PacketCodec<B, T> packetCodec,
            @NotNull ConfigTest<T> test,
            @NotNull CommandAdapter<T> commandAdapter
    ) {
        super(registerFn, nominal, codec, packetCodec, SaveData.codec(codec), test, commandAdapter);
    }

    @Override
    public @NotNull T value() {
        return this.value == null ? this.nominal : this.value;
    }

    @Override
    public @NotNull ConfigResult<T> value(@Nullable T value) {
        if (this.value != null && this.value.equals(value)) return ConfigResult.unchanged(this.value);
        ConfigResult<T> result;
        if (value == null || this.nominal.equals(value)) {
            result = this.value == null ? ConfigResult.unchanged(this.nominal) : ConfigResult.ok(this.value, this.nominal);
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
        if (result.isChange()) this.notifyDataObservers();
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

    @Override
    public void loadData(@NotNull SaveData<T> data) {
        this.value = data.value();
    }

    @Override
    public @NotNull Optional<SaveData<T>> saveData() {
        return this.value == null || this.nominal.equals(this.value)
                ? Optional.empty()
                : Optional.of(new SaveData<>(this.value));
    }

    public record SaveData<T>(@NotNull T value) {
        @Contract("_ -> new")
        private static <T> @NotNull Codec<SaveData<T>> codec(@NotNull Codec<T> codec) {
            return codec.xmap(SaveData::new, SaveData::value);
        }
    }
}
