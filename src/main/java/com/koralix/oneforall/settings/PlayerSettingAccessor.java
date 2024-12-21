package com.koralix.oneforall.settings;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public record PlayerSettingAccessor<T>(PlayerConfigValueWrapper<T> config) implements ConfigValueAccessor<ServerCommandSource, T> {
    @Override
    public T get(ServerCommandSource source) {
        return config.value(source.getPlayer().getUuid());
    }

    @Override
    public T getDefault(ServerCommandSource source) {
        return config.defaultValue(source.getPlayer().getUuid());
    }

    @Override
    public Text set(ServerCommandSource source, T value) {
        return config.value(source.getPlayer().getUuid(), value);
    }

    @Override
    public Text setDefault(ServerCommandSource source, T value) {
        return config.defaultValue(source.getPlayer().getUuid(), value);
    }
}
