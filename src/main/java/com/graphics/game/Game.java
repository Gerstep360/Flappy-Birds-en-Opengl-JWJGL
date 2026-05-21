package com.graphics.game;

import java.util.List;
import java.util.Random;

import com.graphics.core.Node;
import com.graphics.core.Renderer;
import com.graphics.core.Texture;

// Escena raiz: maneja estados, spawn, colisiones y dificultad.
public class Game extends Node {

    private GameState state = GameState.MENU;
    private int nivel = 1;
    private float timerSpawn = 0;
    private final Random random = new Random();
    private float gameOverTimer = 0.0f;
    private boolean justReset = false;

    @Override
    public void _ready() {
        addChild(new Background());
        addChild(new ParticleSystem());
        addChild(new Bird(0, Config.BIRD1_X, Config.BIRD1_COLOR, Assets.getBirdFrames("1P")));
        addChild(new Bird(1, Config.BIRD2_X, Config.BIRD2_COLOR, Assets.getBirdFrames("2P")));
        addChild(new Bird(2, Config.BIRD3_X, Config.BIRD3_COLOR, Assets.getBirdFrames("3P")));
        
        addChild(new HUD());

        Assets.loopSound("menu");
    }

    @Override
    public void _process(float delta) {
        List<Bird> birds = getChildren(Bird.class);
        Background bg    = getChild(Background.class);

        if (state == GameState.MENU || state == GameState.READY) {
            updateMenu(birds, bg);
            justReset = false;
            return;
        }

        if (state == GameState.GAMEOVER) {
            updateGameOver(delta, birds, bg);
            justReset = false;
            return;
        }

        updatePlaying(delta, birds, bg);
        justReset = false;
    }

    @Override
    public void _render(Renderer r) {
        if (state == GameState.GAMEOVER) {
            Texture tex = Assets.getTexture("gameover");
            if (Config.TEXTURED_MODE && tex != null) {
                r.drawTexture(tex, 0, 0.3f, Config.Z_OVERLAY, 0.6f, 0.15f, 0);
            } else {
                float[] ov = Config.COLOR_OVERLAY;
                r.drawRect(0, 0.2f, Config.Z_OVERLAY, 0.8f, 0.3f, 0, ov[0], ov[1], ov[2]);
                r.drawRect(0, 0.2f, Config.Z_OVERLAY-0.01f, 0.4f, 0.05f,  0.78f, 1,0.2f,0.2f);
                r.drawRect(0, 0.2f, Config.Z_OVERLAY-0.01f, 0.4f, 0.05f, -0.78f, 1,0.2f,0.2f);
            }
        }

        if (state == GameState.MENU || state == GameState.READY) {
            Texture tex = Assets.getTexture("message");
            if (Config.TEXTURED_MODE && tex != null) {
                r.drawTexture(tex, 0, 0, Config.Z_OVERLAY, 0.8f, 1.1f, 0);
            }
        }
    }

    public void jumpBird(int index) {
        if (justReset) return;
        if (state == GameState.GAMEOVER) {
            if (gameOverTimer < Config.GAMEOVER_RESTART_DELAY) {
                return;
            }
            reset();
            Assets.playSound("swoosh");
            return;
        }
        if (state == GameState.MENU || state == GameState.READY) {
            state = GameState.PLAYING;
            Assets.stopSound("menu");
            Assets.loopSound("game");
            return;
        }
        List<Bird> birds = getChildren(Bird.class);
        for (Bird b : birds) {
            if (b.getPlayerIndex() == index && b.isAlive()) {
                b.flap();
                Assets.playSound("wing_" + index);
                ParticleSystem ps = getChild(ParticleSystem.class);
                if (ps != null) ps.emit(b.getLeft() + Bird.ANCHO*0.5f, b.getBottom(), b.getColor(), 5);
            }
        }
    }

    public void reset() {
        resetBirds();
        clearPipes();
        clearParticles();

        timerSpawn = 0;
        nivel = 1;
        state = GameState.MENU;
        gameOverTimer = 0.0f;
        justReset = true;
        Assets.stopSound("game");
        Assets.stopSound("gameover");
        Assets.loopSound("menu");
    }

    private void updateMenu(List<Bird> birds, Background bg) {
        bg.setSpeed(0, 0);
        for (Bird b : birds) b.setFrozen(true);
    }

    private void updateGameOver(float delta, List<Bird> birds, Background bg) {
        bg.setSpeed(0, 0);
        for (Pipe p : getChildren(Pipe.class)) p.setSpeed(0);
        for (Bird b : birds) b.setFrozen(false);
        gameOverTimer += delta;
    }

    private void updatePlaying(float delta, List<Bird> birds, Background bg) {
        int maxScore = getMaxScore();
        float speed = calcVelocidad(maxScore);
        boolean night = isNightMode(maxScore);
        bg.setNightMode(night);
        bg.setSpeed(speed, speed);
        for (Bird b : birds) b.setFrozen(false);

        nivel = (maxScore / Config.SCORE_PER_LEVEL) + 1;
        checkBirdBounds(birds);
        if (checkGameOver(birds)) return;

        updatePipeSpawn(delta, maxScore);
        updatePipes(birds, speed, night);
        if (checkScoreLimit()) return;
    }

    private void updatePipeSpawn(float delta, int maxScore) {
        float spawnTime = calcSpawnTime(maxScore);
        timerSpawn += delta;
        if (timerSpawn >= spawnTime) {
            timerSpawn = 0;
            spawnPipe(maxScore);
        }
    }

