package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.Expr2Node;
import com.koralix.oneforall.base.parser.OpType;
import com.koralix.oneforall.base.parser.computable.BinOpScoreNode;
import com.koralix.oneforall.base.parser.computable.ScoreNode;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiFunction;

public class BinopExpr implements Expression {
    private Expression lhs;
    private Expression rhs;
    private OpType opType;


    public BinopExpr(Expression lhs, Expression rhs, OpType opType) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.opType = opType;
    }

    public Expression getLhs() {
        return lhs;
    }

    public void setLhs(Expression lhs) {
        this.lhs = lhs;
    }

    public Expression getRhs() {
        return rhs;
    }

    public void setRhs(Expression rhs) {
        this.rhs = rhs;
    }

    public OpType getOpType() {
        return opType;
    }

    public void setOpType(OpType opType) {
        this.opType = opType;
    }

    @Override
    public @NotNull ScoreNode toScoreNode(@NotNull ScoreNode parent) {

        BiFunction<BigDecimal, BigDecimal, BigDecimal> function = (lhs1, rhs1) -> switch (opType) {
            case Multiplication -> lhs1.multiply(rhs1);
            case Division -> lhs1.divide(rhs1, 64, RoundingMode.HALF_EVEN);
            case Addition -> lhs1.add(rhs1);
            case Substraction -> lhs1.subtract(rhs1);
        };

        return new BinOpScoreNode(parent, new Expr2Node(lhs::toScoreNode), new Expr2Node(rhs::toScoreNode), function);
    }
}
