package com.koralix.oneforall.base.statscore;

import com.koralix.oneforall.base.BaseInit;
import com.koralix.oneforall.base.duck.OpenUserCache;
import com.koralix.oneforall.base.duck.StatHandlerGetter;
import com.koralix.oneforall.base.utils.CustomEvents;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StatScore {
    public static final DerivedNode MINED = mined();
    public static final DerivedNode PICKAXE = pickaxe();
    private static ScoreboardObjective objective;
    private static StatNode active;

    private StatScore() {
        // Prevent instantiation
    }

    public static StatNode parse(@NotNull String input) {
        return switch (input) {
            case "#mined" -> MINED;
            case "#pickaxe" -> PICKAXE;
            default -> {
                if (check(input)) {
                    Stat<?> stat = (Stat<?>) ScoreboardCriterion.getOrCreateStatCriterion(input).orElseThrow();
                    yield new DirectNode(stat, DirectNode.createTitle(input));
                } else {
                    throw new IllegalArgumentException("Unknown stat: " + input);
                }
            }
        };
    }

    private static boolean check(@NotNull String input) {
        return Registries.STAT_TYPE.stream()
                .flatMap(StatScore::names)
                .anyMatch(stat -> stat.equals(input));
    }

    private static <T> @NotNull Stream<String> names(@NotNull StatType<T> type) {
        return type.getRegistry().stream()
                .map(entry -> Stat.getName(type, entry));
    }

    private static @NotNull DerivedNode mined() {
        LargeSum node = new LargeSum(
                Text.translatable("statscore." + BaseInit.id() + ".mined"),
                Stats.MINED.getRegistry().stream()
                        .map(Stats.MINED::getOrCreateStat)
                        .collect(Collectors.toSet())
        );

        CustomEvents.ON_STAT_EVENT.register((handler, player, stat, value) -> {
            if (MINED == active) MINED.refresh(objective, player, handler, stat);
        });

        return node;
    }

    private static @NotNull DerivedNode pickaxe() {
        LargeSum node = new LargeSum(
                Text.translatable("statscore." + BaseInit.id() + ".pickaxe"),
                Stats.USED.getRegistry().stream()
                        .filter(item -> item.getTranslationKey().contains("pickaxe"))
                        .map(Stats.USED::getOrCreateStat)
                        .collect(Collectors.toSet())
        );

        CustomEvents.ON_STAT_EVENT.register((handler, player, stat, value) -> {
            if (PICKAXE == active) PICKAXE.refresh(objective, player, handler, stat);
        });

        return node;
    }

    public static boolean dispose() {
        if (objective == null) return false;
        objective.getScoreboard().removeObjective(objective);
        objective = null;
        return true;
    }

    public static void create(@NotNull MinecraftServer server, @NotNull StatNode node) {
        dispose();

        Scoreboard scoreboard = server.getScoreboard();

        objective = scoreboard.getNullableObjective("statscore");
        if (objective != null) scoreboard.removeObjective(objective);
        if (objective == null) {
            objective = scoreboard.addObjective(
                    "statscore",
                    node.criterion(),
                    node.title(),
                    ScoreboardCriterion.RenderType.INTEGER,
                    false,
                    StyledNumberFormat.EMPTY
            );
        }
        scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, objective);

        init(server, node);
    }

    private static void init(@NotNull MinecraftServer server, @NotNull StatNode node) {
        active = node;
        ((OpenUserCache) server.getUserCache()).profiles()
                .map(profile -> Map.entry(ScoreHolder.fromProfile(profile), ((StatHandlerGetter) server.getPlayerManager()).get(profile.getId())))
                .forEach(entry -> node.init(entry.getKey(), entry.getValue(), objective));
    }
}
