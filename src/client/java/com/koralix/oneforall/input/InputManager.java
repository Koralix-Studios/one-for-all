package com.koralix.oneforall.input;

import com.koralix.oneforall.input.hotkey.HotKey;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static com.koralix.oneforall.input.InputEvents.*;

public class InputManager implements
        KeyboardKeyCallback,
        KeyboardCharCallback,
        MouseMoveCallback,
        MouseButtonCallback,
        MouseScrollCallback
{
    private final List<HotKey<?>> hotKeys = new ArrayList<>();
    private final List<ButtonEvent> actualEvents = new ArrayList<>();

    public int register(HotKey<?> hotKey) {
        hotKeys.add(hotKey);
        return hotKeys.size() - 1;
    }

    public void unregister(int id) {
        hotKeys.remove(id);
    }

    public HotKey<?> hotKey(int id) {
        return hotKeys.get(id);
    }

    private boolean onButtonEvent(ButtonEvent event) {
        if (event.action() == GLFW.GLFW_REPEAT) return false;

        actualEvents.add(event);

        boolean[] cancelExclusive = {false};
        boolean cancelFurtherProcessing = false;
        for (HotKey<?> hotKey : hotKeys) {
            if (hotKey.on(event, actualEvents, cancelExclusive)) {
                cancelFurtherProcessing = true;
            }
        }

        if (event.action() == GLFW.GLFW_RELEASE) {
            actualEvents.remove(event);
            actualEvents.remove(event.complementary());
        }

        return cancelFurtherProcessing;
    }

    @Override
    public boolean onChar(long window, int codepoint, int mods) {
        return false;
    }

    @Override
    public boolean onKey(long window, int key, int scancode, int action, int mods) {
        return onButtonEvent(new ButtonEvent(key, action, ButtonEvent.ButtonType.KEYBOARD));
    }

    @Override
    public boolean onButton(long window, int button, int action, int mods) {
        return onButtonEvent(new ButtonEvent(button, action, ButtonEvent.ButtonType.MOUSE));
    }

    @Override
    public boolean onMove(long window, double xpos, double ypos) {
        return false;
    }

    @Override
    public boolean onScroll(long window, double xoffset, double yoffset) {
        return false;
    }

    public void register() {
        KEYBOARD_CHAR.register(this);
        KEYBOARD_KEY.register(this);
        MOUSE_BUTTON.register(this);
        MOUSE_MOVE.register(this);
        MOUSE_SCROLL.register(this);
    }
}
