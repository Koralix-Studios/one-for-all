package com.koralix.oneforall.input.hotkey;

import com.koralix.oneforall.input.ButtonEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnorderedHotKey extends HotKey<Set<ButtonEvent>> {
    private UnorderedHotKey(Runnable action, byte flags, Set<ButtonEvent> expected) {
        super(action, flags, expected);
    }

    protected UnorderedHotKey(Runnable action, byte flags) {
        this(action, flags, new HashSet<>());
    }

    @Override
    protected boolean testKeys(Set<ButtonEvent> expected, List<ButtonEvent> actual) {
//        boolean unrestricted = Set.copyOf(actual).containsAll(expected);
//        boolean restricted = unrestricted && expected.containsAll(actual);
//
//        boolean extra = allowExtra() && unrestricted;
//        boolean empty = allowEmpty() && actual.isEmpty();
//        boolean release = release() &&
//                (extra || restricted) &&
//                expected.stream().map(ButtonEvent::complementary).anyMatch(actual::contains);
//        return extra || empty || (release() ? release : restricted);

        // Return true if empty is allowed and the actual events list is empty
        if (allowEmpty() && actual.isEmpty()) return true;

        // Return false if the actual events has more elements than the expected events and extra is not allowed
        if (!allowExtra() && actual.size() > expected.size() + (release() ? 1 : 0)) return false;

        // Return false if the actual events list does not contain the expected events list
        if (!Set.copyOf(actual).containsAll(expected)) return false;

        // Return true if release is allowed and the last actual event is the complement of the last expected event
        // or if release is not enabled
        return !release() || expected.contains(actual.get(actual.size() - 1).complementary());
    }
}
