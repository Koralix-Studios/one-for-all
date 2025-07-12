package com.koralix.oneforall.base.mixin.creativekill;

import com.koralix.oneforall.base.settings.PlayerSettings;
import com.koralix.oneforall.base.settings.ServerSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Unique
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @Inject(method = "attack", at = @At("HEAD"))
    private void attack(Entity target, CallbackInfo ci) {
        if (isActive(player) && target.isAttackable() && !target.handleAttack(player) && player.isCreative()) {
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

    @Unique
    private static boolean isActive(@NotNull PlayerEntity player) {
        return ServerSettings.CREATIVE_KILL.get() && (
                player instanceof ServerPlayerEntity server
                        ? PlayerSettings.CREATIVE_KILL.get(server).isActive(player)
                        : PlayerSettings.CREATIVE_KILL.nominalValue().isActive(player)
        );
    }

}
