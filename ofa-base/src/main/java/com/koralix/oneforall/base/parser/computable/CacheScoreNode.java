package com.koralix.oneforall.base.parser.computable;

import com.koralix.oneforall.base.parser.Expr2Node;
import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class CacheScoreNode implements ScoreNode {
    private final Map<StatHandler, BigDecimal> cache = new HashMap<>();
    protected final boolean isCacheEnabled;
    protected boolean forceCache = false;

    protected CacheScoreNode(boolean isCacheEnabled) {
        this.isCacheEnabled = isCacheEnabled;
    }

    protected CacheScoreNode(@NotNull Function<ScoreNode, Boolean> isCacheEnabledFunction) {
        this.isCacheEnabled = isCacheEnabledFunction.apply(this);
    }

    protected CacheScoreNode() {
        this(true);
    }

    protected CacheScoreNode(@NotNull Expr2Node @NotNull... dependencies) {
        this(node -> isCacheEnabled(node, dependencies));
    }

    @Override
    public final @NotNull BigDecimal execute(@NotNull StatHandler handler) {
        if (isCacheEnabled || forceCache) return cache.computeIfAbsent(handler, this::compute);
        return compute(handler);
    }

    protected abstract @NotNull BigDecimal compute(@NotNull StatHandler handler);

    @Override
    public void reset(@Nullable StatHandler handler) {
        if (handler == null) {
            cache.clear();
            ScoreNode.super.reset(null);
            return;
        }
        if ((isCacheEnabled || forceCache) && !cache.containsKey(handler)) return;
        ScoreNode.super.reset(handler);
        if (isCacheEnabled || forceCache) cache.remove(handler);
    }

    protected static boolean isCacheEnabled(@NotNull ScoreNode node) {
        return !(node instanceof CacheScoreNode cache) || cache.isCacheEnabled;
    }

    @Contract(pure = true)
    protected static boolean isCacheEnabled(@NotNull ScoreNode node, @NotNull Expr2Node @NotNull... nodes) {
        for (Expr2Node expr : nodes) {
            if (!isCacheEnabled(expr.get(node))) return false;
        }
        return true;
    }
}
