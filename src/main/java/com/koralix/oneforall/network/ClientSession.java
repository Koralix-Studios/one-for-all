package com.koralix.oneforall.network;

import com.koralix.oneforall.lang.Language;
import com.koralix.oneforall.utils.OnceCell;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientSession {
    private final ClientConnection connection;
    private final OnceCell<ServerPlayerEntity> player = new OnceCell<>();
    private final Map<Class<? extends Packet<?>>, List<ActOnPlayPacketAction<?>>> actionsOnPlayPacket = new HashMap<>();
    private Language language;
    private String modVersion;

    public ClientSession(ClientConnection connection) {
        this.connection = connection;
    }

    public Language language() {
        return language;
    }

    public void language(Language language) {
        this.language = language;
    }

    public String modVersion() {
        return modVersion;
    }

    public void modVersion(String modVersion) {
        this.modVersion = modVersion;
    }

    @Override
    public String toString() {
        return "ClientSession{" +
                "language='" + language + '\'' +
                ", modVersion='" + modVersion + '\'' +
                '}';
    }

    public <T extends Packet<?>> void onPlayPacket(
            Class<T> clazz,
            ActOnPlayPacketAction<T> action
    ) {
        actionsOnPlayPacket.computeIfAbsent(clazz, k -> new ArrayList<>()).add(action);
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet<?>> void onPlayPacket(T packet) {
        actionsOnPlayPacket
                .getOrDefault(packet.getClass(), List.of())
                .forEach(action -> ((ActOnPlayPacketAction<T>) action).execute(connection, player.get(), packet));
    }
}
