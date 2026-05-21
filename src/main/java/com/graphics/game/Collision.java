package com.graphics.game;

// Colisiones simples AABB.
public class Collision {

    // Verifica si dos rectangulos (centro, ancho, alto) se solapan.
    public static boolean checkAABB(float x1, float y1, float w1, float h1,
                                    float x2, float y2, float w2, float h2) {
        return (x1 + w1/2 > x2 - w2/2) && (x1 - w1/2 < x2 + w2/2)
            && (y1 + h1/2 > y2 - h2/2) && (y1 - h1/2 < y2 + h2/2);
    }

    // Verifica si un pajaro toca alguna de las dos tuberias (fuera del hueco).
    public static boolean checkBirdPipe(Bird bird, Pipe pipe) {
        float bL = bird.getLeft(),  bR = bird.getRight();
        float pL = pipe.getX() - Config.PIPE_ANCHO / 2;
        float pR = pipe.getX() + Config.PIPE_ANCHO / 2;

        // Sin traslape horizontal = sin colision.
        if (bR <= pL || bL >= pR) return false;

        // Hay traslape horizontal: verificar si esta fuera del hueco.
        float bT = bird.getTop(), bB = bird.getBottom();
        float gT = pipe.getGapCentroY() + pipe.getGapHeight() / 2;
        float gB = pipe.getGapCentroY() - pipe.getGapHeight() / 2;

        return bT > gT || bB < gB;
    }
}
