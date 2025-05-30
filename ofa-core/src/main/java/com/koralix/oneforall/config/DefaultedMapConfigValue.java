package com.koralix.oneforall.config;

import com.koralix.oneforall.config.registry.ConfigEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DefaultedMapConfigValue<K, T, B extends ByteBuf> extends AbstractConfigValue<T, B, MultiConfigValue.MultiConfigObserver<K, T>, DefaultedMapConfigValue.SaveData<K, T>> implements MultiConfigValue<K, T, B, DefaultedMapConfigValue.SaveData<K, T>> {
    private final @NotNull Codec<K> keyCodec;
    private final @NotNull Map<K, T> valueMap = new HashMap<>();
    private T defaultValue = null;

    public DefaultedMapConfigValue(
            @NotNull Function<ConfigValue<T, B, SaveData<K, T>>, ConfigEntry<T>> registerFn,
            @NotNull T nominal,
            @NotNull Codec<K> keyCodec,
            @NotNull Codec<T> codec,
            @NotNull PacketCodec<B, T> packetCodec,
            @NotNull ConfigTest<T> test
    ) {
        super(registerFn, nominal, codec, packetCodec, SaveData.codec(keyCodec, codec), test);
        this.keyCodec = keyCodec;
    }

    @Override
    public @NotNull T value(K key) {
        return this.valueMap.getOrDefault(key, this.defaultValue());
    }

    @Override
    public @NotNull ConfigResult<T> value(@NotNull K key, @Nullable T value) {
        ConfigResult<T> result;
        if (value == null || this.nominal.equals(value)) {
            T v = this.valueMap.remove(key);
            if (v != null) this.observers.forEach(observer -> observer.onChange(this, key, v, this.nominal));
            result = v == null ? ConfigResult.ok(this.nominal) : ConfigResult.ok(v, this.nominal);
        } else {
            result = this.test.canChange(this.value(key), value);
            if (result.isOk()) {
                T v = this.valueMap.put(key, value);
                this.observers.forEach(observer -> observer.onChange(this, key, v, value));
            }
        }
        if (result.isOk()) this.notifyDataObservers();
        return result;
    }

    @Override
    public @NotNull ConfigResult<T> value(@NotNull ConfigActor actor, @NotNull K key) {
        return this.test.canObserve(actor).replace(this.value(key));
    }

    @Override
    public @NotNull ConfigResult<T> value(@NotNull ConfigActor actor, @NotNull K key, @Nullable T value) {
        return this.test.canChange(actor).and(this.value(key, value));
    }

    @Override
    public @NotNull T defaultValue() {
        return this.defaultValue == null ? this.nominal : this.defaultValue;
    }

    @Override
    public @NotNull ConfigResult<T> defaultValue(@NotNull T defaultValue) {
        ConfigResult<T> result = this.test.canChange(this.defaultValue(), defaultValue);
        if (result.isOk()) {
            this.defaultValue = defaultValue;
            this.notifyDataObservers();
        }
        return result;
    }

    @Override
    public @NotNull ConfigResult<T> defaultValue(@NotNull ConfigActor actor, @NotNull T defaultValue) {
        return this.test.canChange(actor).and(this.defaultValue(defaultValue));
    }

    @Override
    public @NotNull Codec<K> keyCodec() {
        return this.keyCodec;
    }

    @Override
    public @NotNull Map<K, T> valueMap() {
        return Collections.unmodifiableMap(this.valueMap);
    }

    @Override
    public void loadData(@NotNull SaveData<K, T> data) {
        this.defaultValue = data.defaultValue();
        this.valueMap.clear();
        this.valueMap.putAll(data.value());
    }

    @Override
    public @NotNull Optional<SaveData<K, T>> saveData() {
        boolean isDefaultNominal = this.defaultValue == null || this.nominal.equals(this.defaultValue);
        if (this.valueMap.isEmpty() && isDefaultNominal) return Optional.empty();
        return Optional.of(new SaveData<>(
                isDefaultNominal ? null : this.defaultValue,
                Collections.unmodifiableMap(this.valueMap)
        ));
    }

    public record SaveData<K, T>(@Nullable T defaultValue, @NotNull Map<K, T> value) {
        @Contract("_, _ -> new")
        private static <K, T> @NotNull Codec<SaveData<K, T>> codec(@NotNull Codec<K> keyCodec, @NotNull Codec<T> valueCodec) {
            return RecordCodecBuilder.create(instance -> instance.group(
                    valueCodec.optionalFieldOf("defaultValue").xmap(
                            opt -> opt.orElse(null),
                            Optional::ofNullable
                    ).forGetter(data -> data.defaultValue),
                    Codec.unboundedMap(keyCodec, valueCodec).fieldOf("value").forGetter(data -> data.value)
            ).apply(instance, SaveData::new));
        }
    }
}
