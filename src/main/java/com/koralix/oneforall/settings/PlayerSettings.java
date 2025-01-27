package com.koralix.oneforall.settings;

import com.koralix.oneforall.utils.IntoText;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;

public final class PlayerSettings {
    private PlayerSettings() {
        throw new UnsupportedOperationException("Cannot instantiate settings class");
    }

    public enum CAREFUL_BREAK_MODE implements IntoText {
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
        public static final ConfigValueAdapter.Command<CAREFUL_BREAK_MODE, String> COMMAND = ConfigValueAdapter.Command.enumOf(CAREFUL_BREAK_MODE.class);

        public boolean isActive(PlayerEntity player) {
            return ServerSettings.CAREFUL_BREAK.value() && switch (this) {
                case NEVER -> false;
                case SNEAK -> player.isSneaking();
                case NOT_SNEAK -> !player.isSneaking();
                case ALWAYS -> true;
            };
        }

        @Override
        public Text toText() {
            return Text.translatable("enum.oneforall.careful_break_mode." + name().toLowerCase());
        }
    }

    public static final PlayerConfigValue<CAREFUL_BREAK_MODE> CAREFUL_BREAK = ConfigValue.player(
                    CAREFUL_BREAK_MODE.NEVER,
                    CAREFUL_BREAK_MODE.CODEC,
                    CAREFUL_BREAK_MODE.COMMAND
            )
            .test(Objects::nonNull)
            .build();
}
