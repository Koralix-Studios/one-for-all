package com.koralix.oneforall.base.settings;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.MonoConfigValue;
import com.koralix.oneforall.config.adapter.CommandAdapter;
import com.koralix.oneforall.config.registry.ConfigRegistrar;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.koralix.oneforall.config.registry.VersionedIdentifier;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CommandSettings {
    private static final ConfigRegistrar REGISTRAR = ConfigRegistry.builder("0.1.0", OneForAll.id("command_settings")).prepare();

    public static final MonoConfigValue<Boolean, ByteBuf, ?> COMMAND_SIGNAL = create("0.1.0", "command_signal", false);
    public static final MonoConfigValue<Boolean, ByteBuf, ?> COMMAND_BATCH = create("0.1.0", "command_batch", false);
    public static final MonoConfigValue<Boolean, ByteBuf, ?> COMMAND_ENDERCHEST = create("0.1.0", "command_enderchest", false);

    private static MonoConfigValue<Boolean, ByteBuf, ?> create(
            @NotNull String version,
            @NotNull String id,
            boolean value
    ) {
        return REGISTRAR
                .mono(
                        VersionedIdentifier.of(version, OneForAll.id(id)),
                        value,
                        Codec.BOOL,
                        PacketCodecs.BOOLEAN,
                        CommandAdapter.bool()
                )
                .test(Objects::nonNull)
                .build();
    }

    public static @NotNull ConfigRegistry register() {
        return REGISTRAR.complete();
    }
}
