package com.koralix.oneforall.base.parser.computable;

import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class StatScoreNode extends ChainScoreNode {
    private final Stat<?> stat;

    public StatScoreNode(@NotNull ScoreNode parent, @NotNull Stat<?> stat) {
        super(parent);
        this.stat = stat;
    }

    @Override
    protected @NotNull BigDecimal compute(@NotNull StatHandler handler) {
        return BigDecimal.valueOf(handler.getStat(stat));
    }

    @Override
    public void attach() {
        ComputeUnit.attach(stat, this);
    }

    @Override
    public void detach() {
        ComputeUnit.detach(this);
    }
}
