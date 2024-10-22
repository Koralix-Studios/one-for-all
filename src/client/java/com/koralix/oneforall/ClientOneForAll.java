package com.koralix.oneforall;

import com.koralix.oneforall.input.ButtonEvent;
import com.koralix.oneforall.input.hotkey.HotKey;
import com.koralix.oneforall.input.InputManager;
import com.koralix.oneforall.platform.Platform;
import org.lwjgl.glfw.GLFW;

public class ClientOneForAll extends OneForAll {
    private final InputManager inputManager = new InputManager();

    public ClientOneForAll(Platform platform) {
        super(platform);
    }

    public static ClientOneForAll getInstance() {
        return (ClientOneForAll) OneForAll.getInstance();
    }

    @Override
    public void onInitialize() {
        getLogger().info("Initializing OneForAll...");
    }

    public void onInitializeClient() {
        getLogger().info("Initializing OneForAll client...");

        inputManager.register(HotKey.unordered(() -> {
                    getLogger().info("Unordered hotkey pressed!");
                })
                .add(new ButtonEvent(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_PRESS, ButtonEvent.ButtonType.KEYBOARD))
                .add(new ButtonEvent(GLFW.GLFW_KEY_C, GLFW.GLFW_PRESS, ButtonEvent.ButtonType.KEYBOARD)));
        inputManager.register(HotKey.ordered(() -> {
                    getLogger().info("Ordered hotkey pressed!");
                })
                .add(new ButtonEvent(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_PRESS, ButtonEvent.ButtonType.KEYBOARD))
                .add(new ButtonEvent(GLFW.GLFW_KEY_A, GLFW.GLFW_PRESS, ButtonEvent.ButtonType.KEYBOARD)));

        inputManager.register();
    }

    public InputManager getInputManager() {
        return inputManager;
    }
}
