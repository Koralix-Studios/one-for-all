package com.koralix.oneforall.base.client.mixin.preventbreakingtools;

import com.koralix.oneforall.base.client.utils.PreventBreakingToolsHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"), cancellable = true)
    public void updateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (preventBreakingBlock()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean preventBreakingBlock() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

        return PreventBreakingToolsHelper.preventDamagingTool(stack);
    }
}