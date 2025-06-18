package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.OpType;
import com.koralix.oneforall.base.parser.computable.ComputableNode;
import com.koralix.oneforall.base.parser.computable.UnaryComputableNode;

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
    public ComputableNode toComputable() {
        Function<BigDecimal, BigDecimal> function = child -> switch (operator) {
            case Substraction -> child.negate();
            default -> throw new IllegalStateException("Unary expression only supports substraction.");
        };

        return new UnaryComputableNode(expr.toComputable(), function);
    }
}
