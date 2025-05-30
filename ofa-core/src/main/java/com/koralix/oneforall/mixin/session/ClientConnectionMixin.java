package com.koralix.oneforall.mixin.session;

import com.koralix.oneforall.session.Session;
import com.koralix.oneforall.session.SessionHolder;
import net.minecraft.network.ClientConnection;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientConnection.class)
@Implements(@Interface(iface = SessionHolder.class, prefix = "holder$", unique = true))
public abstract class ClientConnectionMixin implements SessionHolder {
    @Unique
    private final Session session = new Session((ClientConnection) (Object) this);

    public @NotNull Session holder$get() {
        return this.session;
    }
}
