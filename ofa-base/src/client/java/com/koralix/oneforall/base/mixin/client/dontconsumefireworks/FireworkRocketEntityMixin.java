package com.koralix.oneforall.base.mixin.client.dontconsumefireworks;

import com.koralix.oneforall.base.duck.DontConsumeFireworks;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin implements DontConsumeFireworks {
    @Shadow
    private int life;

    @Shadow
    private int lifeTime;

    @Unique
    private boolean dontConsumeFirework = false;

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(final CallbackInfo info) {
        FireworkRocketEntity self = (FireworkRocketEntity) (Object) this;
        if (dontConsumeFirework && life > lifeTime) {
            self.discard();
        }
    }

    @Override
    @Unique
    public void oneforall$DontConsumeFirework() {
        this.dontConsumeFirework = true;
    }
}
