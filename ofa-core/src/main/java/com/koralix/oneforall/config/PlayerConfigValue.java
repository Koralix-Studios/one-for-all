package com.koralix.oneforall.config;

import com.koralix.oneforall.config.registry.ConfigEntry;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public class PlayerConfigValue<T, B extends ByteBuf> extends DefaultedMapConfigValue<UUID, T, B> {
    private final Predicate<ConfigActor> canObserveOthers;
    private final Predicate<ConfigActor> canChangeOthers;

    public PlayerConfigValue(
            @NotNull Function<MultiConfigValue<UUID, T, B>, ConfigEntry<T>> registerFn,
            @NotNull T nominal,
            @NotNull Codec<T> codec,
            @NotNull PacketCodec<B, T> packetCodec,
            @NotNull ConfigTest<T> test,
            @NotNull Predicate<ConfigActor> canObserveOthers,
            @NotNull Predicate<ConfigActor> canChangeOthers
    ) {
        super(registerFn, nominal, Uuids.INT_STREAM_CODEC, codec, packetCodec, test);
        this.canObserveOthers = canObserveOthers;
        this.canChangeOthers = canChangeOthers;
    }

    @Override
    public @NotNull ConfigResult<T> value(@NotNull ConfigActor actor, @NotNull UUID key) {
        Optional<UUID> uuidOpt = actor.uuid();
        if (uuidOpt.isEmpty()) return super.value(actor, key);
        UUID uuid = uuidOpt.get();
        if (uuid.equals(key)) return super.value(actor, uuid);
        if (this.canObserveOthers.test(actor)) return super.value(actor, key);
        else return new ConfigResult.ForbidObserve<>(actor);
    }

    @Override
    public @NotNull ConfigResult<T> value(@NotNull ConfigActor actor, @NotNull UUID key, @Nullable T value) {
        Optional<UUID> uuidOpt = actor.uuid();
        if (uuidOpt.isEmpty()) return super.value(actor, key, value);
        UUID uuid = uuidOpt.get();
        if (uuid.equals(key)) return super.value(actor, uuid, value);
        if (this.canChangeOthers.test(actor)) {
            return super.value(actor, key, value);
        } else {
            return new ConfigResult.ForbidChange<>(actor);
        }
    }
}
