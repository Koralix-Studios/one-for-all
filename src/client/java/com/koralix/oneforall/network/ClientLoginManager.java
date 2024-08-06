package com.koralix.oneforall.network;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.serde.Serde;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ClientLoginManager {
    public static void init() {
        ClientLoginConnectionEvents.QUERY_START.register((handler, client) -> {
            ClientLoginNetworking.registerReceiver(ServerLoginManager.getChannel("hello"), ClientLoginManager::onHello);
        });
    }

    private static CompletableFuture<PacketByteBuf> onHello(MinecraftClient client, ClientLoginNetworkHandler handler, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
        PacketByteBuf response = PacketByteBufs.create();
        Serde.serialize(response, OneForAll.getInstance().getMetadata().version());

        return CompletableFuture.completedFuture(response);
    }

}
