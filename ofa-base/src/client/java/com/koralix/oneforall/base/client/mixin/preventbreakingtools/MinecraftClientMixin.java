package com.koralix.oneforall.base.client.mixin.preventbreakingtools;

import com.koralix.oneforall.base.client.utils.PreventBreakingToolsHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Nullable public ClientPlayerEntity player;

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void doAttack(CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);

        if (PreventBreakingToolsHelper.preventDamagingTool(itemStack)) {
            cir.setReturnValue(false);
        }
    }
}
