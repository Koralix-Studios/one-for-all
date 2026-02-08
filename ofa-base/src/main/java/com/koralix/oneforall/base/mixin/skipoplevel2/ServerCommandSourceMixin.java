package com.koralix.oneforall.base.mixin.skipoplevel2;

import com.koralix.oneforall.base.settings.ServerSettings;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerCommandSource.class)
public class ServerCommandSourceMixin {
    @Shadow @Final private int level;

    @Inject(method = "hasPermissionLevel", at = @At("HEAD"), cancellable = true)
    private void skipOpLevel2(int level, CallbackInfoReturnable<Boolean> cir) {
        if (ServerSettings.skipOpLevel2.get() && level == 3 && this.level == 1) {
            cir.setReturnValue(true);
        }
    }
}
