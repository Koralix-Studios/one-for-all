package com.koralix.oneforall.settings;

import com.koralix.oneforall.settings.registry.ConfigValueEntry;
import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerConfigValue<T> extends AbstractMultiConfigValue<PlayerEntity, T, PlayerConfigValue<T>> {
    private final Map<UUID, T> defaults = new HashMap<>();
    private final Map<UUID, T> values = new HashMap<>();

    public PlayerConfigValue(T nominalValue, Codec<T> codec, ConfigValidator<T> validator) {
        super(nominalValue, codec, validator);
    }

    @Override
    public ConfigValueView<T, PlayerConfigValue<T>> view(PlayerEntity player) {
        return new PlayerConfigValueView(player.getUuid());
    }

    public final class PlayerConfigValueView implements ConfigValueView<T, PlayerConfigValue<T>> {
        private final UUID uuid;

        public PlayerConfigValueView(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public PlayerConfigValue<T> configValue() {
            return PlayerConfigValue.this;
        }

        @Override
        public T defaultValue() {
            return defaults.getOrDefault(uuid, nominalValue());
        }

        @Override
        public Optional<Text> defaultValue(T value) {
            if (!nominalValue().equals(value)) return validate(value, v -> defaults.put(uuid, v));
            defaults.remove(uuid);
            return Optional.empty();
        }

        @Override
        public T value() {
            return values.getOrDefault(uuid, defaultValue());
        }

        @Override
        public Optional<Text> value(T value) {
            if (!defaultValue().equals(value)) return validate(value, v -> values.put(uuid, v));
            values.remove(uuid);
            return Optional.empty();
        }
    }

    public static final class Builder<T> extends AbstractConfigValueBuilder<T, PlayerConfigValue<T>> {
        public Builder(T nominalValue, Codec<T> codec) {
            super(nominalValue, codec);
        }

        @Override
        public PlayerConfigValue<T> build() {
            return new PlayerConfigValue<>(nominalValue, codec, validator);
        }
    }
}
