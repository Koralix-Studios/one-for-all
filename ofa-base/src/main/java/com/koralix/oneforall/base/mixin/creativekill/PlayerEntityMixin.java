package com.koralix.oneforall.base.mixin.creativekill;

import com.koralix.oneforall.base.settings.PlayerSettings;
import com.koralix.oneforall.base.settings.ServerSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Unique
    private final PlayerEntity player = (PlayerEntity) (Object) this;

    @Inject(method = "attack", at = @At("HEAD"))
    private void attack(Entity target, CallbackInfo ci) {
        if (PlayerSettings.CREATIVE_KILL.value(player.getUuid()).isActive(player, ServerSettings.CREATIVE_KILL) && target.isAttackable() && !target.handleAttack(player) && player.isCreative()) {
            if (!(target.getWorld() instanceof ServerWorld world)) return;

            if (target instanceof PlayerEntity other) {
                if (!other.isCreative())
                    target.kill(world);
            } else
                target.kill(world);
        }
    }

}
