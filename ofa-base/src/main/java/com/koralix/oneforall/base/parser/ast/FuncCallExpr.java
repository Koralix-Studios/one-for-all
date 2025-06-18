package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.computable.ComputableNode;
import com.koralix.oneforall.base.parser.computable.FuncCallComputableNode;

import java.util.List;

public class FuncCallExpr implements Expression {
    private String name;
    private List<Expression> arguments;

    public FuncCallExpr(String name, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public void setArguments(List<Expression> arguments) {
        this.arguments = arguments;
    }

    @Override
    public ComputableNode toComputable() {
        return new FuncCallComputableNode(name, arguments.stream().map(Expression::toComputable).toArray(ComputableNode[]::new));
    }
}
