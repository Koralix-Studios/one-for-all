package com.koralix.oneforall.base.statscore;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LargeSum implements DerivedNode {
    private final Text title;
    private final Set<Stat<?>> stats;
    private final Map<String, Object2IntMap<Stat<?>>> parts = new HashMap<>();
    private final Object2IntMap<String> totals = new Object2IntOpenHashMap<>();

    public LargeSum(Text title, Set<Stat<?>> stats) {
        this.title = title;
        this.stats = stats;
    }

    @Override
    public Text title() {
        return this.title;
    }

    @Override
    public void init(@NotNull ScoreHolder holder, @NotNull StatHandler handler, @NotNull ScoreboardObjective objective) {
        int total = 0;

        for (Stat<?> stat : stats) {
            int value = handler.getStat(stat);
            total += value;
            parts.computeIfAbsent(holder.getNameForScoreboard(), k -> new Object2IntOpenHashMap<>()).put(stat, value);
        }

        ScoreAccess access = objective.getScoreboard().getOrCreateScore(holder, objective);
        String key = holder.getNameForScoreboard();
        totals.put(key, total);
        access.setScore(total);
    }

    @Override
    public ScoreboardCriterion criterion() {
        return ScoreboardCriterion.DUMMY;
    }

    @Override
    public void refresh(
            @NotNull ScoreboardObjective objective,
            @NotNull ScoreHolder holder,
            @NotNull StatHandler handler,
            @NotNull Stat<?> stat
    ) {
        if (!stats.contains(stat)) return;

        ScoreAccess access = objective.getScoreboard().getOrCreateScore(holder, objective);
        String key = holder.getNameForScoreboard();

        int pre = parts.getOrDefault(key, Object2IntMaps.emptyMap()).getOrDefault(stat, 0);
        int value = handler.getStat(stat);
        int total = totals.getInt(key);
        total += value - pre;
        totals.put(key, total);
        parts.computeIfAbsent(key, k -> new Object2IntOpenHashMap<>()).put(stat, value);

        access.setScore(total);
    }
}
