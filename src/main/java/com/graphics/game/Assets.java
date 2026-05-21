package com.graphics.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.graphics.core.Sound;
import com.graphics.core.Texture;

// Almacen global de texturas y sonidos.
public class Assets {

    private static final Map<String, Texture> textures = new HashMap<>();
    private static final Map<String, Sound>   sounds   = new HashMap<>();
    private static final Set<String> MUSIC_NAMES = Set.of("menu", "game", "gameover");

    public static void loadAll() {
        String spritesPath = Config.PATH_SPRITES;
        String soundsPath = Config.PATH_SOUNDS;
        loadTextures(spritesPath);
        loadSounds(soundsPath);
    }

    private static void loadTextures(String spritesPath) {
        // Fondos y UI
        tex("bg_day",     spritesPath + "Background/background-day.png");
        tex("bg_night",   spritesPath + "Background/background-night.png");
        tex("base",       spritesPath + "Base/base.png");
        tex("pipe_green", spritesPath + "Pipe/pipe-green.png");
        tex("pipe_red",   spritesPath + "Pipe/pipe-red.png");
        tex("message",    spritesPath + "Main/message.png");
        tex("gameover",   spritesPath + "fonts/gameover.png");
        for (int i = 0; i < 10; i++) tex("digit_" + i, spritesPath + "fonts/" + i + ".png");

        // Pajaros (3 frames cada uno)
        tex("bird_1P_0", spritesPath + "Birds/1P/yellowbird-downflap.png");
        tex("bird_1P_1", spritesPath + "Birds/1P/yellowbird-midflap.png");
        tex("bird_1P_2", spritesPath + "Birds/1P/yellowbird-upflap.png");
        tex("bird_2P_0", spritesPath + "Birds/2P/bluebird-downflap.png");
        tex("bird_2P_1", spritesPath + "Birds/2P/bluebird-midflap.png");
        tex("bird_2P_2", spritesPath + "Birds/2P/bluebird-upflap.png");
        tex("bird_3P_0", spritesPath + "Birds/3P/redbird-downflap.png");
        tex("bird_3P_1", spritesPath + "Birds/3P/redbird-midflap.png");
        tex("bird_3P_2", spritesPath + "Birds/3P/redbird-upflap.png");
    }

    private static void loadSounds(String soundsPath) {
        for (int i = 0; i < 3; i++) {
            snd("wing_" + i,  soundsPath + "wing.wav");
            snd("point_" + i, soundsPath + "point.wav");
            snd("hit_" + i,   soundsPath + "hit.wav");
        }
        snd("die",      soundsPath + "die.wav");
        snd("swoosh",   soundsPath + "swoosh.wav");
        snd("menu",     soundsPath + "menu.wav");
        snd("game",     soundsPath + "game.wav");
        snd("gameover", soundsPath + "gameover.wav");
    }

    public static Texture getTexture(String name) { return textures.get(name); }

    public static Texture[] getBirdFrames(String type) {
        return new Texture[] {
            getTexture("bird_" + type + "_0"),
            getTexture("bird_" + type + "_1"),
            getTexture("bird_" + type + "_2")
        };
    }

    public static void playSound(String name) {
        if (isMusic(name) && Config.MUTE_MUSIC) return;
        if (!isMusic(name) && Config.MUTE_SFX)  return;
        Sound s = sounds.get(name);
        if (s != null) s.play();
    }

    public static void loopSound(String name) {
        if (isMusic(name) && Config.MUTE_MUSIC) return;
        if (!isMusic(name) && Config.MUTE_SFX)  return;
        Sound s = sounds.get(name);
        if (s != null) s.loop();
    }

    public static void stopSound(String name) {
        Sound s = sounds.get(name);
        if (s != null) s.stop();
    }

    public static void cleanup() {}

    private static void tex(String name, String path) {
        if (!textures.containsKey(name)) textures.put(name, new Texture(path));
    }
    private static void snd(String name, String path) {
        if (!sounds.containsKey(name)) sounds.put(name, new Sound(path));
    }
    private static boolean isMusic(String n) {
        return MUSIC_NAMES.contains(n);
    }
}
