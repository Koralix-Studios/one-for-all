package com.koralix.oneforall.mixin.server.protocol;

import com.koralix.oneforall.network.ClientSessionWrapper;
import com.koralix.oneforall.network.ClientSession;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin implements ClientSessionWrapper {
    private ClientSession session;

    @Override
    public ClientSession session() {
        return session;
    }

    @Override
    public void session(ClientSession session) {
        this.session = session;
    }

}
