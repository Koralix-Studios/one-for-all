package com.koralix.oneforall.base.client.mixin.flyinertia;

import com.koralix.oneforall.base.client.settings.ClientSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private final @NotNull LivingEntity self = (LivingEntity) (Object) this;

    @ModifyConstant(method = "travelMidAir", constant = @Constant(floatValue = 0.91f))
    private float injected(float value) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || player == self) return value;

        Input input = player.input;

        if (ClientSettings.FLY_INERTIA.value() || input.playerInput.jump() || input.playerInput.sneak() ||
                player.forwardSpeed != 0 || player.sidewaysSpeed != 0 || !player.getAbilities().flying) {
            return value;
        }

        return 0;
    }
}
