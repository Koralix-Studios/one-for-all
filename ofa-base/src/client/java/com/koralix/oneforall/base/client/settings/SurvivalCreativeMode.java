package com.koralix.oneforall.base.client.settings;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum SurvivalCreativeMode implements StringIdentifiable {
    NEVER,
    SURVIVAL,
    CREATIVE,
    ALWAYS;

    public static final @NotNull Codec<SurvivalCreativeMode> CODEC = StringIdentifiable.createCodec(SurvivalCreativeMode::values);
    private static final IntFunction<SurvivalCreativeMode> BY_ID = ValueLists.createIndexToValueFunction(
            SurvivalCreativeMode::ordinal, values(), ValueLists.OutOfBoundsHandling.WRAP
    );
    public static final PacketCodec<ByteBuf, SurvivalCreativeMode> PACKET_CODEC = PacketCodecs.indexed(BY_ID, SurvivalCreativeMode::ordinal);

    @Override
    public @NotNull String asString() {
        return this.name().toLowerCase();
    }

    public boolean onSurvival() {
        return switch (this) {
            case SURVIVAL, ALWAYS -> true;
            default -> false;
        };
    }

    public boolean onCreative() {
        return switch (this) {
            case CREATIVE, ALWAYS -> true;
            default -> false;
        };
    }
}

