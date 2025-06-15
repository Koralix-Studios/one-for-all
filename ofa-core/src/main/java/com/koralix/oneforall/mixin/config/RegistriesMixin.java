package com.koralix.oneforall.mixin.config;

import com.koralix.oneforall.config.loader.Storages;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Registries.class)
public class RegistriesMixin {
    @Inject(method = "freezeRegistries", at = @At("RETURN"))
    private static void onFreezeRegistries(CallbackInfo ci) {
        ConfigRegistry.freeze();
        Storages.loadDirect();
    }
}
