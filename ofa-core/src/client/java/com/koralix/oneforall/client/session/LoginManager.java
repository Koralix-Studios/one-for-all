package com.koralix.oneforall.client.session;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.client.settings.ClientSettings;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketCallbacks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class LoginManager {
    public static void init() {
        ClientLoginConnectionEvents.QUERY_START.register((handler, client) -> {
            ClientLoginNetworking.registerReceiver(OneForAll.id("hello"), LoginManager::onHello);
        });
    }

    private static @NotNull CompletableFuture<@Nullable PacketByteBuf> onHello(MinecraftClient client, ClientLoginNetworkHandler handler, @NotNull PacketByteBuf buf, Consumer<PacketCallbacks> consumer) {
        String version = buf.readString();
        boolean enforceProtocol = buf.readBoolean();

        if (!ClientSettings.PROTOCOL_USAGE_CONDITION.value().isActive(enforceProtocol)) {
            return CompletableFuture.completedFuture(null);
        }

        PacketByteBuf response = PacketByteBufs.create();
        response.writeString(OneForAll.version().getFriendlyString());
        response.writeString(client.options.language);
        return CompletableFuture.completedFuture(response);
    }
}
