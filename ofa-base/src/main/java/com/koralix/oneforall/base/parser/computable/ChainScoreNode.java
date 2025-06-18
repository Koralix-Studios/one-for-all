package com.koralix.oneforall.base.parser.computable;

import com.koralix.oneforall.base.parser.Expr2Node;
import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ChainScoreNode extends CacheScoreNode {
    private final @NotNull ScoreNode parent;

    protected ChainScoreNode(@NotNull ScoreNode parent) {
        super();
        this.parent = parent;
    }

    protected ChainScoreNode(@NotNull ScoreNode parent, boolean isCacheEnabled) {
        super(isCacheEnabled);
        this.parent = parent;
    }

    protected ChainScoreNode(@NotNull ScoreNode parent, @NotNull Expr2Node @NotNull... dependencies) {
        super(dependencies);
        this.parent = parent;
    }

    @Override
    public void reset(@Nullable StatHandler handler) {
        super.reset(handler);
        this.parent.reset(handler);
    }
}
