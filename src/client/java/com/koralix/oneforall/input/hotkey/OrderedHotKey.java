package com.koralix.oneforall.input.hotkey;

import com.koralix.oneforall.input.ButtonEvent;

import java.util.ArrayList;
import java.util.List;

public class OrderedHotKey extends HotKey<List<ButtonEvent>> {
    private OrderedHotKey(Runnable action, byte flags, List<ButtonEvent> expected) {
        super(action, flags, expected);
    }

    protected OrderedHotKey(Runnable action, byte flags) {
        this(action, flags, new ArrayList<>());
    }

    @Override
    protected boolean testKeys(List<ButtonEvent> expected, List<ButtonEvent> actual) {
        // Return true if empty is allowed and the actual events list is empty
        if (allowEmpty() && actual.isEmpty()) return true;

        // Return false if the actual events has more elements than the expected events and extra is not allowed
        if (!allowExtra() && actual.size() > expected.size() + (release() ? 1 : 0)) return false;

        // Return false if the actual events list does not contain the expected events list
        if (actual.size() - expected.size() - (release() ? 1 : 0) < 0) return false;
        if (!actual.subList(
                actual.size() - expected.size() - (release() ? 1 : 0),
                actual.size() - (release() ? 1 : 0)
        ).equals(expected)) return false;

        // Return true if release is allowed and the last actual event is the complement of the last expected event
        // or if release is not enabled
        return !release() || actual.get(actual.size() - 1).complementary().equals(expected.get(expected.size() - 1));
    }
}
