package com.koralix.oneforall.mixin.lang;

import com.koralix.oneforall.lang.Language;
import com.koralix.oneforall.lang.TranslationUnit;
import com.koralix.oneforall.session.Session;
import com.koralix.oneforall.session.SessionHolder;
import com.koralix.oneforall.session.component.VersionComponent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=1.21.6 {
import io.netty.channel.ChannelFutureListener;
//?} else {
/*import net.minecraft.network.PacketCallbacks;
*///?}

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin implements SessionHolder {
    @Inject(method = "sendInternal", at = @At("HEAD"))
    private void prepare(
            Packet<?> packet,
            //? if >=1.21.6 {
            @Nullable ChannelFutureListener listener,
            //?} else {
            /*@Nullable PacketCallbacks callbacks,
             *///?}
            boolean flush,
            CallbackInfo ci
    ) {
        Session session = this.get();
        VersionComponent version = session.get(VersionComponent.TYPE);
        if (version != null && version.isCompatible()) return;
        TranslationUnit.prepare(Language.of(session));
    }

    @Inject(method = "sendInternal", at = @At("RETURN"))
    private void reset(
            Packet<?> packet,
            //? if >=1.21.6 {
            @Nullable ChannelFutureListener listener,
            //?} else {
            /*@Nullable PacketCallbacks callbacks,
             *///?}
            boolean flush,
            CallbackInfo ci
    ) {
        TranslationUnit.reset();
    }
}
