package com.graphics.game;

import com.graphics.core.Node;
import com.graphics.core.Renderer;
import com.graphics.core.Texture;
import java.util.HashSet;
import java.util.Set;

// Par de tuberias con hueco y scoring por jugador.
public class Pipe extends Node {

    private static final float TEXTURE_HEIGHT = 1.5f;
    private static final float OFFSCREEN_X = -1.3f;

    private float x, gapCenterY, gapHeight;
    private Texture texture;
    private float speed;

    // Tracking de puntaje por jugador 
    private final Set<Integer> scoredPlayers = new HashSet<>();

    public Pipe(float x, float gapCentroY, float gapHeight, Texture texture) {
        this.x = x;
        this.gapCenterY = gapCentroY;
        this.gapHeight = gapHeight;
        this.texture = texture;
    }

    @Override
    public void _process(float delta) {
        x -= speed * delta;
        if (isOutOfScreen()) queueFree();
    }

    @Override
    public void _render(Renderer r) {
        float gapTop = getGapTop();
        float gapBottom = getGapBottom();
        if (Config.TEXTURED_MODE && texture != null) {
            drawTextured(r, gapTop, gapBottom);
        } else {
            drawSolid(r, gapTop, gapBottom);
        }
    }

    private void drawTextured(Renderer r, float gapTop, float gapBottom) {
        r.drawTexture(texture, x, gapTop + TEXTURE_HEIGHT * 0.5f, Config.Z_PIPES,
            Config.PIPE_ANCHO, TEXTURE_HEIGHT, 3.14159f);
        r.drawTexture(texture, x, gapBottom - TEXTURE_HEIGHT * 0.5f, Config.Z_PIPES,
            Config.PIPE_ANCHO, TEXTURE_HEIGHT, 0);
    }

    private void drawSolid(Renderer r, float gapTop, float gapBottom) {
        float[] c = Config.PIPE_COLOR;
        float altoSup = 1.0f - gapTop;
        if (altoSup > 0) {
            r.drawRect(x, gapTop + altoSup * 0.5f, Config.Z_PIPES, Config.PIPE_ANCHO, altoSup, 0, c[0], c[1], c[2]);
        }
        float altoInf = gapBottom + 1.0f;
        if (altoInf > 0) {
            r.drawRect(x, -1 + altoInf * 0.5f, Config.Z_PIPES, Config.PIPE_ANCHO, altoInf, 0, c[0], c[1], c[2]);
        }
    }

    // -- Scoring per-bird --
    public boolean hasScored(int playerIndex)  { return scoredPlayers.contains(playerIndex); }
    public void markScored(int playerIndex)    { scoredPlayers.add(playerIndex); }

    // -- Control --
    public void setSpeed(float s)       { this.speed = s; }
    public void setTexture(Texture t)   { this.texture = t; }

    // -- Consultas --
    public float getX()          { return x; }
    public float getGapCentroY() { return gapCenterY; }
    public float getGapHeight()  { return gapHeight; }
    public boolean isOutOfScreen() { return x + Config.PIPE_ANCHO * 0.5f < OFFSCREEN_X; }

    private float getGapTop() { return gapCenterY + gapHeight * 0.5f; }
    private float getGapBottom() { return gapCenterY - gapHeight * 0.5f; }
}
