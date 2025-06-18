package com.koralix.oneforall.base.mixin.statscore;

import com.koralix.oneforall.base.utils.CustomEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatHandler.class)
public class StatHandlerMixin {
    @Inject(method = "setStat", at = @At("RETURN"))
    private void onSetStat(PlayerEntity player, Stat<?> stat, int value, CallbackInfo ci) {
        CustomEvents.ON_STAT_EVENT.invoker().onStatEvent((StatHandler) (Object) this, player, stat, value);
    }
}
