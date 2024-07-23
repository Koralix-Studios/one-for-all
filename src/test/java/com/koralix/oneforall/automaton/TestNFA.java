package com.koralix.oneforall.automaton;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestNFA {
    @Test
    public void dfa() {
        NFA<Character, ?> nfa = new NFA<>();
        int q0 = nfa.state();
        int q1 = nfa.state();
        int q2 = nfa.state();
        nfa.transition('a', q0, q0);
        nfa.transition('b', q0, q1);
        nfa.transition('a', q1, q1);
        nfa.transition('a', q1, q2);
        nfa.transition('b', q1, q1);
        nfa.transition('a', q2, q2);
        nfa.transition('b', q2, q2);
        nfa.transition('b', q2, q1);
        nfa.start(q0);
        nfa.accept(q2);
        Assertions.assertEquals(
                nfa.toDFA().toString(),
                "DFA{transitions=[{a=0, b=1}, {a=2, b=1}, {a=2, b=2}], accepting={2}, states=3, start=0, current=-1}"
        );
    }

    @Test
    public void minimize() {
        NFA<Character, ?> nfa = new NFA<>();
        int q0 = nfa.state();
        int q1 = nfa.state();
        int q2 = nfa.state();
        int q3 = nfa.state();
        int q4 = nfa.state();
        int q5 = nfa.state();
        nfa.transition('c', q0, q1);
        nfa.transition('a', q1, q2);
        nfa.transition('c', q3, q4);
        nfa.transition('b', q4, q5);
        nfa.start(q0);
        nfa.start(q3);
        nfa.accept(q2);
        nfa.accept(q5);

        nfa = nfa.toDFA().toNFA();

        System.out.println(nfa);
    }
}
