package com.koralix.oneforall.base.parser.computable;

import com.koralix.oneforall.base.parser.Lexer;
import com.koralix.oneforall.base.parser.ParseException;
import com.koralix.oneforall.base.parser.Parser;

import java.util.function.Supplier;

public class DeferredMacro implements Supplier<MacroRootComputableNode> {
    private final Supplier<ComputableNode> supplier;
    private MacroRootComputableNode root;

    private DeferredMacro(Supplier<ComputableNode> supplier) {
        this.supplier = supplier;
    }

    public static DeferredMacro parse(Supplier<String> supplier) {
        return new DeferredMacro(() -> {
            Parser parser = new Parser(new Lexer(supplier.get()));

            try {
                return parser.parse().toComputable();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static DeferredMacro of(Supplier<ComputableNode> supplier) {
        return new DeferredMacro(supplier);
    }

    @Override
    public MacroRootComputableNode get() {
        if (this.root != null) return this.root;

        return this.root = new MacroRootComputableNode(this.supplier.get());
    }
}
