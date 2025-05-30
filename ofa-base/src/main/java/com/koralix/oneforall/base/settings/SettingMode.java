package com.koralix.oneforall.base.settings;

import com.koralix.oneforall.config.MonoConfigValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public enum SettingMode {
    NEVER,
    SNEAK,
    NOT_SNEAK,
    ALWAYS;

    public static final Codec<SettingMode> CODEC = Codec.BYTE.comapFlatMap(
            i -> {
                SettingMode[] values = SettingMode.values();
                return i >= 0 && i < values.length
                        ? DataResult.success(values[i])
                        : DataResult.error(() -> "Invalid setting mode: " + i);
            },
            mode -> (byte) mode.ordinal()
    );

    public static final PacketCodec<ByteBuf, SettingMode> PACKET_CODEC = PacketCodecs.BYTE.xmap(
            i -> {
                SettingMode[] values = SettingMode.values();
                if (i >= 0 && i < values.length) return values[i];

                throw new IllegalArgumentException("Invalid setting mode: " + i);
            },
            mode -> (byte) mode.ordinal()
    );

    public boolean isActive(PlayerEntity player, MonoConfigValue<Boolean, ?> config) {
        return config.value() && switch (this) {
            case NEVER -> false;
            case SNEAK -> player.isSneaking();
            case NOT_SNEAK -> !player.isSneaking();
            case ALWAYS -> true;
        };
    }
}
