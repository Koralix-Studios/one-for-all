package com.koralix.oneforall.settings;

@SettingsRegistry(id = "player_settings", env = SettingsRegistry.Env.SERVER)
public final class PlayerSettings {
    private PlayerSettings() {
        throw new UnsupportedOperationException("Cannot instantiate settings class");
    }
}
