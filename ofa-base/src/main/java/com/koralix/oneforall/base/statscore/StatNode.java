package com.koralix.oneforall.base.statscore;

import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public interface StatNode {
    Text title();
    void init(@NotNull ScoreHolder holder, @NotNull StatHandler handler, @NotNull ScoreboardObjective objective);
    ScoreboardCriterion criterion();
}
