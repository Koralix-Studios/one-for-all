package com.koralix.oneforall.mixin.session;

import com.koralix.oneforall.session.Session;
import com.koralix.oneforall.session.SessionHolder;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerEntity.class)
@Implements(@Interface(iface = SessionHolder.class, prefix = "holder$", unique = true))
public abstract class ServerPlayerEntityMixin implements SessionHolder {
    @Shadow
    public ServerPlayNetworkHandler networkHandler;

    public @NotNull Session holder$get() {
        return ((SessionHolder) this.networkHandler).get();
    }
}
