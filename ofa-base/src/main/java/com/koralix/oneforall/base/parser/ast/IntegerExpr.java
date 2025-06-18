package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.computable.ConstantScoreNode;
import com.koralix.oneforall.base.parser.computable.ScoreNode;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class IntegerExpr implements Expression {
    public int integer;

    public IntegerExpr(int integer) {
        this.integer = integer;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    @Override
    public @NotNull ScoreNode toScoreNode(@NotNull ScoreNode parent) {
        return new ConstantScoreNode(BigDecimal.valueOf(integer));
    }
}
