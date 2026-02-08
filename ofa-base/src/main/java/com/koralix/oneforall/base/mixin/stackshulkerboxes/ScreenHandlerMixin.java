package com.koralix.oneforall.base.mixin.stackshulkerboxes;

import com.koralix.oneforall.base.settings.ServerSettings;
import com.koralix.oneforall.base.settings.ShulkerStackMode;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @WrapOperation(
            method = "calculateComparatorOutput(Lnet/minecraft/inventory/Inventory;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/inventory/Inventory;getMaxCount(Lnet/minecraft/item/ItemStack;)I"
            )
    )
    private static int getMaxCount(
            @NotNull Inventory instance, @NotNull ItemStack stack, @NotNull Operation<Integer> original
    ) {
        if (ServerSettings.STACK_SHULKER_BOXES.get().equals(ShulkerStackMode.NEVER)) return original.call(instance, stack);
        if (!ServerSettings.STACK_SHULKER_BOXES.get().equals(ShulkerStackMode.ALWAYS) && stack.isIn(ItemTags.SHULKER_BOXES)) return Math.min(instance.getMaxCountPerStack(), stack.getItem().getMaxCount());
        return original.call(instance, stack);
    }
}
