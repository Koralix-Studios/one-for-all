package com.koralix.oneforall.base.client.mixin.disabletiltwhenhurt;

import com.koralix.oneforall.base.client.settings.ClientSettings;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(
            method = "tiltViewWhenHurt",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tiltViewWhenHurt(MatrixStack matrices, float tickProgress, CallbackInfo ci) {
        if (ClientSettings.DISABLE_TILT_WHEN_HURT.get()) {
            ci.cancel();
        }
    }
}
