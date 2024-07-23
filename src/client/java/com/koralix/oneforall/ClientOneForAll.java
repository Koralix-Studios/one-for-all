package com.koralix.oneforall;

import com.koralix.oneforall.input.HotKey;
import com.koralix.oneforall.input.InputManager;
import com.koralix.oneforall.input.event.KeyboardKeyEvent;
import com.koralix.oneforall.platform.Platform;
import org.lwjgl.glfw.GLFW;

import java.util.List;

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

        inputManager.register(new HotKey(
                List.of(
                        new KeyboardKeyEvent(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_PRESS),
                        new KeyboardKeyEvent(GLFW.GLFW_KEY_C, GLFW.GLFW_PRESS)
                ),
                HotKey.ActivateOn.PRESS,
                HotKey.Context.GAME,
                false,
                false,
                false,
                true,
                false,
                () -> getLogger().info("Control + C pressed!")
        ));
        inputManager.optimize();
    }

    public InputManager getInputManager() {
        return inputManager;
    }
}
