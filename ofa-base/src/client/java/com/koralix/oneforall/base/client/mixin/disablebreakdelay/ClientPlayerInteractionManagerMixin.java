package com.koralix.oneforall.base.client.mixin.disablebreakdelay;

import com.koralix.oneforall.base.client.settings.ClientSettings;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow private int blockBreakingCooldown;

    @Redirect(method = "updateBlockBreakingProgress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", opcode = Opcodes.PUTFIELD, ordinal = 2))
    private void survivalDisableBreakDelay(ClientPlayerInteractionManager instance, int value) {
        if (ClientSettings.DISABLE_BREAK_DELAY.get().onSurvival()) {
            blockBreakingCooldown = 0;
        } else {
            blockBreakingCooldown = value;
        }
    }

    @Redirect(method = "updateBlockBreakingProgress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", opcode = Opcodes.PUTFIELD, ordinal = 1))
    private void creativeDisableBreakDelay(ClientPlayerInteractionManager instance, int value) {
        if (ClientSettings.DISABLE_BREAK_DELAY.get().onCreative()) {
            blockBreakingCooldown = 0;
        } else {
            blockBreakingCooldown = value;
        }
    }

    @Redirect(method = "attackBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", opcode = Opcodes.PUTFIELD))
    private void creativeBreakDelayChange2(ClientPlayerInteractionManager instance, int value) {
        if (ClientSettings.DISABLE_BREAK_DELAY.get().onCreative()) {
            blockBreakingCooldown = 0;
        } else {
            blockBreakingCooldown = value;
        }
    }
}
