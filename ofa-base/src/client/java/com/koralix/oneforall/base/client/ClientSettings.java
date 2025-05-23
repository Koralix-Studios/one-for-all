package com.koralix.oneforall.base.client;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.MonoConfigValue;
import com.koralix.oneforall.config.registry.ConfigRegistrar;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.koralix.oneforall.config.registry.VersionedIdentifier;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

public class ClientSettings {
    private static final ConfigRegistrar REGISTRAR = ConfigRegistry.builder("0.1.0", OneForAll.id("client_settings")).prepare();

    public static final MonoConfigValue<Boolean> CONSUME_FIREWORKS = REGISTRAR
            .mono(VersionedIdentifier.of("0.1.0", OneForAll.id("consume_fireworks")), true, Codec.BOOL, PacketCodecs.BOOLEAN)
            .build();

    public static final MonoConfigValue<Boolean> CENTER_FLOWERS = REGISTRAR
            .mono(VersionedIdentifier.of("0.1.0", OneForAll.id("center_flowers")), false, Codec.BOOL, PacketCodecs.BOOLEAN)
            .build();

    public static final MonoConfigValue<Boolean> FLY_INERTIA = REGISTRAR
            .mono(VersionedIdentifier.of("0.1.0", OneForAll.id("fly_inertia")), true, Codec.BOOL, PacketCodecs.BOOLEAN)
            .build();

    public static @NotNull ConfigRegistry register() {
        return REGISTRAR.complete();
    }
}
