package com.koralix.oneforall.base.settings;

import com.koralix.oneforall.base.BaseInit;
import com.koralix.oneforall.config.ConfigCodec;
import com.koralix.oneforall.config.impl.ServerConfigValue;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.command.ServerCommandSource;

public class ServerSettings {
    public static final ServerConfigValue<Boolean, ByteBuf> CAREFUL_BREAK = ServerConfigValue.create(
                    "0.1.0", BaseInit.id("careful_break"),
                    true,
                    ConfigCodec.BOOLEAN
            )
            .write(actor -> !(actor instanceof ServerCommandSource source) || source.hasPermissionLevel(4))
            .build();

    public static final ServerConfigValue<Boolean, ByteBuf> XP_BAR_MENDING = ServerConfigValue.create(
                    "0.1.0", BaseInit.id("xp_bar_mending"),
                    true,
                    ConfigCodec.BOOLEAN
            )
            .write(actor -> !(actor instanceof ServerCommandSource source) || source.hasPermissionLevel(4))
            .build();

    public static final ServerConfigValue<Boolean, ByteBuf> CREATIVE_KILL = ServerConfigValue.create(
                    "0.1.0", BaseInit.id("creative_kill"),
                    true,
                    ConfigCodec.BOOLEAN
            )
            .write(actor -> !(actor instanceof ServerCommandSource source) || source.hasPermissionLevel(4))
            .build();

    public static final ServerConfigValue<Boolean, ByteBuf> SPLIT_SCATTERED_ITEMS = ServerConfigValue.create(
                    "0.1.0", BaseInit.id("split_scattered_items"),
                    true,
                    ConfigCodec.BOOLEAN
            )
            .write(actor -> !(actor instanceof ServerCommandSource source) || source.hasPermissionLevel(4))
            .build();

    public static final ServerConfigValue<ShulkerStackMode, ByteBuf> STACK_SHULKER_BOXES = ServerConfigValue.create(
                    "0.1.0", BaseInit.id("stack_shulker_boxes"),
                    ShulkerStackMode.NEVER,
                    ConfigCodec.of(ShulkerStackMode.CODEC, ShulkerStackMode.PACKET_CODEC, ShulkerStackMode.COMMAND_ADAPTER)
            )
            .write(actor -> !(actor instanceof ServerCommandSource source) || source.hasPermissionLevel(4))
            .build();
}
