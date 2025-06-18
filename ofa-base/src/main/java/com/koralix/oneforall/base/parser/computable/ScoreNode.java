package com.koralix.oneforall.base.parser.computable;

import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public interface ScoreNode {
    @NotNull BigDecimal execute(@NotNull StatHandler handler);
    default void reset(@Nullable StatHandler handler) {}
    void attach();
    void detach();
}
