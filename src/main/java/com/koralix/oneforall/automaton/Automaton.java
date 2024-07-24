package com.koralix.oneforall.automaton;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Set;
import java.util.function.Function;

public interface Automaton<T, V> {
    void subscribe(int state, Function<V, Boolean> subscriber);
    void attach(int state, V v);
    Int2ObjectMap<Set<Function<V, Boolean>>> subscribers();
    Int2ObjectMap<V> attachments();
    boolean isActor(int state);

    int state();
    int states();
    void transition(T t, int a, int b);
    void clear();
    Automaton<T, V> minimize();

    void start(int state);
    IntSet start();
    default boolean isStart(int state) {
        return start().contains(state);
    }
    void accept(int state);
    IntSet accepting();
    default boolean isAccepting(int state) {
        return accepting().contains(state);
    }

    boolean next(T t);
    IntSet next(T t, int state);
    void reset();

    Set<T> symbolsFrom(int state);
}
