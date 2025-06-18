package com.koralix.oneforall.base.parser.computable;

import com.koralix.oneforall.base.parser.Expr2Node;
import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class RootScoreNode implements ScoreNode {
    private final ScoreNode child;
    private boolean dirty = true;

    public RootScoreNode(@NotNull Expr2Node child) {
        this.child = child.get(this);
    }

    @Override
    public @NotNull BigDecimal execute(@NotNull StatHandler handler) {
        this.dirty = false;
        return child.execute(handler);
    }

    @Override
    public void reset(@Nullable StatHandler handler) {
        ScoreNode.super.reset(handler);
        dirty = true;
    }

    @Override
    public void attach() {
        child.attach();
    }

    @Override
    public void detach() {
        child.detach();
    }

    public boolean isDirty() {
        return dirty;
    }
}
