package com.koralix.oneforall.settings;

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

        public boolean isActive(PlayerEntity player) {
            return ServerSettings.CAREFUL_BREAK.value() && switch (this) {
                case NEVER -> false;
                case SNEAK -> player.isSneaking();
                case NOT_SNEAK -> !player.isSneaking();
                case ALWAYS -> true;
            };
        }
    }

    public static final PlayerConfigValueWrapper<CAREFUL_BREAK_MODE> CAREFUL_BREAK = ConfigValue.of(CAREFUL_BREAK_MODE.NEVER)
            .test(Objects::nonNull)
            .player();
}
