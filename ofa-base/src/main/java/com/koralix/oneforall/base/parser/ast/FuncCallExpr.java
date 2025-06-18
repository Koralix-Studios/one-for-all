package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.Expr2Node;
import com.koralix.oneforall.base.parser.computable.FuncCallScoreNode;
import com.koralix.oneforall.base.parser.computable.ScoreNode;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull ScoreNode toScoreNode(@NotNull ScoreNode parent) {
        return new FuncCallScoreNode(
                parent,
                name,
                arguments
                        .stream()
                        .map(expr -> new Expr2Node(expr::toScoreNode))
                        .toArray(Expr2Node[]::new)
        );
    }
}
