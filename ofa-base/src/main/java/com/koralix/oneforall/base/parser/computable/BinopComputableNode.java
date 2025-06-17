package com.koralix.oneforall.base.parser.computable;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;

public class BinopComputableNode extends ComputableNode {
    private final ComputableNode lhs;
    private final ComputableNode rhs;
    private final BiFunction<BigDecimal, BigDecimal, BigDecimal> function;

    public BinopComputableNode(ComputableNode lhs, ComputableNode rhs, BiFunction<BigDecimal, BigDecimal, BigDecimal> function) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.function = function;

        this.lhs.parent(this);
        this.rhs.parent(this);
    }

    @Override
    public BigDecimal execute(@NotNull UUID uuid) {
        if (this.cached != null) return this.cached;

        return this.cached = function.apply(lhs.execute(uuid), rhs.execute(uuid));
    }

    @Override
    public void subscribe() {
        this.lhs.subscribe();
        this.rhs.subscribe();
    }

    @Override
    public void unsubscribe() {
        this.lhs.unsubscribe();
        this.rhs.unsubscribe();
    }
}
