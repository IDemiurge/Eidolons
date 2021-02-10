package eidolons.libgdx.texture;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;

public class Textures {

    public static final String BOTTOM_PANEL_BG = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "background.png");
    public static final String BOTTOM_PANEL_BG_ALT = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "background alt.png");
    public static final String BOTTOM_OVERLAY = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "bottom overlay.png");
    public static final String HP_BAR_BG = StrPathBuilder.build("ui", "components",
            "dc", "unit", "hp bar empty.png");
    public static TextureRegion BLACK;
    public static final String BLACK_PATH = "ui/black.png";
    public static TextureRegion HOR_GRADIENT_72;
    public static String HOR_GRADIENT_72_PATH = "ui/hor_gradient72.png";


    public static void init() {
        HOR_GRADIENT_72 = TextureCache.getOrCreateR(HOR_GRADIENT_72_PATH);
        BLACK = TextureCache.getOrCreateR(BLACK_PATH);
    }
}
