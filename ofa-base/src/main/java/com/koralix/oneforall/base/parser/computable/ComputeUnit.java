package com.koralix.oneforall.base.parser.computable;

import com.koralix.oneforall.base.duck.OpenUserCache;
import com.koralix.oneforall.base.duck.StatHandlerGetter;
import com.koralix.oneforall.base.parser.Expr2Node;
import com.koralix.oneforall.base.parser.Lexer;
import com.koralix.oneforall.base.parser.ParseException;
import com.koralix.oneforall.base.parser.Parser;
import com.koralix.oneforall.base.utils.CustomEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComputeUnit implements CustomEvents.OnStatEvent, ServerTickEvents.EndTick {
    private static ComputeUnit instance;

    private static final DeferredMacro MINED = DeferredMacro.parse(() -> largeSum(Registries.BLOCK.streamKeys()
            .map(RegistryKey::getValue)
            .map(id -> "minecraft.mined:" + id.getNamespace() + "." + id.getPath())));

    private static String largeSum(@NotNull Stream<String> stream) {
        List<String> strings = stream.toList();
        int size = strings.size();
        int groupSize = (int) Math.min(Math.ceil(size / 10.0), 256);

        return Stream.iterate(0, i -> i < size, i -> i + groupSize)
                .map(i -> strings.subList(i, Math.min(i + groupSize, size)))
                .map(list -> "(" + String.join("+", list) + ")")
                .collect(Collectors.joining("+"));
    }

    public static final DeferredMacro TICK = DeferredMacro.of(parent -> instance.createTickNode(parent));

    private static final Map<Stat<?>, Set<StatScoreNode>> listeners = new HashMap<>();
    private static final Map<StatScoreNode, Set<StatScoreNode>> reverseIndex = new HashMap<>();
    private final MinecraftServer server;
    private ScoreboardUnit scoreboard;

    private ComputeUnit(@NotNull MinecraftServer server) {
        this.server = server;
        CustomEvents.ON_STAT_EVENT.register(this);
        ServerTickEvents.END_SERVER_TICK.register(this);
    }

    public static ComputeUnit get(@NotNull MinecraftServer server) {
        return instance == null ? instance = new ComputeUnit(server) : instance;
    }

    public static @NotNull Optional<MacroRootScoreNode> macro(@NotNull String id) {
        return switch (id) {
            case "mined" -> Optional.of(MINED.get());
            case "tick" -> Optional.of(TICK.get());
            default -> Optional.empty();
        };
    }

    public static void attach(Stat<?> stat, StatScoreNode node) {
        Set<StatScoreNode> set = listeners.computeIfAbsent(stat, k -> new HashSet<>());
        set.add(node);
        reverseIndex.put(node, set);
    }

    public static void detach(StatScoreNode node) {
        Set<StatScoreNode> set = reverseIndex.remove(node);
        if (set != null) set.remove(node);
    }

    private @NotNull TickScoreNode createTickNode(@NotNull ScoreNode parent) {
        return new TickScoreNode(parent, server.getWorld(World.OVERWORLD)::getTime);
    }

    public static @NotNull ScoreboardObjective scoreboard(@NotNull Text title, @NotNull String input) throws ParseException {
        ScoreNode node = new RootScoreNode(new Expr2Node(new Parser(new Lexer(input)).parse()::toScoreNode));
        ScoreboardObjective objective = getOrCreateScore(title);
        instance.scoreboard = new ScoreboardUnit(node, objective);
        instance.scoreboard.attach();
        ((OpenUserCache) instance.server.getUserCache()).profiles()
                .map(profile -> Map.entry(ScoreHolder.fromProfile(profile), ((StatHandlerGetter) instance.server.getPlayerManager()).get(profile.getId())))
                .forEach(entry -> instance.scoreboard.refresh(entry.getKey(), entry.getValue()));
        return objective;
    }

    public static void disposeScoreboard() {
        if (instance.scoreboard == null) return;
        ScoreboardObjective objective = instance.scoreboard.dispose();
        objective.getScoreboard().removeObjective(objective);
    }

    private static @NotNull ScoreboardObjective getOrCreateScore(Text title) {
        if (instance.scoreboard != null) {
            ScoreboardObjective objective = instance.scoreboard.dispose();
            objective.setDisplayName(title);
            return objective;
        }
        ScoreboardObjective objective = instance.server.getScoreboard().getNullableObjective("statscore");
        if (objective != null) objective.setDisplayName(title);
        return objective == null
                ? instance.server.getScoreboard().addObjective("statscore", ScoreboardCriterion.DUMMY, title, ScoreboardCriterion.RenderType.INTEGER, false, StyledNumberFormat.EMPTY)
                : objective;
    }

    @Override
    public void onStatEvent(StatHandler handler, PlayerEntity player, Stat<?> stat, int value) {
        listeners.getOrDefault(stat, Set.of()).forEach(node -> node.reset(handler));
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        if (scoreboard != null) scoreboard.update();
    }
}
