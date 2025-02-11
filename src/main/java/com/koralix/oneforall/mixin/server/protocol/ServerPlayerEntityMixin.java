package com.koralix.oneforall.mixin.server.protocol;

import com.koralix.oneforall.network.ClientSession;
import com.koralix.oneforall.network.ClientSessionWrapper;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements ClientSessionWrapper {
    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Override
    public ClientSession session() {
        return ((ClientSessionWrapper) this.networkHandler).session();
    }

    @Override
    public void session(ClientSession session) {
        ((ClientSessionWrapper) this.networkHandler).session(session);
    }
}
