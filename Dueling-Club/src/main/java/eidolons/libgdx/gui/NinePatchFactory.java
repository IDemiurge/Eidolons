package eidolons.libgdx.gui;

import com.badlogic.gdx.graphics.g2d.NinePatch;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class NinePatchFactory {

    public static NinePatch getTooltip() {
        return new NinePatch(getOrCreateR("UI/components/tooltip_background.png"), 16, 16, 14, 14);
    }

    public static NinePatch get3pxBorder() {
        return new NinePatch(getOrCreateR("UI/components/background_3px_border.png"), 3, 3, 3, 3);
    }

    public static NinePatch getMainMenuFrame() {
        return new NinePatch(getOrCreateR("UI/components/MainMenu ninepatch.png"), 120, 120, 146, 128);
    }

    public static NinePatch getInfoPanel() {
        return new NinePatch(getOrCreateR("UI/components/panel ninepatch.png"), 50, 50, 50, 50);
    }
    public static NinePatch getLightPanel() {
        return new NinePatch(getOrCreateR("light ninepatch.png"), 10, 10, 10, 10);
    }
}
