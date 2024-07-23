package com.koralix.oneforall.input;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.automaton.Automaton;
import com.koralix.oneforall.automaton.NFA;
import com.koralix.oneforall.input.event.ButtonEvent;
import com.koralix.oneforall.input.event.InputEvent;
import com.koralix.oneforall.input.event.KeyboardKeyEvent;
import com.koralix.oneforall.input.event.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;

public class InputManager {
    private Automaton<InputEvent, HotKey> automaton = new NFA<>();

    public void register(HotKey hotKey) {
        hotKey.register(automaton);
    }

    public void optimize() {
        this.automaton = this.automaton.minimize();
        OneForAll.getInstance().getLogger().info("Automaton optimized! {}", this.automaton);
    }

    public boolean onKey(long window, int key, int scancode, int action, int mods) {
        return onInputEvent(new KeyboardKeyEvent(key, action));
    }

    public boolean onChar(long window, int codepoint, int mods) {
        return false;
    }

    public boolean onMouseMove(long window, double xpos, double ypos) {
        return false;
    }

    public boolean onMouseButton(long window, int button, int action, int mods) {
        return onInputEvent(new MouseButtonEvent(button, action));
    }

    public boolean onMouseScroll(long window, double xoffset, double yoffset) {
        return false;
    }

    public boolean onInputEvent(InputEvent event) {
        if (event instanceof ButtonEvent buttonEvent && buttonEvent.action() == GLFW.GLFW_REPEAT) return false;
        return automaton.next(event);
    }
}
