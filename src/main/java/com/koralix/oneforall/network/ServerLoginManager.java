package com.koralix.oneforall.network;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.serde.Serde;
import com.koralix.oneforall.settings.ServerSettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;


public class ServerLoginManager {
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
            Optional<String> errorMessage = onQueryResponse(server1, handler1, understood, buf, synchronizer1, responseSender);
            errorMessage.ifPresent(s -> handler.disconnect(Text.of("Disconnected by OneForAll\n\nReason: " + s)));

            future.complete(null);
        });

        // SEND
        sender.sendPacket(getChannel("hello"), createHelloPacket());

        synchronizer.waitFor(future);
    }

    private static Optional<String> onQueryResponse(
            MinecraftServer server,
            ServerLoginNetworkHandler handler,
            boolean understood,
            PacketByteBuf buf,
            ServerLoginNetworking.LoginSynchronizer synchronizer,
            PacketSender responseSender
    ) {
        if (!understood && ServerSettings.ENFORCE_PROTOCOL.value()) {
            return Optional.of("One-For-All Mod Protocol is required to connect to the server.");
        } else if (!understood) {
            return Optional.empty();
        }

        // If understood
        String version = buf.readString();
        OneForAll.getInstance().getLogger().debug("Client connected with version: {}", version);

        return Optional.empty();
    }
}