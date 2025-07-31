package com.koralix.oneforall.base.settings;

import com.koralix.oneforall.base.BaseInit;
import com.koralix.oneforall.config.ConfigCodec;
import com.koralix.oneforall.config.impl.ServerConfigValue;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

public class ServerSettings {
    public static final ServerConfigValue<Boolean, ByteBuf> CAREFUL_BREAK = create("0.1.0", "careful_break");

    public static final ServerConfigValue<Boolean, ByteBuf> XP_BAR_MENDING = create("0.1.0", "xp_bar_mending");

    public static final ServerConfigValue<Boolean, ByteBuf> CREATIVE_KILL = create("0.1.0", "creative_kill");

    public static final ServerConfigValue<Boolean, ByteBuf> SPLIT_SCATTERED_ITEMS = create("0.1.0", "split_scattered_items");

    public static final ServerConfigValue<ShulkerStackMode, ByteBuf> STACK_SHULKER_BOXES = create(
            "0.1.0", "stack_shulker_boxes",
            ShulkerStackMode.NEVER,
            ConfigCodec.of(ShulkerStackMode.CODEC, ShulkerStackMode.PACKET_CODEC, ShulkerStackMode.COMMAND_ADAPTER)
    );

    public static final ServerConfigValue<Boolean, ByteBuf> angryZombifiedPiglinsDropXP = create("0.1.0", "angry_zombified_piglins_drop_xp");

    public static final ServerConfigValue<Boolean, ByteBuf> skipOpLevel2 = create("0.1.0", "skip_op_level_2");

    private static @NotNull ServerConfigValue<Boolean, ByteBuf> create(@NotNull String version, @NotNull String id) {
        return create(version, id, false, ConfigCodec.BOOLEAN);
    }

    private static <T, B> @NotNull ServerConfigValue<T, B> create(
            @NotNull String version,
            @NotNull String id,
            @NotNull T defaultValue,
            @NotNull ConfigCodec<T, B> codec
    ) {
        return ServerConfigValue.create(version, BaseInit.id(id), defaultValue, codec)
                .write(actor -> !(actor instanceof ServerCommandSource source) || source.hasPermissionLevel(4))
                .build();
    }
}
