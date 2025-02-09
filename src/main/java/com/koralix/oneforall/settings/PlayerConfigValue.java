package com.koralix.oneforall.settings;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.commands.CommonCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.SemanticVersion;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class PlayerConfigValue<T> extends AbstractMultiConfigValue<PlayerEntity, T> {
    private final Codec<Data<T>> codec;
    private final Map<UUID, T> defaults = new HashMap<>();
    private final Map<UUID, T> values = new HashMap<>();

    public PlayerConfigValue(
            T nominalValue,
            Codec<T> codec,
            ConfigValueAdapter.Command<T, ?> command,
            ConfigValidator<T> validator,
            Predicate<CommandSource> permission,
            SemanticVersion since
    ) {
        super(nominalValue, codec, command, validator, permission, since);
        this.codec = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Uuids.CODEC, codec).fieldOf("defaultValue").forGetter(Data::defaultValue),
                Codec.unboundedMap(Uuids.CODEC, codec).fieldOf("value").forGetter(Data::value)
        ).apply(instance, Data::new));
    }

    @Override
    public ConfigValueView<T> view(PlayerEntity player) {
        return new PlayerConfigValueView(player);
    }

    @Override
    public ConfigValueView<T> view(CommandContext<? extends CommandSource> context) {
        if (!(context.getSource() instanceof CommonCommandSource source))
            throw new IllegalStateException("CommandSource is not a CommonCommandSource");

        return source
                .player()
                .map(this::view)
                .orElseThrow(() -> new IllegalStateException("CommandSource does not have a player"));
    }

    @Override
    public void read(NbtCompound compound) {
        String key = this.entry().key().toString();
        DataResult<Data<T>> result = this.codec.parse(NbtOps.INSTANCE, compound.getCompound(key));
        result.resultOrPartial(s -> {
            OneForAll.LOGGER.error("[{}] Failed to read config value: {}", key, s);
        }).ifPresent(data -> {
            this.defaults.clear();
            this.defaults.putAll(data.defaultValue());
            this.values.clear();
            this.values.putAll(data.value());
        });
    }

    @Override
    public void write(NbtCompound compound) {
        String key = this.entry().key().toString();
        this.codec.encodeStart(NbtOps.INSTANCE, new Data<>(this.defaults, this.values)).resultOrPartial(s -> {
            OneForAll.LOGGER.error("[{}] Failed to write config value: {}", key, s);
        }).ifPresent(data -> {
            compound.put(key, data);
        });
    }

    public final class PlayerConfigValueView implements ConfigValueView<T> {
        private final PlayerEntity player;
        private final UUID uuid;

        public PlayerConfigValueView(PlayerEntity player) {
            this.player = player;
            this.uuid = player.getUuid();
        }

        @Override
        public T defaultValue() {
            return defaults.getOrDefault(uuid, nominalValue());
        }

        @Override
        public Optional<Text> defaultValue(T value) {
            if (!nominalValue().equals(value)) return validate(value, v -> {
                defaults.put(uuid, v);
                SettingsManager.save(entry().registry().environment());
            });
            defaults.remove(uuid);
            SettingsManager.save(entry().registry().environment());
            return Optional.empty();
        }

        @Override
        public T value() {
            return values.getOrDefault(uuid, defaultValue());
        }

        @Override
        public Optional<Text> value(T value) {
            if (!defaultValue().equals(value)) return validate(value, v -> {
                T oldValue = values.put(uuid, v);
                PlayerConfigValue.this.onChange().invoker().onChange(new ConfigValueChange.MultiChange<>(PlayerConfigValue.this, player, oldValue, v));
                SettingsManager.save(entry().registry().environment());
            });
            values.remove(uuid);
            SettingsManager.save(entry().registry().environment());
            return Optional.empty();
        }
    }

    public static final class Builder<T> extends AbstractConfigValueBuilder<T, PlayerConfigValue<T>> {
        public Builder(T nominalValue, Codec<T> codec, ConfigValueAdapter.Command<T, ?> command, SemanticVersion since) {
            super(nominalValue, codec, command, since);
        }

        @Override
        public PlayerConfigValue<T> build() {
            return new PlayerConfigValue<>(nominalValue, codec, command, validator, permission, since);
        }
    }

    private record Data<T>(Map<UUID, T> defaultValue, Map<UUID, T> value) {}
}
