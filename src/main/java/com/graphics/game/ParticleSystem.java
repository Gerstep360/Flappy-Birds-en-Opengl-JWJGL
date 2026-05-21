package com.graphics.game;

import com.graphics.core.Node;
import com.graphics.core.Renderer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Particulas simples para feedback visual.
public class ParticleSystem extends Node {

    private static class Particle {
        float x, y, vx, vy, life;
        float[] color;
        Particle(float x, float y, float vx, float vy, float[] c) {
            this.x=x; this.y=y; this.vx=vx; this.vy=vy; this.color=c; this.life=1;
        }
    }

    private static final float LIFE_DECAY = 2f;
    private static final float BASE_SIZE = 0.02f;
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();

    @Override
    public void _process(float delta) {
        updateParticles(delta);
    }

    @Override
    public void _render(Renderer r) {
        renderParticles(r);
    }

    private void updateParticles(float delta) {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.x += p.vx * delta;
            p.y += p.vy * delta;
            p.life -= delta * LIFE_DECAY;
            if (p.life <= 0) particles.remove(i);
        }
    }

    private void renderParticles(Renderer r) {
        for (Particle p : particles) {
            float s = BASE_SIZE * p.life;
            r.drawRect(p.x, p.y, Config.Z_PARTICLES, s, s, 0, p.color[0], p.color[1], p.color[2]);
        }
    }

    public void emit(float x, float y, float[] color, int count) {
        for (int i = 0; i < count; i++) {
            float vx = (random.nextFloat()-0.5f)*0.5f;
            float vy = (random.nextFloat()-0.5f)*0.5f;
            particles.add(new Particle(x, y, vx, vy, color));
        }
    }

    public void clear() { particles.clear(); }
}
