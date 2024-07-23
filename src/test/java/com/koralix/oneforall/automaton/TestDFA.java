package com.koralix.oneforall.automaton;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestDFA {
    @Test
    public void minimize() {
        DFA<Character, ?> dfa = new DFA<>();
        int q0 = dfa.state();
        int q1 = dfa.state();
        int q2 = dfa.state();
        int q3 = dfa.state();
        int q4 = dfa.state();
        int q5 = dfa.state();
        dfa.start(q0);
        dfa.accept(q2);
        dfa.accept(q4);
        dfa.transition('a', q0, q1);
        dfa.transition('b', q0, q3);
        dfa.transition('a', q1, q3);
        dfa.transition('b', q1, q2);
        dfa.transition('a', q2, q2);
        dfa.transition('b', q2, q2);
        dfa.transition('a', q3, q3);
        dfa.transition('b', q3, q4);
        dfa.transition('a', q4, q4);
        dfa.transition('b', q4, q4);
        dfa.transition('a', q5, q5);
        dfa.transition('b', q5, q4);
        AutomatonMinimizer.minimize(dfa);
        Assertions.assertEquals(
                "DFA{transitions=[{a=2, b=2}, {a=1, b=1}, {a=2, b=1}], accepting={1}, states=3, start=0, current=-1}",
                dfa.toString()
        );
    }
}
