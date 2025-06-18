package com.koralix.oneforall.base.parser.computable;

import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class MacroRootScoreNode implements ScoreNode {
    private final ScoreNode child;
    private final Set<MacroScoreNode> listeners = new HashSet<>();
    private int refCount;

    public MacroRootScoreNode(@NotNull Function<ScoreNode, ScoreNode> factory) {
        this.child = factory.apply(this);
        this.refCount = 0;
    }

    @Override
    public @NotNull BigDecimal execute(@NotNull StatHandler handler) {
        return this.child.execute(handler);
    }

    @Override
    public void reset(@Nullable StatHandler handler) {
        ScoreNode.super.reset(handler);
        for (MacroScoreNode listener : listeners) {
            listener.reset(handler);
        }
    }

    @Override
    public void attach() {
        if (refCount++ == 0) child.attach();
    }

    @Override
    public void detach() {
        if (refCount == 0) {
            throw new IllegalStateException("Cannot detach a node that is not attached.");
        }
        if (--refCount == 0) child.detach();
    }

    public void attach(@NotNull MacroScoreNode listener) {
        listeners.add(listener);
    }

    public void detach(@NotNull MacroScoreNode listener) {
        listeners.remove(listener);
    }
}
