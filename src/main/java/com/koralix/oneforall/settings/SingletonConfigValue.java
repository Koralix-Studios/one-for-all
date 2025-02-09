package com.koralix.oneforall.settings;

import com.koralix.oneforall.OneForAll;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.SemanticVersion;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class SingletonConfigValue<T> extends AbstractConfigValue<T> implements MonoConfigValue<T> {
    private final Codec<Data<T>> codec;
    private T defaultValue;
    private T value;

    public SingletonConfigValue(
            T nominalValue,
            Codec<T> codec,
            ConfigValueAdapter.Command<T, ?> command,
            ConfigValidator<T> validator,
            Predicate<CommandSource> permission,
            SemanticVersion since
    ) {
        super(nominalValue, codec, command, validator, permission, since);
        this.defaultValue = nominalValue;
        this.value = nominalValue;
        this.codec = RecordCodecBuilder.create(instance -> instance.group(
                codec.optionalFieldOf("defaultValue").forGetter(Data::defaultValue),
                codec.optionalFieldOf("value").forGetter(Data::value)
        ).apply(instance, Data::new));
    }

    @Override
    public T defaultValue() {
        return defaultValue;
    }

    @Override
    public Optional<Text> defaultValue(T value) {
        return validate(value, v -> {
            this.defaultValue = v;
            SettingsManager.save(entry().registry().environment());
        });
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public Optional<Text> value(T value) {
        return validate(value, v -> {
            T oldValue = this.value;
            this.value = v;
            this.onChange().invoker().onChange(new ConfigValueChange.MonoChange<>(this, oldValue, v));
            SettingsManager.save(entry().registry().environment());
        });
    }

    @Override
    public ConfigValueView<T> view(CommandContext<? extends CommandSource> context) {
        return this;
    }

    @Override
    public void read(NbtCompound compound) {
        String key = this.entry().key().toString();
        compound = compound.getCompound(key);

        if (compound.isEmpty()) return;

        DataResult<Data<T>> result = this.codec.parse(NbtOps.INSTANCE, compound);
        result.resultOrPartial(s -> {
            OneForAll.LOGGER.error("[{}] Failed to read config value: {}", key, s);
        }).ifPresent(data -> {
            this.defaultValue = data.defaultValue().orElse(this.nominalValue());
            this.value = data.value().orElse(this.defaultValue);
        });
    }

    @Override
    public void write(NbtCompound compound) {
        String key = this.entry().key().toString();
        Data<T> data = Data.of(this.nominalValue(), this.defaultValue, this.value);
        if (data.nominal()) return;
        this.codec.encodeStart(NbtOps.INSTANCE, data).resultOrPartial(s -> {
            OneForAll.LOGGER.error("[{}] Failed to write config value: {}", key, s);
        }).ifPresent(nbt -> {
            compound.put(key, nbt);
        });
    }

    public static final class Builder<T> extends AbstractConfigValueBuilder<T, SingletonConfigValue<T>> {
        public Builder(T nominalValue, Codec<T> codec, ConfigValueAdapter.Command<T, ?> command, SemanticVersion since) {
            super(nominalValue, codec, command, since);
        }

        @Override
        public SingletonConfigValue<T> build() {
            return new SingletonConfigValue<>(nominalValue, codec, command, validator, permission, since);
        }
    }

    private record Data<T>(Optional<T> defaultValue, Optional<T> value) {
        public static <T> Data<T> of(T nominalValue, T defaultValue, T value) {
            Optional<T> defaultOptional = Optional.ofNullable(Objects.equals(nominalValue, defaultValue) ? null : defaultValue);
            Optional<T> valueOptional = Optional.ofNullable(Objects.equals(defaultValue, value) ? null : value);
            return new Data<>(defaultOptional, valueOptional);
        }

        public boolean nominal() {
            return !(defaultValue.isPresent() || value.isPresent());
        }
    }
}
