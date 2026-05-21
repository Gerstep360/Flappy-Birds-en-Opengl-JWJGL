package com.graphics.game;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import com.graphics.core.Input;
import com.graphics.core.Renderer;

// Motor principal: crea ventana y ejecuta el loop.
public class App {

    private long window;
    private Renderer renderer;
    private Input input;
    private Game game;

    public static void main(String[] args) {
        new App().run();
    }

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        initWindow();
        initOpenGL();
        initRenderer();
        Assets.loadAll();
        input = new Input(window);
        game = new Game();
        game.enterTree();
    }

    private void initWindow() {
        if (!GLFW.glfwInit()) throw new IllegalStateException("GLFW Fail");
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(
            Config.VENTANA_ANCHO, Config.VENTANA_ALTO, Config.VENTANA_TITULO, 0, 0);
        if (window == 0) throw new RuntimeException("Window Fail");

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);
    }

    private void initOpenGL() {
        GL.createCapabilities();
        GL11.glViewport(0, 0, Config.VENTANA_ANCHO, Config.VENTANA_ALTO);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void initRenderer() {
        renderer = new Renderer();
        float aspect = (float) Config.VENTANA_ANCHO / Config.VENTANA_ALTO;
        renderer.setOrtho(-aspect, aspect, -1, 1);
    }

    private void loop() {
        float lastTime = (float) GLFW.glfwGetTime();

        while (!GLFW.glfwWindowShouldClose(window)) {
            float now = (float) GLFW.glfwGetTime();
            float delta = Math.min(now - lastTime, 0.05f);
            lastTime = now;

            handleInput();
            game.processTree(delta);
            float[] sky = Config.COLOR_CIELO;
            GL11.glClearColor(sky[0], sky[1], sky[2], 1);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            renderer.start();
            game.renderTree(renderer);
            GLFW.glfwSetWindowTitle(window, game.getWindowTitle());
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void handleInput() {
        if (input.isExitPressed()) GLFW.glfwSetWindowShouldClose(window, true);

        if (input.isTJustPressed()) toggleTextures();
        if (input.isMJustPressed()) toggleMusic();
        if (input.isSJustPressed()) toggleSfx();
        if (input.isSpaceJustPressed() || input.isUpJustPressed()) game.jumpBird(0);
        if (input.isWJustPressed()) game.jumpBird(1);
        if (input.isIJustPressed()) game.jumpBird(2);
        if (input.isRJustPressed()) game.reset();
    }

    private void toggleTextures() {
        Config.TEXTURED_MODE = !Config.TEXTURED_MODE;
        Assets.playSound("swoosh");
    }

    private void toggleMusic() {
        Config.MUTE_MUSIC = !Config.MUTE_MUSIC;
        if (Config.MUTE_MUSIC) {
            Assets.stopSound("menu");
            Assets.stopSound("game");
            Assets.stopSound("gameover");
            return;
        }
        if (game.isMenu()) Assets.loopSound("menu");
        else if (game.isGameOver()) Assets.loopSound("gameover");
        else Assets.loopSound("game");
    }

    private void toggleSfx() {
        Config.MUTE_SFX = !Config.MUTE_SFX;
        Assets.playSound("swoosh");
    }

    private void cleanup() {
        renderer.cleanup();
        Assets.cleanup();
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }
}
