package com.koralix.oneforall.mixin.server.protocol;

import com.koralix.oneforall.network.ClientSession;
import com.koralix.oneforall.network.ClientSessionWrapper;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginNetworkHandlerMixin implements ClientSessionWrapper {
    @Shadow private ClientConnection connection;

    @Override
    public ClientSession session() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void session(@NotNull ClientSession session) {
        ClientSessionWrapper modConnection = (ClientSessionWrapper) Objects.requireNonNull(connection);

        modConnection.session(session);
    }
}
