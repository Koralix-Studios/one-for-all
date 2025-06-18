package com.koralix.oneforall.base.parser.computable;

import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class ConstantScoreNode implements ScoreNode {
    private final BigDecimal score;

    public ConstantScoreNode(BigDecimal score) {
        this.score = score;
    }

    @Override
    public @NotNull BigDecimal execute(@NotNull StatHandler handler) {
        return score;
    }

    @Override
    public void attach() {

    }

    @Override
    public void detach() {

    }
}
