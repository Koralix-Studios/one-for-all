package com.koralix.oneforall.base.statscore;

import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DirectNode implements StatNode {
    private final Stat<?> stat;
    private final Text title;

    public DirectNode(@NotNull Stat<?> stat, @NotNull Text title) {
        this.stat = stat;
        this.title = title;
    }

    public static @NotNull Text createTitle(@NotNull String input) {
        Identifier statId = Identifier.of(input);

        String typeName = statId.getNamespace().replace("minecraft.", "").replace("_", " ");
        String statName = statId.getPath().replace("minecraft.", "").replace("_", " ");

        typeName = Arrays.stream(typeName.split(" ")).map(word -> word.substring(0, 1).toUpperCase() + word.substring(1)).collect(Collectors.joining(" "));
        statName = Arrays.stream(statName.split(" ")).map(word -> word.substring(0, 1).toUpperCase() + word.substring(1)).collect(Collectors.joining(" "));

        MutableText modeText = Text.literal(typeName);
        modeText.setStyle(modeText.getStyle().withBold(true).withColor(TextColor.fromRgb(13848661)));
        MutableText statText = Text.literal(statName);
        return Text.empty().append(modeText).append(": ").append(statText);
    }

    @Override
    public Text title() {
        return title;
    }

    @Override
    public void init(@NotNull ScoreHolder holder, @NotNull StatHandler handler, @NotNull ScoreboardObjective objective) {
        objective.getScoreboard().getOrCreateScore(holder, objective).setScore(handler.getStat(stat));
    }

    @Override
    public ScoreboardCriterion criterion() {
        return stat;
    }
}
