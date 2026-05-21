package com.graphics.game;

import java.util.List;

import com.graphics.core.Node;
import com.graphics.core.Renderer;
import com.graphics.core.Texture;

// HUD: puntajes y barra de nivel.
public class HUD extends Node {

    private Texture[] digits;
    private static final float SCORE_Y = 0.85f;
    private static final float LEVEL_BAR_Y = 0.95f;

    @Override
    public void _ready() {
        digits = new Texture[10];
        for (int i = 0; i < 10; i++) digits[i] = Assets.getTexture("digit_" + i);
    }

    @Override
    public void _render(Renderer r) {
        List<Bird> birds = getBirds();
        if (birds == null) return;

        for (Bird b : birds) {
            float x;
            float[] color;
            if (b.getPlayerIndex() == 0) {
                x = -0.85f;
                color = Config.BIRD1_COLOR;
            } else if (b.getPlayerIndex() == 1) {
                String scoreStr = String.valueOf(b.getScore());
                x = -((scoreStr.length() - 1) * 0.08f) / 2.0f;
                color = Config.BIRD2_COLOR;
            } else if (b.getPlayerIndex() == 2) {
                x = 0.75f;
                color = Config.BIRD3_COLOR;
            } else {
                continue;
            }
            drawScore(r, b.getScore(), x, SCORE_Y, color);
        }

        drawLevelBar(r, getMaxScore(birds));
    }

    private List<Bird> getBirds() {
        Node parent = getParent();
        if (parent == null) return null;
        return parent.getChildren(Bird.class);
    }

    private int getMaxScore(List<Bird> birds) {
        int maxScore = 0;
        for (Bird b : birds) if (b.getScore() > maxScore) maxScore = b.getScore();
        return maxScore;
    }

    private void drawLevelBar(Renderer r, int maxScore) {
        float progress = (maxScore % Config.SCORE_PER_LEVEL) / (float) Config.SCORE_PER_LEVEL;
        r.drawRect(0, LEVEL_BAR_Y, Config.Z_HUD, 0.5f, 0.02f, 0, 0.3f, 0.3f, 0.3f);
        r.drawRect(-0.25f + progress * 0.25f, LEVEL_BAR_Y, Config.Z_HUD, progress * 0.5f, 0.02f, 0, 1, 1, 1);
    }

    private void drawScore(Renderer r, int score, float x, float y, float[] color) {
        String s = String.valueOf(score);
        for (int i = 0; i < s.length(); i++) {
            int d = Character.getNumericValue(s.charAt(i));
            if (Config.TEXTURED_MODE && digits != null && d >= 0 && d < digits.length) {
                r.drawTexture(digits[d], x + i * 0.08f, y, Config.Z_HUD, 0.07f, 0.1f, 0);
            } else {
                drawDigit7Seg(r, d, x + i * 0.08f, y, color);
            }
        }
    }

    private void drawDigit7Seg(Renderer r, int d, float x, float y, float[] c) {
        float w=0.04f, h=0.01f, sp=0.03f, z=Config.Z_HUD;
        float cr=c[0], cg=c[1], cb=c[2];
        if (d!=1&&d!=4)                     r.drawRect(x,y+sp*2,z,w,h,0,cr,cg,cb);
        if (d!=5&&d!=6)                     r.drawRect(x+w/2,y+sp,z,h,w,0,cr,cg,cb);
        if (d!=2)                           r.drawRect(x+w/2,y-sp,z,h,w,0,cr,cg,cb);
        if (d!=1&&d!=4&&d!=7)               r.drawRect(x,y-sp*2,z,w,h,0,cr,cg,cb);
        if (d==0||d==2||d==6||d==8)         r.drawRect(x-w/2,y-sp,z,h,w,0,cr,cg,cb);
        if (d!=1&&d!=2&&d!=3&&d!=7)         r.drawRect(x-w/2,y+sp,z,h,w,0,cr,cg,cb);
        if (d!=0&&d!=1&&d!=7)               r.drawRect(x,y,z,w,h,0,cr,cg,cb);
    }
}
