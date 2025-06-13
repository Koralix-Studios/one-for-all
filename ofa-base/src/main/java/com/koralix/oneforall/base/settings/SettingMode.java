package com.koralix.oneforall.base.settings;

import com.koralix.oneforall.client.settings.ProtocolUsageCondition;
import com.koralix.oneforall.config.MonoConfigValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum SettingMode implements StringIdentifiable {
    NEVER,
    SNEAK,
    NOT_SNEAK,
    ALWAYS;

    public static final @NotNull Codec<SettingMode> CODEC = StringIdentifiable.createCodec(SettingMode::values);
    private static final IntFunction<SettingMode> BY_ID = ValueLists.createIndexToValueFunction(
            SettingMode::ordinal, values(), ValueLists.OutOfBoundsHandling.WRAP
    );
    public static final PacketCodec<ByteBuf, SettingMode> PACKET_CODEC = PacketCodecs.indexed(BY_ID, SettingMode::ordinal);

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
