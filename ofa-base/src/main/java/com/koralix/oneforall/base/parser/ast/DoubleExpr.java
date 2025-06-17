package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.computable.ComputableNode;
import com.koralix.oneforall.base.parser.computable.ConstantComputableNode;

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
    public ComputableNode toComputable() {
        return new ConstantComputableNode(BigDecimal.valueOf(value));
    }
}
