package com.koralix.oneforall.settings;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public record ServerSettingAccessor<T>(ConfigValueWrapper<T> config) implements ConfigValueAccessor<ServerCommandSource, T> {
    @Override
    public T get(ServerCommandSource source) {
        return config.value();
    }

    @Override
    public T getDefault(ServerCommandSource source) {
        return config.defaultValue();
    }

    @Override
    public Text set(ServerCommandSource source, T value) {
        return config.value(value);
    }

    @Override
    public Text setDefault(ServerCommandSource source, T value) {
        return config.defaultValue(value);
    }
}
