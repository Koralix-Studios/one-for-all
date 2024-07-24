package com.koralix.oneforall.automaton;

import it.unimi.dsi.fastutil.ints.*;

import java.util.*;
import java.util.function.Function;

public class NFA<T, V> implements Automaton<T, V> {
    private final Int2ObjectMap<Set<Function<V, Boolean>>> subscribers = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<V> attachments = new Int2ObjectOpenHashMap<>();
    private final List<Map<T, IntSet>> transitions = new ArrayList<>();
    private final IntSet accepting = new IntOpenHashSet();
    private int states = 0;
    private IntSet start = new IntOpenHashSet();
    private IntSet current = new IntOpenHashSet();
    private T wildcard;

    @Override
    public void subscribe(int state, Function<V, Boolean> subscriber) {
        this.subscribers.computeIfAbsent(state, k -> new HashSet<>()).add(subscriber);
    }

    @Override
    public void attach(int state, V v) {
        this.attachments.put(state, v);
    }

    @Override
    public Int2ObjectMap<Set<Function<V, Boolean>>> subscribers() {
        return this.subscribers;
    }

    @Override
    public Int2ObjectMap<V> attachments() {
        return this.attachments;
    }

    @Override
    public boolean isActor(int state) {
        return this.subscribers.containsKey(state) || this.attachments.containsKey(state);
    }

    @Override
    public int state() {
        this.transitions.add(new HashMap<>());
        return states++;
    }

    @Override
    public int states() {
        return this.states;
    }

    @Override
    public void transition(T t, int a, int b) {
        this.transitions.get(a).computeIfAbsent(t, k -> new IntOpenHashSet()).add(b);
    }

    @Override
    public void clear() {
        this.transitions.clear();
        this.accepting.clear();
        this.states = 0;
        this.start.clear();
        this.current.clear();
    }

    @Override
    public Automaton<T, V> minimize() {
        DFA<T, V> dfa = this.toDFA();
        AutomatonMinimizer.minimize(dfa);
        return dfa.toNFA();
    }

    @Override
    public T wildcard() {
        return this.wildcard;
    }

    @Override
    public void wildcard(T t) {
        this.wildcard = t;
    }

    @Override
    public void start(int state) {
        this.start.add(state);
    }

    @Override
    public IntSet start() {
        return this.start;
    }

    @Override
    public void accept(int state) {
        this.accepting.add(state);
    }

    @Override
    public IntSet accepting() {
        return this.accepting;
    }

    @Override
    public boolean next(T t) {
        if (this.current.isEmpty()) this.reset();

        IntSet next = new IntOpenHashSet();
        for (int state : this.current) {
            next.addAll(next(t, state));
        }
        if (next.isEmpty() && this.wildcard != null) {
            for (int state : this.current) {
                next.addAll(next(this.wildcard, state));
            }
        }

        this.current = next;

        for (int state : this.current) {
            if (this.isAccepting(state)) {
                for (Function<V, Boolean> subscriber : this.subscribers.getOrDefault(state, Set.of())) {
                    if (subscriber.apply(this.attachments.get(state))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public IntSet next(T t, int state) {
        return this.epsilonClosure(this.transitions.get(state).getOrDefault(t, IntSets.EMPTY_SET));
    }

    @Override
    public void reset() {
        this.current = this.start;
    }

    @Override
    public Set<T> symbolsFrom(int state) {
        return this.transitions.get(state).keySet();
    }

    public DFA<T, V> toDFA() {
        DFA<T, V> dfa = new DFA<>();

        Map<IntSet, Integer> mapping = new HashMap<>();
        Queue<IntSet> queue = new LinkedList<>();

        IntSet start = this.epsilonClosure(this.start);

        int q0 = dfa.state();
        mapping.put(start, q0);
        dfa.start(q0);

        if (this.accepting.intStream().anyMatch(start::contains)) dfa.accept(q0);

        queue.add(start);

        while (!queue.isEmpty()) {
            IntSet states = queue.poll();
            int q = mapping.get(states);

            for (int state : states) {
                this.subscribers.getOrDefault(state, Set.of()).forEach(subscriber -> dfa.subscribe(q, subscriber));
                V attachment = this.attachments.get(state);
                if (attachment != null) dfa.attach(q, attachment);
            }

            Map<T, IntSet> reachable = new HashMap<>();
            for (int state : states) {
                for (Map.Entry<T, IntSet> entry : this.transitions.get(state).entrySet()) {
                    reachable.computeIfAbsent(entry.getKey(), k -> new IntOpenHashSet()).addAll(entry.getValue());
                }
            }

            for (Map.Entry<T, IntSet> entry : reachable.entrySet()) {
                IntSet next = this.epsilonClosure(entry.getValue());
                if (next.isEmpty()) continue;
                if (!mapping.containsKey(next)) {
                    int r = dfa.state();
                    mapping.put(next, r);
                    if (this.accepting.intStream().anyMatch(next::contains)) dfa.accept(r);
                    queue.add(next);
                }
                dfa.transition(entry.getKey(), q, mapping.get(next));
            }
        }

        return dfa;
    }

    private IntSet epsilonClosure(IntSet states) {
        IntSet closure = new IntOpenHashSet(states);
        Queue<Integer> queue = new LinkedList<>(states);

        while (!queue.isEmpty()) {
            int state = queue.poll();
            if (this.transitions.get(state).containsKey(null)) {
                for (int next : this.transitions.get(state).get(null)) {
                    if (closure.add(next)) queue.add(next);
                }
            }
        }

        return closure;
    }

    @Override
    public String toString() {
        return "NFA{" +
                "transitions=" + transitions +
                ", accepting=" + accepting +
                ", states=" + states +
                ", start=" + start +
                ", current=" + current +
                '}';
    }
}
