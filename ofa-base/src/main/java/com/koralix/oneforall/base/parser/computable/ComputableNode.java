package com.koralix.oneforall.base.parser.computable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class ComputableNode {
    protected BigDecimal cached;
    private @Nullable ComputableNode parent;

    public abstract BigDecimal execute(@NotNull UUID uuid);

    public void parent(ComputableNode parent) {
        if (this.parent != null) throw new IllegalStateException("Node already has parent.");
        this.parent = parent;
    }

    public abstract void subscribe();
    public abstract void unsubscribe();

    public void reset() {
        this.cached = null;

        if (this.parent != null) this.parent.reset();
    }
}