package com.koralix.oneforall.base.parser.computable;

import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardUnit implements ScoreNode {
    private final ScoreNode root;
    private final ScoreboardObjective objective;
    private final Map<StatHandler, ScoreHolder> scoreHolders = new HashMap<>();
    private boolean isAttached = false;

    public ScoreboardUnit(ScoreNode root, ScoreboardObjective objective) {
        this.root = root;
        this.objective = objective;
    }

    @Override
    public @NotNull BigDecimal execute(@NotNull StatHandler handler) {
        return root.execute(handler);
    }

    @Override
    public void reset(@Nullable StatHandler handler) {
        ScoreNode.super.reset(handler);
        if (handler != null) refresh(scoreHolders.get(handler), handler);
    }

    @Override
    public void attach() {
        if (!isAttached) root.attach();
    }

    @Override
    public void detach() {
        if (isAttached) root.detach();
    }

    public ScoreboardObjective dispose() {
        detach();
        return objective;
    }

    public void refresh(ScoreHolder holder, StatHandler handler) {
        refresh(holder, handler, true);
    }

    private void refresh(ScoreHolder holder, StatHandler handler, boolean store) {
        ScoreAccess access = objective.getScoreboard().getOrCreateScore(holder, objective);
        try {
            BigDecimal result = execute(handler);
            access.setScore(result.intValue());
            if (store) scoreHolders.put(handler, holder);
        } catch (Exception e) {
            access.resetScore();
        }
    }

    public void update() {
        scoreHolders.forEach((handler, holder) -> refresh(holder, handler, false));
    }
}
