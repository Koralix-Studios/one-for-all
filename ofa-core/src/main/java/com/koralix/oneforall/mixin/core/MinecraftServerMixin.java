package com.koralix.oneforall.mixin.core;

import com.koralix.oneforall.init.Initializer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "runServer", at = @At("HEAD"))
    private void onRun(CallbackInfo ci) {
        MinecraftServer self = (MinecraftServer) (Object) this;
        Initializer.get().server(self);
        Initializer.LOGGER.info(
                "\"{}\" has been selected as the server mode",
                self.isDedicated()
                        ? "Dedicated Server"
                        : self.isSingleplayer()
                        ? "Singleplayer"
                        : "Unknown Mode"
        );
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onShutdown(CallbackInfo ci) {
        Initializer.get().server(null);
        Initializer.LOGGER.info("Exiting server mode");
    }
}
