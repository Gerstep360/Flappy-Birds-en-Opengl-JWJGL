package com.graphics.game;

import com.graphics.core.Animation;
import com.graphics.core.Node;
import com.graphics.core.Renderer;
import com.graphics.core.Texture;

// Pajaro controlable con fisica simple y animacion por frames.
public class Bird extends Node {

    public static final float ANCHO = 0.10f;
    public static final float ALTO  = 0.10f;

    private static final float GRAVEDAD      = -1.9f;
    private static final float IMPULSO       = 0.8f;
    private static final float VEL_MAX_CAIDA = -1.8f;
    private static final float MAX_ROT_UP    = 0.6f;
    private static final float MAX_ROT_DOWN  = -1.6f;
    private static final float DEAD_ROT      = -1.57f;

    private int playerIndex;
    private float x, y, velocityY;
    private float initialX;
    private float[] color;
    private int score;
    private boolean alive;
    private boolean frozen;

    private Animation animation;
    private float flapTimer;
    private float hitTimer;

    public Bird(int playerIndex, float x, float[] color, Texture[] frames) {
        this.playerIndex = playerIndex;
        this.x = x;
        this.initialX = x;
        this.color = color;
        this.animation = new Animation(15.0f, frames);
    }

    @Override
    public void _ready() {
        x = initialX;
        y = 0.0f;
        velocityY = 0.0f;
        flapTimer = 0.0f;
        hitTimer = 0.0f;
        score = 0;
        alive = true;
        frozen = true;
        animation.reset();
    }

    @Override
    public void _process(float delta) {
        if (frozen) return;
        applyGravity(delta);
        updateStateTimers(delta);
        clampToGround();

        if (!alive) {
            float speed = 0.0f;
            if (getParent() instanceof Game) {
                speed = ((Game) getParent()).getPipeSpeed();
            } else {
                speed = Config.PIPE_VELOCIDAD_BASE;
            }
            x -= speed * delta;
        }
    }

    @Override
    public void _render(Renderer r) {
        if (!alive && y < -0.84f && hitTimer > 2.0f) return;

        float rotation = getRotation();

        float w = ANCHO, h = ALTO;

        Texture frame = animation.getCurrentFrame();
        if (Config.TEXTURED_MODE && frame != null) {
            r.drawTexture(frame, x, y, Config.Z_BIRD, w, h, rotation);
            return;
        }

        // Modo geometrico: pajaro compuesto por figuras.
        float cr = color[0], cg = color[1], cb = color[2];
        if (!alive && y > -0.8f) { cr = 1.0f; cg = 0.3f; cb = 0.3f; }

        // Cola
        r.drawTriangle(x - w*0.4f, y, Config.Z_BIRD, w*0.5f, h*0.5f, rotation + 3.14f, cr*0.8f, cg*0.8f, cb*0.8f);
        // Cuerpo
        r.drawCircle(x, y, Config.Z_BIRD, w, h, cr, cg, cb);
        // Ojo
        r.drawCircle(x + w*0.25f, y + h*0.2f, Config.Z_BIRD, w*0.35f, h*0.35f, 1, 1, 1);
        // Pupila
        r.drawCircle(x + w*0.3f, y + h*0.2f, Config.Z_BIRD, w*0.15f, h*0.15f, 0, 0, 0);
        // Pico
        r.drawTriangle(x + w*0.5f, y - h*0.1f, Config.Z_BIRD, w*0.5f, h*0.4f, rotation - 1.57f, 1, 0.5f, 0);
        // Ala animada
        float wingAngle = alive ? (float)Math.sin(flapTimer) * 0.8f : 0.8f;
        r.drawTriangle(x - w*0.1f, y - h*0.1f, Config.Z_BIRD, w*0.6f, h*0.5f, rotation + wingAngle + 3.14f, cr+0.1f, cg+0.1f, cb+0.1f);
    }

    private void applyGravity(float delta) {
        velocityY += GRAVEDAD * delta;
        if (velocityY < VEL_MAX_CAIDA) velocityY = VEL_MAX_CAIDA;
        y += velocityY * delta;
    }

    private void updateStateTimers(float delta) {
        if (alive) {
            animation.update(delta);
            flapTimer += delta * 10.0f;
        } else {
            hitTimer += delta;
        }
    }

    private void clampToGround() {
        if (y < Config.GROUND_Y) {
            y = Config.GROUND_Y;
            velocityY = 0;
        }
    }

    private float getRotation() {
        float rotation = velocityY * 0.4f;
        if (!alive) rotation = DEAD_ROT;
        return clamp(rotation, MAX_ROT_DOWN, MAX_ROT_UP);
    }

    private float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    // Acciones
    public void flap()     { if (alive) velocityY = IMPULSO; }
    public void addPoint() { score++; }

    // Estado 
    public boolean isAlive()        { return alive; }
    public void setAlive(boolean v) { alive = v; }
    public void setFrozen(boolean v){ frozen = v; }
    public int getPlayerIndex()     { return playerIndex; }
    public int getScore()           { return score; }
    public float[] getColor()       { return color; }

    public float getTop()    { return y + ALTO * 0.5f; }
    public float getBottom() { return y - ALTO * 0.5f; }
    public float getLeft()   { return x - ANCHO * 0.5f; }
    public float getRight()  { return x + ANCHO * 0.5f; }

    public boolean isOutOfBounds() {
        if (!alive) return false;
        return getTop() >= 1.0f || getBottom() <= -1.0f;
    }
}
