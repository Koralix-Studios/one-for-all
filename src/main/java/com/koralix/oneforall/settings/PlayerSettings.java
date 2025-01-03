package com.koralix.oneforall.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;

@SettingsRegistry(id = "player_settings", env = SettingsRegistry.Env.SERVER)
public final class PlayerSettings {
    private PlayerSettings() {
        throw new UnsupportedOperationException("Cannot instantiate settings class");
    }

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

        public boolean isActive(PlayerEntity player) {
            return ServerSettings.CAREFUL_BREAK.value() && switch (this) {
                case NEVER -> false;
                case SNEAK -> player.isSneaking();
                case NOT_SNEAK -> !player.isSneaking();
                case ALWAYS -> true;
            };
        }
    }

    public static final PlayerConfigValueWrapper<CAREFUL_BREAK_MODE> CAREFUL_BREAK = ConfigValue.of(CAREFUL_BREAK_MODE.NEVER, CAREFUL_BREAK_MODE.CODEC)
            .test(Objects::nonNull)
            .player();
}
