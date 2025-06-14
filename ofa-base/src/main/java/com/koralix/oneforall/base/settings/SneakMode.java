package com.koralix.oneforall.base.settings;

import com.koralix.oneforall.config.MonoConfigValue;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum SneakMode implements StringIdentifiable {
    NEVER,
    SNEAK,
    NOT_SNEAK,
    ALWAYS;

    public static final @NotNull Codec<SneakMode> CODEC = StringIdentifiable.createCodec(SneakMode::values);
    private static final IntFunction<SneakMode> BY_ID = ValueLists.createIndexToValueFunction(
            SneakMode::ordinal, values(), ValueLists.OutOfBoundsHandling.WRAP
    );
    public static final PacketCodec<ByteBuf, SneakMode> PACKET_CODEC = PacketCodecs.indexed(BY_ID, SneakMode::ordinal);

    public boolean isActive(@NotNull PlayerEntity player, @NotNull MonoConfigValue<Boolean, ?, ?> config) {
        return config.value() && switch (this) {
            case NEVER -> false;
            case SNEAK -> player.isSneaking();
            case NOT_SNEAK -> !player.isSneaking();
            case ALWAYS -> true;
        };
    }

    @Override
    public @NotNull String asString() {
        return this.name().toLowerCase();
    }
}
