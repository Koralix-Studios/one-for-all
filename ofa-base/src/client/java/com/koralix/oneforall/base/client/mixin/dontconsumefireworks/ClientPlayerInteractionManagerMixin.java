package com.koralix.oneforall.base.client.mixin.dontconsumefireworks;

import com.koralix.oneforall.base.client.duck.DontConsumeFireworks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
public class ClientPlayerInteractionManagerMixin {
    @Unique
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void interactItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        // FIXME(ArtikGz): Add config check
        if (false) return;

        FireworksComponent fireworksComponent = player.getStackInHand(hand).get(DataComponentTypes.FIREWORKS);

        if (!player.isGliding() || mc.currentScreen != null || fireworksComponent == null) return;

        ItemStack itemStack = Items.FIREWORK_ROCKET.getDefaultStack();
        itemStack.set(DataComponentTypes.FIREWORKS, fireworksComponent);

        FireworkRocketEntity entity = new FireworkRocketEntity(player.getWorld(), itemStack, player);
        mc.world.addEntity(entity);
        mc.world.playSoundFromEntity(mc.player, entity, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);

        ((DontConsumeFireworks) entity).dontConsumeFirework();

        cir.setReturnValue(ActionResult.PASS);
    }

}
