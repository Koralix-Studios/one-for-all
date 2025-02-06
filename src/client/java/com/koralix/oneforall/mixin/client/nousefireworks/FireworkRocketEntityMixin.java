package com.koralix.oneforall.mixin.client.nousefireworks;

import com.koralix.oneforall.duck.FireworkGetter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {
    @Shadow private int life;

    @Shadow private int lifeTime;

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(final CallbackInfo info) {
        FireworkRocketEntity self = (FireworkRocketEntity) (Object) this;
        if (((FireworkGetter)MinecraftClient.getInstance().interactionManager).isActiveFirework(self)) {
            if (life > lifeTime) {
                self.discard();
            }
        }
    }
}
