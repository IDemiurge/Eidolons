package eidolons.libgdx.texture;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Textures {

    public static TextureRegion BLACK;
    public static final String BLACK_PATH = "ui/black.png";
    public static TextureRegion HOR_GRADIENT_72;
    public static String HOR_GRADIENT_72_PATH = "ui/hor_gradient72.png";


    public static void init() {
        HOR_GRADIENT_72 = TextureCache.getOrCreateR(HOR_GRADIENT_72_PATH);
        BLACK = TextureCache.getOrCreateR(BLACK_PATH);
    }
}
