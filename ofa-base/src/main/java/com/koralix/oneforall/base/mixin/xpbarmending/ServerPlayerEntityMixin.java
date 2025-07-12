package com.koralix.oneforall.base.mixin.xpbarmending;

import com.koralix.oneforall.base.settings.PlayerSettings;
import com.koralix.oneforall.base.settings.ServerSettings;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Unique
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (isActive(player)) {
            Optional<EnchantmentEffectContext> entry = EnchantmentHelper.chooseEquipmentWith(EnchantmentEffectComponentTypes.REPAIR_WITH_XP, player, ItemStack::isDamaged);
            if (entry.isEmpty()) return;

            ItemStack itemStack = entry.get().stack();
            int i = Math.min(player.totalExperience * 2, itemStack.getDamage());
            player.addExperience(-(int) Math.ceil(i / 2D));
            itemStack.setDamage(itemStack.getDamage() - i);
        }
    }

    @Unique
    private static boolean isActive(@NotNull PlayerEntity player) {
        return ServerSettings.XP_BAR_MENDING.get() && (
                player instanceof ServerPlayerEntity server
                        ? PlayerSettings.XP_BAR_MENDING.get(server).isActive(player)
                        : PlayerSettings.XP_BAR_MENDING.nominalValue().isActive(player)
        );
    }
}
