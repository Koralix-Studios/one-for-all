package com.koralix.oneforall.mixin.lang;

import com.koralix.oneforall.lang.Language;
import com.koralix.oneforall.lang.TranslationUnit;
import com.koralix.oneforall.session.SessionHolder;
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
        TranslationUnit.prepare(Language.of(this.get()));
    }

    @Inject(method = "sendInternal", at = @At("RETURN"))
    private void reset(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        TranslationUnit.reset();
    }
}
