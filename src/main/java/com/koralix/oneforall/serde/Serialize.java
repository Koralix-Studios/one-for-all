package com.koralix.oneforall.serde;

import net.minecraft.network.PacketByteBuf;

public interface Serialize {
    /**
     * Serialize this object to a packet buffer
     * @param buf the packet buffer to serialize to
     */
    default void serialize(PacketByteBuf buf) {
        Serde.serialize(buf, this);
    }
}
