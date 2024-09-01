package com.koralix.oneforall.mixin.server.fapi;

import net.fabricmc.fabric.impl.networking.GlobalReceiverRegistry;
import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkAddon;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLoginNetworkAddon.class)
public class FixServerLoginNetworkAddonRegisterReceiver {
    @Redirect(
            method = "handle(ILnet/minecraft/network/PacketByteBuf;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/fabricmc/fabric/impl/networking/GlobalReceiverRegistry;getHandler(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;"
            ))
    private Object fix(GlobalReceiverRegistry instance, Identifier channelName) {
        return ((ServerLoginNetworkAddon) (Object) this).getHandler(channelName);
    }
}
