package com.koralix.oneforall.mixin.client.input;

import com.koralix.oneforall.ClientOneForAll;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Keyboard.class)
public class KeyboardInterceptor {
    @ModifyArg(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/InputUtil;setKeyboardCallbacks(JLorg/lwjgl/glfw/GLFWKeyCallbackI;Lorg/lwjgl/glfw/GLFWCharModsCallbackI;)V"
            ),
            index = 1
    )
    private GLFWKeyCallbackI onKey(GLFWKeyCallbackI keyCallback) {
        return (window, key, scancode, action, mods) -> {
            if (ClientOneForAll.getInstance().getInputManager().onKey(window, key, scancode, action, mods))
                return;

            keyCallback.invoke(window, key, scancode, action, mods);
        };
    }

    @ModifyArg(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/InputUtil;setKeyboardCallbacks(JLorg/lwjgl/glfw/GLFWKeyCallbackI;Lorg/lwjgl/glfw/GLFWCharModsCallbackI;)V"
            ),
            index = 2
    )
    private GLFWCharModsCallbackI onChar(GLFWCharModsCallbackI charModsCallback) {
        return (window, codepoint, mods) -> {
            if (!ClientOneForAll.getInstance().getInputManager().onChar(window, codepoint, mods))
                return;

            charModsCallback.invoke(window, codepoint, mods);
        };
    }
}
