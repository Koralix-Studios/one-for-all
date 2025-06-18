package com.koralix.oneforall.base.parser.computable;

import com.koralix.oneforall.base.parser.Lexer;
import com.koralix.oneforall.base.parser.ParseException;
import com.koralix.oneforall.base.parser.Parser;

import java.util.function.Function;
import java.util.function.Supplier;

public class DeferredMacro implements Supplier<MacroRootScoreNode> {
    private final Function<ScoreNode, ScoreNode> factory;
    private MacroRootScoreNode root;

    private DeferredMacro(Function<ScoreNode, ScoreNode> factory) {
        this.factory = factory;
    }

    public static DeferredMacro parse(Supplier<String> supplier) {
        return new DeferredMacro(node -> {
            Parser parser = new Parser(new Lexer(supplier.get()));

            try {
                return parser.parse().toScoreNode(node);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static DeferredMacro of(Function<ScoreNode, ScoreNode> factory) {
        return new DeferredMacro(factory);
    }

    @Override
    public MacroRootScoreNode get() {
        if (this.root != null) return this.root;

        return this.root = new MacroRootScoreNode(this.factory);
    }
}
