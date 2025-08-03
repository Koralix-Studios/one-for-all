package com.koralix.oneforall.base.client.settings;

import com.koralix.oneforall.base.BaseInit;
import com.koralix.oneforall.client.config.impl.ClientConfigValue;
import com.koralix.oneforall.config.ConfigCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;

public class ClientSettings {
    public static final ClientConfigValue<Boolean, ByteBuf> CONSUME_FIREWORKS = ClientConfigValue.create(
                    "0.1.0",
                    BaseInit.id("consume_fireworks"),
                    true,
                    ConfigCodec.BOOLEAN
            )
            .build();

    public static final ClientConfigValue<Boolean, ByteBuf> CENTER_FLOWERS = ClientConfigValue.create(
                    "0.1.0",
                    BaseInit.id("center_flowers"),
                    false,
                    ConfigCodec.BOOLEAN
            )
            .build();

    public static final ClientConfigValue<Boolean, ByteBuf> FLY_INERTIA = ClientConfigValue.create(
                    "0.1.0",
                    BaseInit.id("fly_inertia"),
                    true,
                    ConfigCodec.BOOLEAN
            )
            .build();

    public static final ClientConfigValue<Boolean, ByteBuf> FLAT_DIGGER = ClientConfigValue.create(
                    "0.1.0",
                    BaseInit.id("flat_digger"),
                    false,
                    ConfigCodec.BOOLEAN
            )
            .onChange((client, configValue, oldValue, newValue) -> {
                client.execute(() -> {
                    MinecraftClient.getInstance().worldRenderer.reload();
                });
            })
            .build();

    public static final ClientConfigValue<SurvivalCreativeMode, ByteBuf> DISABLE_BREAK_DELAY = ClientConfigValue.create(
                    "0.1.0",
                    BaseInit.id("disable_break_delay"),
                    SurvivalCreativeMode.NEVER,
                    ConfigCodec.of(SurvivalCreativeMode.CODEC, SurvivalCreativeMode.PACKET_CODEC, SurvivalCreativeMode.COMMAND_ADAPTER)
            )
            .build();

    public static final ClientConfigValue<Boolean, ByteBuf> DISABLE_TILT_WHEN_HURT = ClientConfigValue.create(
                    "0.1.0",
                    BaseInit.id("disable_tilt_when_hurt"),
                    false,
                    ConfigCodec.BOOLEAN
            )
            .build();

    public static final ClientConfigValue<Integer, ByteBuf> PREVENT_BREAKING_TOOLS = ClientConfigValue.create(
                    "0.1.0",
                    BaseInit.id("prevent_breaking_tools"),
                    0,
                    ConfigCodec.integer(0, Integer.MAX_VALUE)
            )
            .test(e -> e != null && e >= 0)
            .build();
}
