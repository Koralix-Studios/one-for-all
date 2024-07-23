package com.koralix.oneforall.automaton;

import it.unimi.dsi.fastutil.ints.*;

import java.util.*;

public class AutomatonMinimizer {
    private AutomatonMinimizer() {}

    public static <T, V> void minimize(Automaton<T, V> automaton) {
        Set<IntSet> classes = computeEquivalenceClasses(automaton);
        if (classes.size() == automaton.states()) return;

        Int2IntMap mapping = new Int2IntOpenHashMap();
        int i = 0;
        for (IntSet equivalenceClass : classes) {
            for (int state : equivalenceClass) {
                mapping.put(state, i);
            }
            ++i;
        }

        List<Map<T, Integer>> transitions = new ArrayList<>();
        IntSet accepting = new IntOpenHashSet();
        IntSet start = new IntOpenHashSet();
        for (IntSet equivalenceClass : classes) {
            Map<T, Integer> transition = new HashMap<>();
            for (int state : equivalenceClass) {
                for (T key : automaton.symbolsFrom(state)) {
                    for (int next : automaton.next(key, state)) {
                        transition.put(key, mapping.get(next));
                    }
                }
                if (automaton.isAccepting(state)) {
                    accepting.add(mapping.get(state));
                }
                if (automaton.isStart(state)) {
                    start.add(mapping.get(state));
                }
            }
            transitions.add(transition);
        }

        automaton.clear();
        for (Map<T, Integer> transition : transitions) {
            int q = automaton.state();
            for (Map.Entry<T, Integer> entry : transition.entrySet()) {
                automaton.transition(entry.getKey(), q, entry.getValue());
            }
            if (accepting.contains(q)) {
                automaton.accept(q);
            }
            if (start.contains(q)) {
                automaton.start(q);
            }
        }
    }

    private static <T, V> Set<IntSet> computeEquivalenceClasses(Automaton<T, V> automaton) {
        Int2ObjectMap<IntSet> equivalent = new Int2ObjectOpenHashMap<>();
        Int2ObjectMap<IntSet> nonEquivalent = new Int2ObjectOpenHashMap<>();
        for (int a = 0; a < automaton.states(); ++a) {
            for (int b = a + 1; b < automaton.states(); ++b) {
                if (isEquivalent(automaton, nonEquivalent, a, b)) {
                    equivalent.computeIfAbsent(a, k -> new IntOpenHashSet()).add(b);
                } else {
                    nonEquivalent.computeIfAbsent(a, k -> new IntOpenHashSet()).add(b);
                }
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            Int2ObjectMap<IntSet> newEquivalent = new Int2ObjectOpenHashMap<>();
            for (Map.Entry<Integer, IntSet> entry : equivalent.int2ObjectEntrySet()) {
                int a = entry.getKey();
                for (int b : entry.getValue()) {
                    if (isEquivalent(automaton, nonEquivalent, a, b)) {
                        newEquivalent.computeIfAbsent(a, k -> new IntOpenHashSet()).add(b);
                    } else {
                        nonEquivalent.computeIfAbsent(a, k -> new IntOpenHashSet()).add(b);
                        changed = true;
                    }
                }
            }
            if (changed) equivalent = newEquivalent;
        }

        IntSet visited = new IntOpenHashSet();
        Set<IntSet> classes = new HashSet<>();
        for (int a = 0; a < automaton.states(); ++a) {
            if (visited.contains(a)) continue;
            IntSet equivalenceClass = new IntOpenHashSet();
            equivalenceClass.add(a);
            visited.add(a);
            for (int b : equivalent.getOrDefault(a, IntSets.EMPTY_SET)) {
                equivalenceClass.add(b);
                visited.add(b);
            }
            classes.add(equivalenceClass);
        }

        return classes;
    }

    private static <T, V> boolean isEquivalent(Automaton<T, V> automaton, Int2ObjectMap<IntSet> nonEquivalent, int a, int b) {
        if (a == b) return true;
        if (automaton.isAccepting(a) != automaton.isAccepting(b)) return false;
        if (automaton.isActor(a) || automaton.isActor(b)) return false;
        if (nonEquivalent.getOrDefault(a, IntSets.EMPTY_SET).contains(b)) return false;

        Set<T> keys = new HashSet<>(automaton.symbolsFrom(a));
        keys.addAll(automaton.symbolsFrom(b));

        for (T key : keys) {
            IntSet nextA = automaton.next(key, a);
            IntSet nextB = automaton.next(key, b);

            if (nextA.isEmpty() != nextB.isEmpty()) return false;

            for (int stateA : nextA) {
                for (int stateB : nextB) {
                    if (stateA == a && stateB == b) continue;
                    if (!isEquivalent(automaton, nonEquivalent, stateA, stateB)) return false;
                }
            }
        }

        if (a == 0) System.out.println(b);

        return true;
    }
}
