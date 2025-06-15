package com.koralix.oneforall.mixin.session;

import com.koralix.oneforall.session.Session;
import com.koralix.oneforall.session.SessionHolder;
import com.koralix.oneforall.session.component.GameProfileComponent;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerLoginNetworkHandler.class)
@Implements(@Interface(iface = SessionHolder.class, prefix = "holder$", unique = true))
public abstract class ServerLoginNetworkHandlerMixin implements SessionHolder {
    @Shadow @Final ClientConnection connection;

    public @NotNull Session holder$get() {
        return ((SessionHolder) this.connection).get();
    }

    @Inject(method = "sendSuccessPacket", at = @At("HEAD"))
    public void catchGameProfile(GameProfile profile, CallbackInfo ci) {
        Session session = this.holder$get();
        session.set(new GameProfileComponent(profile));
    }
}
