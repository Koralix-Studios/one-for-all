package com.koralix.oneforall.mixin.session;

import com.koralix.oneforall.session.Session;
import com.koralix.oneforall.session.SessionHolder;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

@Mixin(ServerLoginNetworkHandler.class)
@Implements(@Interface(iface = SessionHolder.class, prefix = "holder$", unique = true))
public abstract class ServerLoginNetworkHandlerMixin implements SessionHolder {
    @Shadow @Final ClientConnection connection;

    public @NotNull Session holder$get() {
        return ((SessionHolder) this.connection).get();
    }
}
