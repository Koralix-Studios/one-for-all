package com.koralix.oneforall.settings;

import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public interface ConfigValueAccessor<S extends CommandSource, T> {
    T get(S source);

    T getDefault(S source);

    Text set(S source, T value);

    Text setDefault(S source, T value);

    static <T> ConfigValueAccessor<ServerCommandSource, T> server(ConfigValue<T> config) {
        if (config instanceof ConfigValueWrapper<T> serverConfig) {
            return new ServerSettingAccessor<>(serverConfig);
        } else if (config instanceof PlayerConfigValueWrapper<T> playerConfig) {
            return new PlayerSettingAccessor<>(playerConfig);
        } else {
            throw new IllegalArgumentException("Config value is not a server or player config value");
        }
    }
}
