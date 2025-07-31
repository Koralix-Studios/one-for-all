package com.koralix.oneforall.base.settings;

import com.koralix.oneforall.base.BaseInit;
import com.koralix.oneforall.config.ConfigCodec;
import com.koralix.oneforall.config.ConfigValue;
import com.koralix.oneforall.config.impl.ServerConfigValue;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.jetbrains.annotations.NotNull;

public class CommandSettings {
    public static final ServerConfigValue<Boolean, ByteBuf> COMMAND_SIGNAL = create("0.1.0", "command_signal");
    public static final ServerConfigValue<Boolean, ByteBuf> COMMAND_BATCH = create("0.1.0", "command_batch");
    public static final ServerConfigValue<Boolean, ByteBuf> COMMAND_ENDERCHEST = create("0.1.0", "command_enderchest");
    public static final ServerConfigValue<Boolean, ByteBuf> COMMAND_BASE2BASE = create("0.1.0", "command_base2base");
    public static final ServerConfigValue<Boolean, ByteBuf> COMMAND_STATSCORE = create("0.1.0", "command_statscore");
    public static final ServerConfigValue<Boolean, ByteBuf> COMMAND_WHERE = create("0.1.0", "command_where");

    private static ServerConfigValue<Boolean, ByteBuf> create(
            @NotNull String version,
            @NotNull String id
    ) {
        return ServerConfigValue.create(
                        version,
                        BaseInit.id(id),
                        false,
                        ConfigCodec.BOOLEAN
                )
                .onChange(CommandSettings::update)
                .build();
    }

    private static void update(
            @NotNull MinecraftServer server,
            @NotNull ConfigValue<MinecraftServer, Boolean, ByteBuf> configValue,
            boolean oldValue,
            boolean newValue
    ) {
        PlayerManager manager = server.getPlayerManager();
        manager.getPlayerList().forEach(manager::sendCommandTree);
    }
}
