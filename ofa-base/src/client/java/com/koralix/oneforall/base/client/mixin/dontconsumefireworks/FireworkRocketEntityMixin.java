package com.koralix.oneforall.base.client.mixin.dontconsumefireworks;

import com.koralix.oneforall.base.client.duck.DontConsumeFireworks;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
@Implements(@Interface(iface = DontConsumeFireworks.class, prefix = "oneforall$"))
public abstract class FireworkRocketEntityMixin implements DontConsumeFireworks {
    @Shadow
    private int life;

    @Shadow
    private int lifeTime;

    @Unique
    private boolean consumeFirework = true;

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(final CallbackInfo info) {
        FireworkRocketEntity self = (FireworkRocketEntity) (Object) this;
        if (!consumeFirework && life > lifeTime) {
            self.discard();
        }
    }

    @Unique
    public void oneforall$dontConsumeFirework() {
        this.consumeFirework = false;
    }
}
