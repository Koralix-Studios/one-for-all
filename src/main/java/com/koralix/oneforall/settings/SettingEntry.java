package com.koralix.oneforall.settings;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class SettingEntry<T> {
    private final SettingsRegistry registry;
    private final ConfigValue<T> setting;
    private final Identifier settingId;

    public SettingEntry(
            @NotNull SettingsRegistry registry,
            @NotNull Identifier settingId,
            @NotNull ConfigValue<T> setting
    ) {
        this.registry = registry;
        this.setting = setting;
        this.settingId = Identifier.of(
                SettingsManager.identifier(registry).toTranslationKey(),
                settingId.getNamespace().equals(SettingsManager.identifier(registry).getNamespace())
                        ? settingId.getPath()
                        : settingId.toTranslationKey()
        );
    }

    public SettingsRegistry registry() {
        return registry;
    }

    public ConfigValue<T> setting() {
        return setting;
    }

    public Identifier registryId() {
        return SettingsManager.identifier(registry);
    }

    public Identifier id() {
        return settingId;
    }

    public String translation() {
        return settingId.toTranslationKey("settings");
    }

    @Override
    public String toString() {
        return settingId.toString();
    }
}
