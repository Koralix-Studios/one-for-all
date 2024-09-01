package com.koralix.oneforall.network;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface ActOnPlayPacketAction<T extends Packet<?>>  {
    void execute(ClientConnection connection, ServerPlayerEntity player, T packet);
}