    private void updatePipes(List<Bird> birds, float speed, boolean night) {
        Texture pipeTex = Assets.getTexture(night ? "pipe_red" : "pipe_green");
        ParticleSystem particles = getChild(ParticleSystem.class);

        for (Pipe p : getChildren(Pipe.class)) {
            p.setSpeed(speed);
            p.setTexture(pipeTex);
            scorePipes(p, birds);
            checkPipeCollisions(p, birds, particles);
        }
    }

    private void scorePipes(Pipe p, List<Bird> birds) {
        for (Bird b : birds) {
            if (!p.hasScored(b.getPlayerIndex())
                && p.getX() + Config.PIPE_ANCHO * 0.5f < b.getLeft()) {
                p.markScored(b.getPlayerIndex());
                if (b.isAlive()) {
                    b.addPoint();
                    Assets.playSound("point_" + b.getPlayerIndex());
                }
            }
        }
    }

    private void checkPipeCollisions(Pipe p, List<Bird> birds, ParticleSystem particles) {
        for (Bird b : birds) {
            if (b.isAlive() && Collision.checkBirdPipe(b, p)) {
                b.setAlive(false);
                Assets.playSound("hit_" + b.getPlayerIndex());
                if (particles != null) {
                    particles.emit(b.getRight(), b.getTop(), b.getColor(), 15);
                }
            }
        }
    }

    private void checkBirdBounds(List<Bird> birds) {
        for (Bird b : birds) {
            if (b.isAlive() && (b.isOutOfBounds() || b.getBottom() <= Config.GROUND_Y)) {
                b.setAlive(false);
                Assets.playSound("hit_" + b.getPlayerIndex());
            }
        }
    }

    private boolean isNightMode(int maxScore) {
        return (maxScore / Config.SCORE_CYCLE_DAY_NIGHT) % 2 != 0;
    }

    private void spawnPipe(int maxScore) {
        float variation = Math.min(1f, maxScore / 50f);
        float minY = Config.PIPE_GAP_MIN_Y - variation * 0.1f;
        float maxY = Config.PIPE_GAP_MAX_Y + variation * 0.1f;
        float gapCentro = minY + random.nextFloat() * (maxY - minY);
        float gapHeight = Config.PIPE_GAP_ALTO_MIN
            + (Config.PIPE_GAP_ALTO_BASE - Config.PIPE_GAP_ALTO_MIN)
            * (float) Math.exp(-Config.PIPE_GAP_K * maxScore);

        boolean night = isNightMode(maxScore);
        Texture tex = Assets.getTexture(night ? "pipe_red" : "pipe_green");
        addChild(new Pipe(1.4f, gapCentro, gapHeight, tex));
    }

    private boolean checkGameOver(List<Bird> birds) {
        for (Bird b : birds) if (b.isAlive()) return false;
        state = GameState.GAMEOVER;
        gameOverTimer = 0.0f;
        Assets.playSound("die");
        Assets.stopSound("game");
        Assets.loopSound("gameover");
        return true;
    }

    private boolean checkScoreLimit() {
        if (!Config.TERMINAR_PUNTAJE) return false;
        if (getMaxScore() < Config.PUNTAJE_TERMINAR) return false;
        state = GameState.GAMEOVER;
        gameOverTimer = 0.0f;
        Assets.playSound("die");
        Assets.stopSound("game");
        Assets.loopSound("gameover");
        return true;
    }

    private float calcVelocidad(int score) {
        return Config.PIPE_VELOCIDAD_MAX
            - (Config.PIPE_VELOCIDAD_MAX - Config.PIPE_VELOCIDAD_BASE)
            * (float) Math.exp(-Config.PIPE_VELOCIDAD_K * score);
    }

    public float getPipeSpeed() {
        if (state == GameState.PLAYING) {
            return calcVelocidad(getMaxScore());
        }
        return 0.0f;
    }

    private float calcSpawnTime(int score) {
        return Config.PIPE_SPAWN_MIN
            + (Config.PIPE_SPAWN_BASE - Config.PIPE_SPAWN_MIN)
            * (float) Math.exp(-Config.PIPE_SPAWN_K * score);
    }

    public GameState getState() { return state; }
    public int getNivel()       { return nivel; }
    public boolean isMenu()     { return state == GameState.MENU || state == GameState.READY; }
    public boolean isGameOver() { return state == GameState.GAMEOVER; }

    public int getScore(int index) {
        for (Bird b : getChildren(Bird.class)) {
            if (b.getPlayerIndex() == index) return b.getScore();
        }
        return 0;
    }

    public int getMaxScore() {
        int max = 0;
        for (Bird b : getChildren(Bird.class)) if (b.getScore() > max) max = b.getScore();
        return max;
    }

    public String getWindowTitle() {
        String mode  = Config.TEXTURED_MODE ? "[TEXT]" : "[GEOM]";
        String mMute = Config.MUTE_MUSIC    ? "[MUTE-M]" : "";
        String sMute = Config.MUTE_SFX      ? "[MUTE-S]" : "";
        String t = String.format("%s%s%s Nivel: %d | P1: %d | P2: %d | P3: %d",
            mode, mMute, sMute, nivel, getScore(0), getScore(1), getScore(2));
        if (isMenu()) t += " | ESPACIO/W/I: JUGAR | T: TEXTURA | M: MUSICA | S: SFX";
        else if (isGameOver()) t += " | GAME OVER - R: REINICIAR";
        return t;
    }

    private void resetBirds() {
        for (Bird b : getChildren(Bird.class)) b._ready();
    }

    private void clearPipes() {
        for (Pipe p : getChildren(Pipe.class)) {
            p.queueFree();
            removeChild(p);
        }
    }

    private void clearParticles() {
        ParticleSystem ps = getChild(ParticleSystem.class);
        if (ps != null) ps.clear();
    }
}
