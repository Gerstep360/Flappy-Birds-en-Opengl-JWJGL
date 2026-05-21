package com.graphics.core;

import org.lwjgl.glfw.GLFW;

// Maneja teclado con deteccion de "just pressed" (flanco) por frame.
public class Input {

    private final long window;
    private final boolean[] prevDown = new boolean[GLFW.GLFW_KEY_LAST + 1];

    public Input(long window) {
        this.window = window;
    }

    public boolean isExitPressed() {
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS;
    }

    public boolean isSpaceJustPressed() {
        return isJustPressed(GLFW.GLFW_KEY_SPACE);
    }

    public boolean isUpJustPressed() {
        return isJustPressed(GLFW.GLFW_KEY_UP);
    }

    public boolean isWJustPressed() {
        return isJustPressed(GLFW.GLFW_KEY_W);
    }

    public boolean isIJustPressed() {
        return isJustPressed(GLFW.GLFW_KEY_I);
    }

    public boolean isMJustPressed() {
        return isJustPressed(GLFW.GLFW_KEY_M);
    }

    public boolean isRJustPressed() {
        return isJustPressed(GLFW.GLFW_KEY_R);
    }

    public boolean isTJustPressed() {
        return isJustPressed(GLFW.GLFW_KEY_T);
    }

    public boolean isSJustPressed() {
        return isJustPressed(GLFW.GLFW_KEY_S);
    }

    private boolean isJustPressed(int key) {
        if (key < 0 || key >= prevDown.length) return false;
        boolean down = isDown(key);
        boolean justPressed = down && !prevDown[key];
        prevDown[key] = down;
        return justPressed;
    }

    private boolean isDown(int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }
}
