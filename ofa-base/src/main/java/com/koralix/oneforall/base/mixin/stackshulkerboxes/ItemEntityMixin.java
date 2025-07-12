package com.koralix.oneforall.base.mixin.stackshulkerboxes;

import com.koralix.oneforall.base.settings.ServerSettings;
import com.koralix.oneforall.entry.OneForAll;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.ItemEntity;
import net.minecraft.registry.tag.ItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @WrapOperation(
            method = "tryMerge()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ItemEntity;canMerge()Z"
            )
    )
    private boolean canMerge1(ItemEntity instance, Operation<Boolean> original) {
        if (!ServerSettings.STACK_SHULKER_BOXES.get().onGround() && instance.getStack().isIn(ItemTags.SHULKER_BOXES))
            return false;
        return original.call(instance);
    }
}
