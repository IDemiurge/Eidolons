package eidolons.game.battlecraft.logic.battlefield.vision.colormap;

public class LightConsts {
    public static final int MIN_AMBIENT_LIGHT = 20;
    public static final float MIN_SCREEN =  0.05f;
    public static final float MIN_LIGHTNESS = 0.5f;
    public static final float PILLAR_COEF_LIGHT = 1.2f;
    public static final float PILLAR_WALL_COEF_LIGHT = 1.4f;
    public static final float PILLAR_COLOR_LERP = 0.66F;

    public static float getScreen(float light) {
        return light - 0.5f;
    }

    public static float getNegative(float light) {
        return 0.5f-light;
    }
}
