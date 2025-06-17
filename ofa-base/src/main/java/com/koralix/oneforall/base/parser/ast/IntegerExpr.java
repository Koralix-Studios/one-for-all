package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.computable.ComputableNode;
import com.koralix.oneforall.base.parser.computable.ConstantComputableNode;

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
    public ComputableNode toComputable() {
        return new ConstantComputableNode(BigDecimal.valueOf(integer));
    }
}
