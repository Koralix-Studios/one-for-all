package com.koralix.oneforall.settings;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public record ClientSettingAccessor<T>(ConfigValueWrapper<T> config) implements ConfigValueAccessor<FabricClientCommandSource, T> {
    @Override
    public T get(FabricClientCommandSource source) {
        return config.value();
    }

    @Override
    public T getDefault(FabricClientCommandSource source) {
        return config.defaultValue();
    }

    @Override
    public Text set(FabricClientCommandSource source, T value) {
        return config.value(value);
    }

    @Override
    public Text setDefault(FabricClientCommandSource source, T value) {
        return config.defaultValue(value);
    }
}
