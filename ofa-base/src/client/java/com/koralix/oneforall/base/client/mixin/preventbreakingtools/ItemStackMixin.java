package com.koralix.oneforall.base.client.mixin.preventbreakingtools;

import com.koralix.oneforall.base.client.utils.PreventBreakingToolsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (PreventBreakingToolsHelper.preventDamagingTool(context.getStack())) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (PreventBreakingToolsHelper.preventDamagingTool(user.getStackInHand(hand))) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    @Inject(method = "useOnEntity", at = @At("HEAD"), cancellable = true)
    private void useOnEntity(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (PreventBreakingToolsHelper.preventDamagingTool(user.getStackInHand(hand))) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
