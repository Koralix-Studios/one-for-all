package com.koralix.oneforall.mixin.lang;

import com.koralix.oneforall.lang.TranslationUnit;
import com.koralix.oneforall.session.Session;
import com.koralix.oneforall.session.SessionHolder;
import com.koralix.oneforall.session.component.LangComponent;
import com.koralix.oneforall.session.component.VersionComponent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin implements SessionHolder {
    @Inject(method = "sendInternal", at = @At("HEAD"))
    private void prepare(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        Session session = this.get();
        VersionComponent versionComponent = session.get(VersionComponent.TYPE);
//        if (versionComponent != null && versionComponent.isCompatible()) return;
        LangComponent langComponent = session.get(LangComponent.TYPE);
        if (langComponent == null) return;
        TranslationUnit.prepare(langComponent.language());
    }

    @Inject(method = "sendInternal", at = @At("RETURN"))
    private void reset(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        TranslationUnit.reset();
    }
}
