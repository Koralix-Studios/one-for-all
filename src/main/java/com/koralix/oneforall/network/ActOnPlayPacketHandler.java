package com.koralix.oneforall.network;

import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActOnPlayPacketHandler {
    private static final Map<ClientSession, Map<Class<Packet<?>>, List<ActOnPlayPacketAction<?>>>> REGISTRY = new HashMap<>();

    public static <T extends Packet<?>> void register(Class<T> clazz, ClientSession session, ActOnPlayPacketAction<T> action) {
        if (!REGISTRY.containsKey(session)) REGISTRY.put(session, new HashMap<>());
        if (!REGISTRY.get(session).containsKey(clazz)) REGISTRY.get(session).put((Class<Packet<?>>) clazz, new ArrayList<>());

        REGISTRY.get(session).get(clazz).add(action);
    }

    public static <T extends Packet<?>> List<ActOnPlayPacketAction<T>> getActionsFor(ClientSession session, Class<T> clazz) {
        if (REGISTRY.containsKey(session) && REGISTRY.get(session).containsKey(clazz)) {
            // This cast should be safe since T extends Packet<?>
            return (List<ActOnPlayPacketAction<T>>)(Object)REGISTRY.get(session).get(clazz);
        }
        return new ArrayList<>();
    }
}
