package com.koralix.oneforall.mixin.session;

import com.koralix.oneforall.session.SessionHolder;
import com.koralix.oneforall.session.component.PlayerComponent;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void initSession(
            MinecraftServer server,
            ClientConnection connection,
            ServerPlayerEntity player,
            ConnectedClientData clientData,
            CallbackInfo ci
    ) {
        ((SessionHolder) connection).get().set(new PlayerComponent(player));
    }
}
