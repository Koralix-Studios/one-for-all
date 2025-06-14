package com.koralix.oneforall.base.mixin.stackshulkerboxes;

import com.koralix.oneforall.base.settings.ServerSettings;
import com.koralix.oneforall.base.settings.ShulkerStackMode;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract boolean isIn(TagKey<Item> tag);

    @Inject(method = "getMaxCount", at = @At("HEAD"), cancellable = true)
    private void getMaxCount(CallbackInfoReturnable<Integer> cir) {
        if (ServerSettings.STACK_SHULKER_BOXES.value().equals(ShulkerStackMode.NEVER) || !this.isIn(ItemTags.SHULKER_BOXES)) return;
        cir.setReturnValue(64);
    }
}
