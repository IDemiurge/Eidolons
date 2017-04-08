package main.libgdx.gui;

import com.badlogic.gdx.graphics.g2d.NinePatch;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class NinePathFactory {

    public static NinePatch getTooltip() {
        return new NinePatch(getOrCreateR("UI/components/tooltip_background.png"), 16, 16, 14, 14);
    }
}
