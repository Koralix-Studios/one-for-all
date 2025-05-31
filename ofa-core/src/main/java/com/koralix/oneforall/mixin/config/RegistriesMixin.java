package com.koralix.oneforall.mixin.config;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.loader.Storages;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(Registries.class)
public class RegistriesMixin {
    @Inject(method = "freezeRegistries", at = @At("RETURN"))
    private static void onFreezeRegistries(CallbackInfo ci) {
        ConfigRegistry.freeze();
        try {
            Storages.UNIVERSAL.path(FabricLoader.getInstance().getConfigDir().resolve(OneForAll.id()).resolve("universal.nbt"));
        } catch (IOException e) {
            OneForAll.logger().error("Failed to load universal config storage", e);
        }
    }
}
