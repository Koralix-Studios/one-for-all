package com.koralix.oneforall.automaton;

import it.unimi.dsi.fastutil.ints.*;

import java.util.*;
import java.util.function.Function;

public class DFA<T, V> implements Automaton<T, V> {
    private final Int2ObjectMap<Set<Function<V, Boolean>>> subscribers = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<V> attachments = new Int2ObjectOpenHashMap<>();
    private final List<Map<T, Integer>> transitions = new ArrayList<>();
    private final IntSet accepting = new IntOpenHashSet();
    private int states = 0;
    private int start = -1;
    private int current = -1;

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
        this.transitions.get(a).put(t, b);
    }

    @Override
    public void clear() {
        this.transitions.clear();
        this.accepting.clear();
        this.states = 0;
        this.start = -1;
        this.current = -1;
    }

    @Override
    public Automaton<T, V> minimize() {
        AutomatonMinimizer.minimize(this);
        return this;
    }

    @Override
    public void start(int state) {
        this.start = state;
    }

    @Override
    public IntSet start() {
        return IntSets.singleton(this.start);
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
        if (this.current == -1) this.reset();

        this.current = this.transitions.get(this.current).getOrDefault(t, -1);

        if (this.isAccepting(this.current)) {
            for (Function<V, Boolean> subscriber : this.subscribers.getOrDefault(this.current, Collections.emptySet())) {
                if (subscriber.apply(this.attachments.get(this.current))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public IntSet next(T t, int state) {
        return Optional.ofNullable(this.transitions.get(state).get(t))
                .map(IntSets::singleton)
                .orElse(IntSets.EMPTY_SET);
    }

    @Override
    public void reset() {
        this.current = this.start;
    }

    @Override
    public Set<T> symbolsFrom(int state) {
        return this.transitions.get(state).keySet();
    }

    public NFA<T, V> toNFA() {
        NFA<T, V> nfa = new NFA<>();

        for (int i = 0; i < this.states; ++i) {
            nfa.state();
            for (Function<V, Boolean> subscriber : this.subscribers.getOrDefault(i, Set.of())) {
                nfa.subscribe(i, subscriber);
            }
            V attachment = this.attachments.get(i);
            if (attachment != null) nfa.attach(i, attachment);

            for (Map.Entry<T, Integer> entry : this.transitions.get(i).entrySet()) {
                nfa.transition(entry.getKey(), i, entry.getValue());
            }
        }

        for (int accepting : this.accepting) {
            nfa.accept(accepting);
        }

        nfa.start(this.start);

        return nfa;
    }

    @Override
    public String toString() {
        return "DFA{" +
                "transitions=" + transitions +
                ", accepting=" + accepting +
                ", states=" + states +
                ", start=" + start +
                ", current=" + current +
                '}';
    }
}
