package com.koralix.oneforall.base.parser.computable;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Function;

public class UnaryComputableNode extends ComputableNode {
    private final ComputableNode child;
    private final Function<BigDecimal, BigDecimal> function;

    public UnaryComputableNode(ComputableNode child, Function<BigDecimal, BigDecimal> function) {
        this.child = child;
        this.function = function;
    }

    @Override
    public BigDecimal execute(@NotNull UUID uuid) {
        if (this.cached != null) return this.cached;

        return this.cached = function.apply(child.execute(uuid));
    }

    @Override
    public void subscribe() {
        child.subscribe();
    }

    @Override
    public void unsubscribe() {
        child.unsubscribe();
    }
}
