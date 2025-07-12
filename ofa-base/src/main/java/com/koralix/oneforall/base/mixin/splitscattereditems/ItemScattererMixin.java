package com.koralix.oneforall.base.mixin.splitscattereditems;

import com.koralix.oneforall.base.settings.ServerSettings;
import com.koralix.oneforall.entry.OneForAll;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemScatterer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemScatterer.class)
public class ItemScattererMixin {
    @Redirect(
            method = "spawn(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;split(I)Lnet/minecraft/item/ItemStack;"
            )
    )
    private static ItemStack split(ItemStack stack, int count) {
        if (ServerSettings.SPLIT_SCATTERED_ITEMS.get()) return stack.split(count);
        return stack.split(stack.getCount());
    }
}
