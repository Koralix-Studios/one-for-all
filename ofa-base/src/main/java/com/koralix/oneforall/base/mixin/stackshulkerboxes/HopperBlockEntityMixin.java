package com.koralix.oneforall.base.mixin.stackshulkerboxes;

import com.koralix.oneforall.base.settings.ServerSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
    @WrapOperation(
            method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/HopperBlockEntity;canMergeItems(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    private static boolean canMergeItems(ItemStack first, ItemStack second, Operation<Boolean> original) {
        if (!ServerSettings.STACK_SHULKER_BOXES.value().byHopper() && first.isIn(ItemTags.SHULKER_BOXES)) return false;
        return original.call(first, second);
    }
}
