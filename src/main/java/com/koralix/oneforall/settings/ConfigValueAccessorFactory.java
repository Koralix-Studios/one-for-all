package com.koralix.oneforall.settings;

import net.minecraft.command.CommandSource;

@FunctionalInterface
public interface ConfigValueAccessorFactory<S extends CommandSource> {
    <T> ConfigValueAccessor<S, T> create(ConfigValue<T> config);

    default <T> ConfigValueAccessor<S, T> create(SettingEntry<T> entry) {
        return create(entry.setting());
    }
}
