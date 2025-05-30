package com.koralix.oneforall.base;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.PlayerConfigValue;
import com.koralix.oneforall.config.registry.ConfigRegistrar;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.koralix.oneforall.config.registry.VersionedIdentifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayerSettings {
    private static final ConfigRegistrar REGISTRAR = ConfigRegistry.builder("0.1.0", OneForAll.id("player_settings")).prepare();

    public enum CAREFUL_BREAK_MODE {
        NEVER,
        SNEAK,
        NOT_SNEAK,
        ALWAYS;

        public static final Codec<CAREFUL_BREAK_MODE> CODEC = Codec.BYTE.comapFlatMap(
                i -> {
                    CAREFUL_BREAK_MODE[] values = CAREFUL_BREAK_MODE.values();
                    return i >= 0 && i < values.length
                            ? DataResult.success(values[i])
                            : DataResult.error(() -> "Invalid careful break mode: " + i);
                },
                carefulBreakMode -> (byte) carefulBreakMode.ordinal()
        );

        public static final PacketCodec<ByteBuf, CAREFUL_BREAK_MODE> PACKET_CODEC = PacketCodecs.BYTE.xmap(
                i -> {
                    CAREFUL_BREAK_MODE[] values = CAREFUL_BREAK_MODE.values();
                    if (i >= 0 && i < values.length) return values[i];

                    throw new IllegalArgumentException("Invalid careful break mode: " + i);
                },
                carefulBreakMode -> (byte) carefulBreakMode.ordinal()
        );

        public boolean isActive(PlayerEntity player) {
            return ServerSettings.CAREFUL_BREAK.value() && switch (this) {
                case NEVER -> false;
                case SNEAK -> player.isSneaking();
                case NOT_SNEAK -> !player.isSneaking();
                case ALWAYS -> true;
            };
        }
    }

    public static final PlayerConfigValue<CAREFUL_BREAK_MODE, ByteBuf> CAREFUL_BREAK = REGISTRAR
            .player(VersionedIdentifier.of("0.1.0", OneForAll.id("careful_break")), CAREFUL_BREAK_MODE.ALWAYS, CAREFUL_BREAK_MODE.CODEC, CAREFUL_BREAK_MODE.PACKET_CODEC, a -> false, a -> false)
            .test(Objects::nonNull)
            .build();

    public static @NotNull ConfigRegistry register() {
        return REGISTRAR.complete();
    }
}
