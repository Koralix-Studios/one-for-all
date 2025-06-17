package com.koralix.oneforall.base.parser.computable;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

public class MacroRootComputableNode extends ComputableNode {
    private final ComputableNode child;
    private final Set<ComputableNode> listeners = new HashSet<>();
    private int refCount;

    public MacroRootComputableNode(ComputableNode child) {
        this.child = child;
        this.refCount = 0;
    }

    @Override
    public BigDecimal execute(@NotNull UUID uuid) {
        return this.child.execute(uuid);
    }

    @Override
    public void subscribe() {
        if (refCount++ != 0) return;

        child.subscribe();
    }

    @Override
    public void unsubscribe() {
        if (--refCount != 0) return;

        child.unsubscribe();
    }

    public void register(ComputableNode node) {
        if (!(node instanceof MacroComputableNode)) throw new IllegalArgumentException("Only MacroComputableNode allowed.");
        if (!this.listeners.add(node)) throw new IllegalStateException("ComputableNode already registered.");
    }

    public void unregister(ComputableNode node) {
        if (!(node instanceof MacroComputableNode)) throw new IllegalArgumentException("Only MacroComputableNode allowed.");
        if (!this.listeners.remove(node)) throw new NoSuchElementException();
    }

    @Override
    public void reset() {
        super.reset();

        this.listeners.forEach(ComputableNode::reset);
    }
}
