package com.graphics.game;

import com.graphics.core.Node;
import com.graphics.core.Renderer;
import com.graphics.core.Texture;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Fondo con parallax simple (cielo, nubes y base).
public class Background extends Node {

    private static class Cloud {
        float x, y, scale, speed;
        Cloud(float x, float y, float scale, float speed) {
            this.x = x; this.y = y; this.scale = scale; this.speed = speed;
        }
    }

    private final List<Cloud> clouds = new ArrayList<>();
    private final Random random = new Random();
    private Texture backgroundTexture, baseTexture;
    private float backgroundOffset, baseOffset;
    private boolean isNight;
    private float backgroundSpeed, baseSpeed;
    private static final int CLOUD_COUNT = 6;

    @Override
    public void _ready() {
        backgroundTexture = Assets.getTexture("bg_day");
        baseTexture = Assets.getTexture("base");
        for (int i = 0; i < CLOUD_COUNT; i++) spawnCloud(random.nextFloat() * 2f - 1f);
    }

    @Override
    public void _process(float delta) {
        float fullWidth = getFullWidth();
        updateOffsets(delta, fullWidth);
        updateClouds(delta);
    }

    @Override
    public void _render(Renderer r) {
        float fullWidth = getFullWidth();
        drawBackground(r, fullWidth);
        drawClouds(r);
        drawBase(r, fullWidth);
    }

    public void setSpeed(float bg, float base) { backgroundSpeed = bg; baseSpeed = base; }

    public void setNightMode(boolean night) {
        if (isNight != night) {
            isNight = night;
            backgroundTexture = Assets.getTexture(night ? "bg_night" : "bg_day");
        }
    }

    private float getFullWidth() {
        float aspect = (float) Config.VENTANA_ANCHO / Config.VENTANA_ALTO;
        return aspect * 2f;
    }

    private void updateOffsets(float delta, float fullWidth) {
        backgroundOffset -= backgroundSpeed * 0.1f * delta;
        baseOffset -= baseSpeed * delta;
        if (backgroundOffset <= -fullWidth) backgroundOffset += fullWidth;
        if (baseOffset <= -fullWidth) baseOffset += fullWidth;
    }

    private void updateClouds(float delta) {
        float moving = (backgroundSpeed > 0) ? 1f : 0f;
        for (int i = clouds.size() - 1; i >= 0; i--) {
            Cloud c = clouds.get(i);
            c.x -= c.speed * delta * moving;
            if (c.x < -1.5f) {
                clouds.remove(i);
                spawnCloud(1.5f);
            }
        }
    }

    private void drawBackground(Renderer r, float fullWidth) {
        if (Config.TEXTURED_MODE && backgroundTexture != null) {
            r.drawTexture(backgroundTexture, backgroundOffset, 0, Config.Z_BACKGROUND, fullWidth, 2f, 0);
            r.drawTexture(backgroundTexture, backgroundOffset + fullWidth, 0, Config.Z_BACKGROUND, fullWidth, 2f, 0);
        }
    }

    private void drawClouds(Renderer r) {
        if (Config.TEXTURED_MODE) return;
        float z = Config.Z_BACKGROUND - 0.05f;
        for (Cloud c : clouds) {
            r.drawCircle(c.x, c.y, z, c.scale, c.scale * 0.6f, 1, 1, 1);
            r.drawCircle(c.x + c.scale * 0.3f, c.y - c.scale * 0.1f, z, c.scale * 0.8f, c.scale * 0.5f, 1, 1, 1);
            r.drawCircle(c.x - c.scale * 0.3f, c.y - c.scale * 0.1f, z, c.scale * 0.8f, c.scale * 0.5f, 1, 1, 1);
        }
    }

    private void drawBase(Renderer r, float fullWidth) {
        if (Config.TEXTURED_MODE && baseTexture != null) {
            r.drawTexture(baseTexture, baseOffset, Config.GROUND_Y - 0.15f, Config.Z_BASE, fullWidth, 0.3f, 0);
            r.drawTexture(baseTexture, baseOffset + fullWidth, Config.GROUND_Y - 0.15f, Config.Z_BASE, fullWidth, 0.3f, 0);
            return;
        }
        if (!Config.TEXTURED_MODE) {
            r.drawRect(0, Config.GROUND_Y - 0.15f, Config.Z_BASE, fullWidth, 0.28f, 0, 0.70f, 0.55f, 0.35f);
            r.drawRect(0, Config.GROUND_Y - 0.025f, Config.Z_BASE, fullWidth, 0.05f, 0, 0.40f, 0.80f, 0.20f);
        }
    }

    private void spawnCloud(float x) {
        clouds.add(new Cloud(x, 0.3f + random.nextFloat()*0.5f,
                             0.2f + random.nextFloat()*0.3f, 0.05f + random.nextFloat()*0.1f));
    }
}
