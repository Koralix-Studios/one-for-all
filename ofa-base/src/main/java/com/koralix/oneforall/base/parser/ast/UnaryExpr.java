package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.Expr2Node;
import com.koralix.oneforall.base.parser.OpType;
import com.koralix.oneforall.base.parser.computable.ScoreNode;
import com.koralix.oneforall.base.parser.computable.UnaryOpScoreNode;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.function.Function;

public class UnaryExpr implements Expression {
    private Expression expr;
    private OpType operator;

    public UnaryExpr(Expression expr, OpType operator) {
        this.expr = expr;
        this.operator = operator;
    }

    public Expression getExpr() {
        return expr;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }

    public OpType getOperator() {
        return operator;
    }

    public void setOperator(OpType operator) {
        this.operator = operator;
    }

    @Override
    public @NotNull ScoreNode toScoreNode(@NotNull ScoreNode parent) {
        Function<BigDecimal, BigDecimal> function = child -> switch (operator) {
            case Substraction -> child.negate();
            default -> throw new IllegalStateException("Unary expression only supports substraction.");
        };

        return new UnaryOpScoreNode(parent, new Expr2Node(expr::toScoreNode), function);
    }
}
