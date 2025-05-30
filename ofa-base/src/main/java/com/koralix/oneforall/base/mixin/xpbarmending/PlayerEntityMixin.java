package com.koralix.oneforall.base.mixin.xpbarmending;

import com.koralix.oneforall.base.settings.PlayerSettings;
import com.koralix.oneforall.base.settings.ServerSettings;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Unique
    private final PlayerEntity player = (PlayerEntity) (Object) this;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (PlayerSettings.XP_BAR_MENDING.value(player.getUuid()).isActive(player, ServerSettings.XP_BAR_MENDING)) {
            Optional<EnchantmentEffectContext> entry = EnchantmentHelper.chooseEquipmentWith(EnchantmentEffectComponentTypes.REPAIR_WITH_XP, player, ItemStack::isDamaged);
            if (entry.isEmpty()) return;

            ItemStack itemStack = entry.get().stack();
            int i = Math.min(player.totalExperience * 2, itemStack.getDamage());
            player.addExperience(-(int)Math.ceil(i / 2D));
            itemStack.setDamage(itemStack.getDamage() - i);
        }
    }
}
