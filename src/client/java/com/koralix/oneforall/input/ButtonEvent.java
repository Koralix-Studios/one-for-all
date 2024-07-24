package com.koralix.oneforall.input;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public record ButtonEvent(int button, int action, ButtonType type) {
    @Contract(" -> new")
    public @NotNull ButtonEvent complementary() {
        return new ButtonEvent(button, action == GLFW.GLFW_RELEASE ? GLFW.GLFW_PRESS : GLFW.GLFW_RELEASE, type);
    }

    public enum ButtonType {
        KEYBOARD,
        MOUSE
    }
}
