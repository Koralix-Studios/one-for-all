package com.koralix.oneforall.base.mixin.creativekill;

import com.koralix.oneforall.base.settings.PlayerSettings;
import com.koralix.oneforall.base.settings.ServerSettings;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Unique
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @Inject(method = "attack", at = @At("HEAD"))
    private void attack(Entity target, CallbackInfo ci) {
        if (PlayerSettings.CREATIVE_KILL.value(player.getUuid()).isActive(player, ServerSettings.CREATIVE_KILL) && target.isAttackable() && !target.handleAttack(player) && player.isCreative()) {
            if (target instanceof ServerPlayerEntity other) {
                if (!other.isCreative()) {
                    //? if >=1.21.6 {
                     target.kill(other.getWorld());
                     //?} else {
                    /*target.kill(other.getServerWorld());
                    *///?}
                }
            } else if (target.getWorld() instanceof ServerWorld world) {
                target.kill(world);
            }
        }
    }

}
