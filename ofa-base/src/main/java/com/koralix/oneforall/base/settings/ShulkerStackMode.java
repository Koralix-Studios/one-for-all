package com.koralix.oneforall.base.settings;

import com.koralix.oneforall.config.ConfigCommandAdapter;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum ShulkerStackMode implements StringIdentifiable {
    NEVER,
    ON_INVENTORY,
    ON_INVENTORY_AND_GROUND,
    ON_INVENTORY_AND_HOPPER,
    ALWAYS_EXCEPT_COMPARATOR,
    ALWAYS;

    public static final @NotNull Codec<ShulkerStackMode> CODEC = StringIdentifiable.createCodec(ShulkerStackMode::values);
    private static final IntFunction<ShulkerStackMode> BY_ID = ValueLists.createIndexToValueFunction(
            ShulkerStackMode::ordinal, values(), ValueLists.OutOfBoundsHandling.WRAP
    );
    public static final PacketCodec<ByteBuf, ShulkerStackMode> PACKET_CODEC = PacketCodecs.indexed(BY_ID, ShulkerStackMode::ordinal);
    public static final ConfigCommandAdapter<ShulkerStackMode> COMMAND_ADAPTER = ConfigCommandAdapter.ofEnum(ShulkerStackMode.class);

    @Override
    public @NotNull String asString() {
        return this.name().toLowerCase();
    }

    public boolean onGround() {
        return switch (this) {
            case ON_INVENTORY_AND_GROUND, ALWAYS_EXCEPT_COMPARATOR, ALWAYS -> true;
            default -> false;
        };
    }

    public boolean onInventory() {
        return switch (this) {
            case ON_INVENTORY, ON_INVENTORY_AND_GROUND, ON_INVENTORY_AND_HOPPER, ALWAYS_EXCEPT_COMPARATOR, ALWAYS -> true;
            default -> false;
        };
    }

    public boolean byHopper() {
        return switch (this) {
            case ON_INVENTORY_AND_HOPPER, ALWAYS_EXCEPT_COMPARATOR, ALWAYS -> true;
            default -> false;
        };
    }
}
