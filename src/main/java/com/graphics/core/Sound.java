package com.graphics.core;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

// Reproductor WAV simple (no streaming).
public class Sound {

    private final Clip clip;
    private long lastPlayTime = 0;

    public Sound(String path) {
        clip = loadClip(path);
    }

    private Clip loadClip(String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.err.println("Sonido no encontrado: " + path);
            return null;
        }
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            Clip newClip = AudioSystem.getClip();
            newClip.open(ais);
            return newClip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void play() {
        if (clip == null) return;
        long now = System.currentTimeMillis();
        if (now - lastPlayTime < 50) {
            return;
        }
        lastPlayTime = now;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void loop() {
        if (clip == null) return;
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip != null) clip.stop();
    }
}
