package com.koralix.oneforall.network;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.serde.Serde;
import com.koralix.oneforall.settings.ServerSettings;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;


public class ServerLoginManager {
    public static final Map<UUID, ClientSession> SESSIONS = new HashMap<>();
    public static void init() {
        ServerLoginConnectionEvents.QUERY_START.register(ServerLoginManager::onQueryStart);
    }

    private static PacketByteBuf createHelloPacket() {
        String modVersion = OneForAll.getInstance().getMetadata().version();
        boolean enforceProtocol = ServerSettings.ENFORCE_PROTOCOL.value();

        PacketByteBuf buf = PacketByteBufs.create();
        Serde.serialize(buf, modVersion);
        Serde.serialize(buf, enforceProtocol);

        return buf;
    }

    public static Identifier getChannel(String name) {
        return Identifier.of(OneForAll.MOD_ID, name);
    }

    private static void onQueryStart(
            ServerLoginNetworkHandler handler,
            MinecraftServer server,
            PacketSender sender,
            ServerLoginNetworking.LoginSynchronizer synchronizer
    ) {
        if (!ServerSettings.PROTOCOL_ENABLED.value()) return;

        // RECEIVE
        CompletableFuture<Void> future = new CompletableFuture<>();

        ServerLoginNetworking.registerReceiver(handler, getChannel("hello"), (server1, handler1, understood, buf, synchronizer1, responseSender) -> {
            Optional<Text> errorMessage = onQueryResponse(server1, handler1, understood, buf, synchronizer1, responseSender);

            errorMessage.ifPresent(s -> {
                Text disconnectMessage = Text.translatable("text.oneforall.disconnected").append(s);

                handler.disconnect(disconnectMessage);
            });

            future.complete(null);
        });

        // SEND
        sender.sendPacket(getChannel("hello"), createHelloPacket());

        synchronizer.waitFor(future);
    }

    private static Optional<Text> onQueryResponse(
            MinecraftServer server,
            ServerLoginNetworkHandler handler,
            boolean understood,
            PacketByteBuf buf,
            ServerLoginNetworking.LoginSynchronizer synchronizer,
            PacketSender responseSender
    ) {
        ClientSession session = new ClientSession();
        ((ClientSessionWrapper) handler).session(session);
        ActOnPlayPacketHandler.register(ClientSettingsC2SPacket.class, session, (connection, player, packet) -> {
            ClientSession sessionWrapper = ((ClientSessionWrapper) connection).session();
            sessionWrapper.language(packet.language());

            ((ClientSessionWrapper) connection).session(sessionWrapper);
            ServerLoginManager.SESSIONS.put(player.getUuid(), sessionWrapper);
        });
        if (!understood && ServerSettings.ENFORCE_PROTOCOL.value()) {
            return Optional.of(Text.translatable("text.oneforall.disconnected.notUsingProtocol"));
        } else if (!understood) {
            return Optional.empty();
        }

        // If understood
        Optional<String> version = readSafe(buf, PacketByteBuf::readString);
        Optional<String> language = readSafe(buf, PacketByteBuf::readString);
        OneForAll.getInstance().getLogger().debug("Client connected with version: {}", version);

        session.modVersion(version.orElse(null));
        session.language(language.orElse(null));
        ((ClientSessionWrapper) handler).session(session);

        return Optional.empty();
    }

    private static <T> Optional<T> readSafe(PacketByteBuf buf, Function<PacketByteBuf, T> function) {
        try {
           return Optional.of(function.apply(buf));
        } catch (Exception e) {
           return Optional.empty();
        }
    }
}