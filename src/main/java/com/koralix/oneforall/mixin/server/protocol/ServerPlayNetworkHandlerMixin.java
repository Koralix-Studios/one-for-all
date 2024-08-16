package com.koralix.oneforall.mixin.server.protocol;

import com.koralix.oneforall.lang.TranslationUnit;
import com.koralix.oneforall.network.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin implements ClientSessionWrapper {
    @Shadow
    ServerPlayerEntity player;
    @Shadow
    ClientConnection connection;


    @Shadow private @Nullable PublicPlayerSession session;

    @Inject(
            method = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V",
            at = @At("HEAD")
    )
    private void setTranslationTargetUUID(Packet<?> packet, @Nullable PacketCallbacks callbacks, CallbackInfo ci) {
        TranslationUnit.prepare(player.getUuid());
    }

    @Override
    public ClientSession session() {
        return ((ClientSessionWrapper) connection).session();
    }

    @Override
    public void session(ClientSession session) {
        ((ClientSessionWrapper) connection).session(session);
    }

    @Inject(
            method = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;onClientSettings(Lnet/minecraft/network/packet/c2s/play/ClientSettingsC2SPacket;)V",
            at = @At("HEAD")
    )
    private void onClientSettings(ClientSettingsC2SPacket packet, CallbackInfo ci) {
        for (ActOnPlayPacketAction<ClientSettingsC2SPacket> action: ActOnPlayPacketHandler.getActionsFor(this.session(), ClientSettingsC2SPacket.class)) {
            action.execute(connection, player, packet);
        }
    }
}