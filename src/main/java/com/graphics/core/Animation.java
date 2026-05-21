package com.graphics.core;

// Animacion simple por frames, avanza segun tiempo acumulado.
public class Animation {

    private final Texture[] frames;
    private final float frameDuration;
    private float timerSeconds;
    private int currentIndex;

    public Animation(float framesPerSecond, Texture... frames) {
        this.frames = (frames != null) ? frames : new Texture[0];
        this.frameDuration = (framesPerSecond > 0f) ? (1.0f / framesPerSecond) : 1.0f;
    }

    public void update(float dt) {
        if (frames.length <= 1) return;
        timerSeconds += dt;
        while (timerSeconds >= frameDuration) {
            timerSeconds -= frameDuration;
            currentIndex = (currentIndex + 1) % frames.length;
        }
    }

    public Texture getCurrentFrame() {
        if (frames.length == 0) return null;
        return frames[currentIndex];
    }

    public void reset() {
        timerSeconds = 0f;
        currentIndex = 0;
    }
}
