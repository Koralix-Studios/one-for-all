package com.koralix.oneforall.base.parser;

import com.koralix.oneforall.base.parser.computable.ScoreNode;

import java.util.function.Function;

public class Expr2Node {
    private final Function<ScoreNode, ScoreNode> factory;
    private ScoreNode node;

    public Expr2Node(Function<ScoreNode, ScoreNode> factory) {
        this.factory = factory;
    }

    public ScoreNode get(ScoreNode parent) {
        return node == null ? node = factory.apply(parent) : node;
    }
}
