package com.koralix.oneforall.base.client.mixin.flatdigger;

import com.koralix.oneforall.base.client.settings.ClientSettings;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Redirect(method = "attackBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isBlockBreakingRestricted(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/GameMode;)Z"))
    public boolean isBlockBreakingRestricted(ClientPlayerEntity instance, World world, BlockPos blockPos, GameMode gameMode) {
        if (instance.isBlockBreakingRestricted(world, blockPos, gameMode)) return true;
        if (!ClientSettings.FLAT_DIGGER.value()) return false;

        return instance.getBlockPos().getY() > blockPos.getY();
    }
}
