package com.koralix.oneforall.client.session;

import com.koralix.oneforall.CoreInit;
import com.koralix.oneforall.client.settings.ClientSettings;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
//? if >=1.21.6 {
import io.netty.channel.ChannelFutureListener;
//?} else {
/*import net.minecraft.network.PacketCallbacks;
 *///?}

public class LoginManager {
    public static void init() {
        ClientLoginConnectionEvents.QUERY_START.register((handler, client) -> {
            ClientLoginNetworking.registerReceiver(CoreInit.id("hello"), LoginManager::onHello);
        });
    }

    private static @NotNull CompletableFuture<@Nullable PacketByteBuf> onHello(
            MinecraftClient client,
            ClientLoginNetworkHandler handler,
            @NotNull PacketByteBuf buf,
            //? if >=1.21.6 {
            @Nullable Consumer<ChannelFutureListener> listener
            //?} else {
            /*@Nullable Consumer<PacketCallbacks> callbacks
             *///?}
    ) {
        String version = buf.readString();
        boolean enforceProtocol = buf.readBoolean();

        if (!ClientSettings.PROTOCOL_USAGE_CONDITION.get().isActive(enforceProtocol)) {
            return CompletableFuture.completedFuture(null);
        }

        PacketByteBuf response = PacketByteBufs.create();
        response.writeString(CoreInit.version().getFriendlyString());
        response.writeString(client.options.language);
        return CompletableFuture.completedFuture(response);
    }
}
