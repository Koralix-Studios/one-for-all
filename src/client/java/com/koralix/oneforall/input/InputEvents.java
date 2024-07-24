package com.koralix.oneforall.input;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class InputEvents {

    public static final Event<KeyboardKeyCallback> KEYBOARD_KEY = EventFactory.createArrayBacked(
            KeyboardKeyCallback.class,
            (listeners) -> (window, key, scancode, action, mods) -> {
                for (KeyboardKeyCallback event : listeners) {
                    if (event.onKey(window, key, scancode, action, mods)) {
                        return true;
                    }
                }
                return false;
            });

    public static final Event<KeyboardCharCallback> KEYBOARD_CHAR = EventFactory.createArrayBacked(
            KeyboardCharCallback.class,
            (listeners) -> (window, codepoint, mods) -> {
                for (KeyboardCharCallback event : listeners) {
                    if (event.onChar(window, codepoint, mods)) {
                        return true;
                    }
                }
                return false;
            });

    public static final Event<MouseButtonCallback> MOUSE_BUTTON = EventFactory.createArrayBacked(
            MouseButtonCallback.class,
            (listeners) -> (window, button, action, mods) -> {
                for (MouseButtonCallback event : listeners) {
                    if (event.onButton(window, button, action, mods)) {
                        return true;
                    }
                }
                return false;
            });

    public static final Event<MouseMoveCallback> MOUSE_MOVE = EventFactory.createArrayBacked(
            MouseMoveCallback.class,
            (listeners) -> (window, xpos, ypos) -> {
                for (MouseMoveCallback event : listeners) {
                    if (event.onMove(window, xpos, ypos)) {
                        return true;
                    }
                }
                return false;
            });

    public static final Event<MouseScrollCallback> MOUSE_SCROLL = EventFactory.createArrayBacked(
            MouseScrollCallback.class,
            (listeners) -> (window, xoffset, yoffset) -> {
                for (MouseScrollCallback event : listeners) {
                    if (event.onScroll(window, xoffset, yoffset)) {
                        return true;
                    }
                }
                return false;
            });

    @FunctionalInterface
    public interface KeyboardKeyCallback {
        boolean onKey(long window, int key, int scancode, int action, int mods);
    }

    @FunctionalInterface
    public interface KeyboardCharCallback {
        boolean onChar(long window, int codepoint, int mods);
    }

    @FunctionalInterface
    public interface MouseButtonCallback {
        boolean onButton(long window, int button, int action, int mods);
    }

    @FunctionalInterface
    public interface MouseMoveCallback {
        boolean onMove(long window, double xpos, double ypos);
    }

    @FunctionalInterface
    public interface MouseScrollCallback {
        boolean onScroll(long window, double xoffset, double yoffset);
    }

}
