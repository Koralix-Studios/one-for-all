package com.koralix.oneforall.mixin.client.nousefireworks;

import com.koralix.oneforall.duck.FireworkGetter;
import com.koralix.oneforall.settings.ClientSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin implements FireworkGetter {
    @Unique
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Unique
    private FireworkRocketEntity firework = null;

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void interactItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (ClientSettings.NO_USE_FIREWORKS.value() && player.getStackInHand(hand).getItem() instanceof FireworkRocketItem) {
            if (!player.isFallFlying() || mc.currentScreen != null) return;

            FireworkRocketEntity entity = new FireworkRocketEntity(player.getWorld(), player.getStackInHand(hand), player);
            mc.world.addEntity(entity.getId(), entity);
            mc.world.playSoundFromEntity(mc.player, entity, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);

            if (firework != null) {
                firework.discard();
            }
            firework = entity;

            cir.setReturnValue(ActionResult.PASS);
        }
    }

    @Override
    @Unique
    public boolean isActiveFirework(FireworkRocketEntity firework) {
        return firework == this.firework;
    }
}
