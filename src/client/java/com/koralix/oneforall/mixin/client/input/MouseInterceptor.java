package com.koralix.oneforall.mixin.client.input;

import com.koralix.oneforall.ClientOneForAll;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Mouse.class)
public class MouseInterceptor {
    @ModifyArg(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/InputUtil;setMouseCallbacks(JLorg/lwjgl/glfw/GLFWCursorPosCallbackI;Lorg/lwjgl/glfw/GLFWMouseButtonCallbackI;Lorg/lwjgl/glfw/GLFWScrollCallbackI;Lorg/lwjgl/glfw/GLFWDropCallbackI;)V"
            ),
            index = 1
    )
    private GLFWCursorPosCallbackI onMouseMove(GLFWCursorPosCallbackI cursorPosCallback) {
        return (window, xpos, ypos) -> {
            if (ClientOneForAll.getInstance().getInputManager().onMouseMove(window, xpos, ypos))
                return;

            cursorPosCallback.invoke(window, xpos, ypos);
        };
    }

    @ModifyArg(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/InputUtil;setMouseCallbacks(JLorg/lwjgl/glfw/GLFWCursorPosCallbackI;Lorg/lwjgl/glfw/GLFWMouseButtonCallbackI;Lorg/lwjgl/glfw/GLFWScrollCallbackI;Lorg/lwjgl/glfw/GLFWDropCallbackI;)V"
            ),
            index = 2
    )
    private GLFWMouseButtonCallbackI onMouseButton(GLFWMouseButtonCallbackI mouseButtonCallback) {
        return (window, button, action, mods) -> {
            if (ClientOneForAll.getInstance().getInputManager().onMouseButton(window, button, action, mods))
                return;

            mouseButtonCallback.invoke(window, button, action, mods);
        };
    }

    @ModifyArg(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/InputUtil;setMouseCallbacks(JLorg/lwjgl/glfw/GLFWCursorPosCallbackI;Lorg/lwjgl/glfw/GLFWMouseButtonCallbackI;Lorg/lwjgl/glfw/GLFWScrollCallbackI;Lorg/lwjgl/glfw/GLFWDropCallbackI;)V"
            ),
            index = 3
    )
    private GLFWScrollCallbackI onMouseScroll(GLFWScrollCallbackI scrollCallback) {
        return (window, xoffset, yoffset) -> {
            if (ClientOneForAll.getInstance().getInputManager().onMouseScroll(window, xoffset, yoffset))
                return;

            scrollCallback.invoke(window, xoffset, yoffset);
        };
    }
}
