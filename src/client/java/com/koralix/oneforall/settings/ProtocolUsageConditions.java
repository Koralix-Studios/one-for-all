package com.koralix.oneforall.settings;

import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum ProtocolUsageConditions implements StringIdentifiable {
    Always,
    OnlyEnforced,
    Never;

    @Contract(pure = true)
    @Override
    public @NotNull String asString() {
        return name();
    }
}
