package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.computable.ConstantScoreNode;
import com.koralix.oneforall.base.parser.computable.ScoreNode;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class DoubleExpr implements Expression {
    private double value;

    public DoubleExpr(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public @NotNull ScoreNode toScoreNode(@NotNull ScoreNode parent) {
        return new ConstantScoreNode(BigDecimal.valueOf(value));
    }
}
