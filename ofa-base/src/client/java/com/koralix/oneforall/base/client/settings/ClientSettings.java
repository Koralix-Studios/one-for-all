package com.koralix.oneforall.base.client.settings;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.MonoConfigValue;
import com.koralix.oneforall.config.adapter.CommandAdapter;
import com.koralix.oneforall.config.registry.ConfigRegistrar;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.koralix.oneforall.config.registry.VersionedIdentifier;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ClientSettings {
    private static final ConfigRegistrar REGISTRAR = ConfigRegistry.builder("0.1.0", OneForAll.id("client_settings")).prepare();

    public static final MonoConfigValue<Boolean, ByteBuf, ?> CONSUME_FIREWORKS = REGISTRAR
            .mono(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("consume_fireworks")),
                    true,
                    Codec.BOOL,
                    PacketCodecs.BOOLEAN,
                    CommandAdapter.bool()
            )
            .test(Objects::nonNull)
            .build();

    public static final MonoConfigValue<Boolean, ByteBuf, ?> CENTER_FLOWERS = REGISTRAR
            .mono(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("center_flowers")),
                    false,
                    Codec.BOOL,
                    PacketCodecs.BOOLEAN,
                    CommandAdapter.bool()
            )
            .test(Objects::nonNull)
            .build();

    static {
        CENTER_FLOWERS.onChange((configValue, oldValue, newValue) -> {
            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().worldRenderer.reload();
            });
        });
    }

    public static final MonoConfigValue<Boolean, ByteBuf, ?> FLY_INERTIA = REGISTRAR
            .mono(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("fly_inertia")),
                    true,
                    Codec.BOOL,
                    PacketCodecs.BOOLEAN,
                    CommandAdapter.bool()
            )
            .test(Objects::nonNull)
            .build();

    public static final MonoConfigValue<Boolean, ByteBuf, ?> FLAT_DIGGER = REGISTRAR
            .mono(
                    VersionedIdentifier.of("0.1.0", OneForAll.id("flat_digger")),
                    false,
                    Codec.BOOL,
                    PacketCodecs.BOOLEAN,
                    CommandAdapter.bool()
            )
            .test(Objects::nonNull)
            .build();

    public static @NotNull ConfigRegistry register() {
        return REGISTRAR.complete();
    }
}
