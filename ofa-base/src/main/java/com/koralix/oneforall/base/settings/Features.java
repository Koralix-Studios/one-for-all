package com.koralix.oneforall.base.settings;

import com.koralix.oneforall.config.MonoConfigValue;
import com.koralix.oneforall.config.PlayerConfigValue;
import com.koralix.oneforall.config.feature.FeatureRegistrar;
import com.koralix.oneforall.config.feature.FeatureRegistry;
import com.koralix.oneforall.config.feature.GatedFeature;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Features {
    private static final FeatureRegistrar REGISTRAR = FeatureRegistry.registrar();

    public static final GatedFeature<SneakMode, Boolean> CAREFUL_BREAK = sneak(
            "careful_break",
            PlayerSettings.CAREFUL_BREAK,
            ServerSettings.CAREFUL_BREAK
    );

    public static final GatedFeature<SneakMode, Boolean> XP_BAR_MENDING = sneak(
            "xp_bar_mending",
            PlayerSettings.XP_BAR_MENDING,
            ServerSettings.XP_BAR_MENDING
    );

    public static final GatedFeature<SneakMode, Boolean> CREATIVE_KILL = sneak(
            "creative_kill",
            PlayerSettings.CREATIVE_KILL,
            ServerSettings.CREATIVE_KILL
    );

    @Contract(value = "_, _, _ -> new", pure = true)
    private static @NotNull GatedFeature<SneakMode, Boolean> sneak(
            @NotNull String id,
            @NotNull PlayerConfigValue<SneakMode, ?> configValue,
            @NotNull MonoConfigValue<Boolean, ?, ?> gateValue
    ) {
        return REGISTRAR.register(id, new GatedFeature<>(
                id,
                configValue,
                gateValue,
                (sneakMode, gate) -> gate ? sneakMode : SneakMode.NEVER
        ));
    }

    public static void register() {
        REGISTRAR.complete();
    }
}
