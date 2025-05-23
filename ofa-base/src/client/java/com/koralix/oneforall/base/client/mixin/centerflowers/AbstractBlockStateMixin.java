package com.koralix.oneforall.base.client.mixin.centerflowers;

import com.koralix.oneforall.base.client.ClientSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FlowerBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
    @Shadow
    public abstract Block getBlock();

    @Inject(method = "getModelOffset", at = @At("HEAD"), cancellable = true)
    public void getModelOffset(BlockPos pos, CallbackInfoReturnable<Vec3d> cir) {
        if (ClientSettings.CENTER_FLOWERS.value() && this.getBlock() instanceof FlowerBlock) {
            cir.setReturnValue(Vec3d.ZERO);
        }
    }
}
