package com.koralix.oneforall.base.client.utils;

import com.koralix.oneforall.base.client.settings.ClientSettings;
import net.minecraft.item.ItemStack;

public class PreventBreakingToolsHelper {
    public static boolean preventDamagingTool(ItemStack itemStack) {
        if (itemStack == null || !itemStack.isDamageable()) return false;

        return itemStack.getMaxDamage() - itemStack.getDamage() <= ClientSettings.PREVENT_BREAKING_TOOLS.get();
    }
}
