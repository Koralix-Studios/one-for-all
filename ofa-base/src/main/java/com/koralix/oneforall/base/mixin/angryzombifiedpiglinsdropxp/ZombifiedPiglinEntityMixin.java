package com.koralix.oneforall.base.mixin.angryzombifiedpiglinsdropxp;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombifiedPiglinEntity.class)
public abstract class ZombifiedPiglinEntityMixin extends LivingEntity {
    @Shadow public abstract int getAngerTime();

    @Unique
    private final ZombifiedPiglinEntity self = (ZombifiedPiglinEntity) (Object) this;

    protected ZombifiedPiglinEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "mobTick", at = @At("RETURN"))
    private void ofa$angryZombifiedPiglinsDropXP$mobTick(CallbackInfo ci) {
        if (self.hasAngerTime()) this.playerHitTimer = 100;
    }

    @Inject(method = "setTarget", at = @At("RETURN"))
    private void ofa$angryZombifiedPiglinsDropXP$setTarget(LivingEntity target, CallbackInfo ci) {
        if (target instanceof PlayerEntity player) self.setAttacking(player, 100);
    }
}
