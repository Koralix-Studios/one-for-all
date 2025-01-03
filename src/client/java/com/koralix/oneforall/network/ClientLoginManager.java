package com.koralix.oneforall.network;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.settings.ClientSettings;
import com.koralix.oneforall.settings.ProtocolUsageConditions;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ClientLoginManager {
    public static void init() {
        ClientLoginConnectionEvents.QUERY_START.register((handler, client) -> {
            ClientLoginNetworking.registerReceiver(ServerLoginManager.getChannel("hello"), ClientLoginManager::onHello);
        });
    }

    private static boolean doesClientEnableProtocol(boolean enforceProtocol) {
        ProtocolUsageConditions usageConditions = ClientSettings.PROTOCOL_USAGE_CONDITIONS.value();

        return switch (usageConditions) {
            case ALWAYS -> true;
            case ONLY_ENFORCED -> enforceProtocol;
            case NEVER -> false;
        };
    }

    private static CompletableFuture<PacketByteBuf> onHello(MinecraftClient client, ClientLoginNetworkHandler handler, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
        String version = buf.readString();
        boolean enforceProtocol = buf.readBoolean();

        boolean enablesProtocol = doesClientEnableProtocol(enforceProtocol);
        if (!enablesProtocol) {
            return CompletableFuture.completedFuture(null);
        }

        PacketByteBuf response = PacketByteBufs.create();
        response.writeString(OneForAll.getInstance().getMetadata().version());
        return CompletableFuture.completedFuture(response);
    }

}
