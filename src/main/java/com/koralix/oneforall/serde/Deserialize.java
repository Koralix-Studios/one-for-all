package com.koralix.oneforall.serde;

import net.minecraft.network.PacketByteBuf;

public interface Deserialize {
    /**
     * Deserialize this object from a packet buffer
     * @param buf the packet buffer to deserialize from
     */
    default void deserialize(PacketByteBuf buf) {
        Serde.deserialize(buf, this.getClass());
    }
}
