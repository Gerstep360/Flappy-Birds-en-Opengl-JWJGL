package com.graphics.game;

// Panel de control del juego (constantes y opciones runtime).
public class Config {

    // ========== VENTANA ==========
    public static final int VENTANA_ANCHO     = 900;
    public static final int VENTANA_ALTO      = 700;
    public static final String VENTANA_TITULO = "Flappy Bird OpenGL";

    // ========== RUTAS ==========
    public static final String PATH_SPRITES = "src/main/resources/assets/sprites/";
    public static final String PATH_SOUNDS  = "src/main/resources/assets/sounds/";

    // ========== OPCIONES (se cambian en runtime) ==========
    public static boolean TEXTURED_MODE = true;
    public static boolean MUTE_MUSIC    = false;
    public static boolean MUTE_SFX      = false;

    // ========== PAJAROS ==========
    public static final float   BIRD1_X     = -0.55f;
    public static final float[] BIRD1_COLOR = {0.98f, 0.85f, 0.20f};
    public static final float   BIRD2_X     = -0.35f;
    public static final float[] BIRD2_COLOR = {0.30f, 0.60f, 1.00f};
    public static final float   BIRD3_X     = -0.15f;
    public static final float[] BIRD3_COLOR = {1.00f, 0.20f, 0.20f};

    // ========== TUBERIAS ==========
    public static final float PIPE_ANCHO          = 0.18f;
    public static final float PIPE_GAP_ALTO_BASE  = 0.52f;
    public static final float PIPE_GAP_ALTO_MIN   = 0.32f;
    public static final float PIPE_GAP_K          = 0.015f;
    public static final float PIPE_VELOCIDAD_BASE = 0.45f;
    public static final float PIPE_VELOCIDAD_MAX  = 0.85f;
    public static final float PIPE_VELOCIDAD_K    = 0.012f;
    public static final float PIPE_SPAWN_BASE     = 2.3f;
    public static final float PIPE_SPAWN_MIN      = 1.3f;
    public static final float PIPE_SPAWN_K        = 0.01f;
    public static final float PIPE_GAP_MIN_Y      = -0.45f;
    public static final float PIPE_GAP_MAX_Y      = 0.45f;
    public static final float[] PIPE_COLOR        = {0.18f, 0.70f, 0.25f};

    // ========== NIVELES ==========
    public static final int SCORE_PER_LEVEL       = 5;
    public static final int SCORE_CYCLE_DAY_NIGHT = 5;
    public static final float GAMEOVER_RESTART_DELAY = 1.5f;
    public static boolean TERMINAR_PUNTAJE        = false;
    public static final int PUNTAJE_TERMINAR      = 1;

    // ========== MUNDO ==========
    public static final float[] COLOR_CIELO   = {0.52f, 0.80f, 0.92f};
    public static final float[] COLOR_OVERLAY = {0.15f, 0.18f, 0.22f};
    public static final float GROUND_Y        = -0.72f;

    // ========== Z-INDEX (profundidad) ==========
    public static final float Z_BACKGROUND = 0.9f;
    public static final float Z_PIPES      = 0.5f;
    public static final float Z_PARTICLES  = 0.2f;
    public static final float Z_BASE       = 0.0f;
    public static final float Z_BIRD       = -0.2f;
    public static final float Z_HUD        = -0.5f;
    public static final float Z_OVERLAY    = -0.8f;
}
