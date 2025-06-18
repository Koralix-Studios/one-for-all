package com.koralix.oneforall.base.parser.computable;

import com.koralix.oneforall.base.utils.CustomEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.stat.Stat;
import net.minecraft.world.World;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ComputeUnit {
    private static final Set<SubscriptorComputableNode> tickListeners = new HashSet<>();
    private static final Map<Stat<?>, Set<SubscriptorComputableNode>> statListeners = new HashMap<>();

    private static final Map<SubscriptorComputableNode, Set<SubscriptorComputableNode>> reverseMap = new HashMap<>();

    private static final DeferredMacro MINED = DeferredMacro.parse(() -> Registries.BLOCK.streamKeys()
            .map(RegistryKey::getValue)
            .map(id -> "minecraft.mined:" + id.getNamespace() + "." + id.getPath())
            .collect(Collectors.joining("+")));

    public static final DeferredMacro TICK = DeferredMacro.of(() -> new SubscriptorComputableNode(ComputeUnit::registerOnTick));


    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (SubscriptorComputableNode node : tickListeners) {
                node.set(BigDecimal.valueOf(server.getWorld(World.OVERWORLD).getTime()));
            }
        });

        CustomEvents.ON_STAT_EVENT.register((handler, player, stat, value) -> {
            Set<SubscriptorComputableNode> set = statListeners.get(stat);

            if (set == null) return;

            for (SubscriptorComputableNode node : set) {
                node.set(BigDecimal.valueOf(value), player.getUuid());
            }
        });
    }


    private ComputeUnit() {}

    public static void registerOnTick(SubscriptorComputableNode node) {
        tickListeners.add(node);
        reverseMap.put(node, tickListeners);
    }

    public static void registerOnStat(Stat<?> stat, SubscriptorComputableNode node) {
        Set<SubscriptorComputableNode> set = statListeners.computeIfAbsent(stat, key -> new HashSet<>());
        set.add(node);

        reverseMap.put(node, set);
    }

    public static void unregister(SubscriptorComputableNode node) {
        reverseMap.computeIfAbsent(node, k -> Set.of()).remove(node);
    }

    public static Optional<MacroRootComputableNode> macro(String id) {
        return switch (id) {
            case "mined" -> Optional.of(MINED.get());
            case "tick" -> Optional.of(TICK.get());
            default -> Optional.empty();
        };
    }
}
