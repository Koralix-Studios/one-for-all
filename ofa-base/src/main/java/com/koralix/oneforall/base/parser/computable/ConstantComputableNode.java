package com.koralix.oneforall.base.parser.computable;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class ConstantComputableNode extends ComputableNode {
    public ConstantComputableNode(BigDecimal constant) {
        this.cached = constant;
    }

    @Override
    public BigDecimal execute(@NotNull UUID uuid) {
        return this.cached;
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
    }

    @Override
    public void reset() {
        // Intentionally empty to avoid cache loss
    }
}
