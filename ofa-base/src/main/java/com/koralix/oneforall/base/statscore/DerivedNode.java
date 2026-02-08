package com.koralix.oneforall.base.statscore;

import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.NotNull;

public interface DerivedNode extends StatNode {
    void refresh(
            @NotNull ScoreboardObjective objective,
            @NotNull ScoreHolder holder,
            @NotNull StatHandler handler,
            @NotNull Stat<?> stat
    );
}
