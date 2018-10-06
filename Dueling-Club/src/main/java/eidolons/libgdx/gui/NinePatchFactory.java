package eidolons.libgdx.gui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class NinePatchFactory {

    private static final String LIGHT_DECOR_PANEL_PATH =
     StrPathBuilder.build(PathFinder.getComponentsPath(),
      "ninepatch", "std", "light ninepatch decor.png");
    private static final String LIGHT_DECOR_FILLED_PANEL_PATH =
     StrPathBuilder.build(PathFinder.getComponentsPath(),
      "ninepatch", "std", "light ninepatch decor filled.png");
    private static final String LIGHT_PANEL_PATH =
     StrPathBuilder.build(PathFinder.getComponentsPath(),
      "ninepatch", "std", "light ninepatch.png");
    private static final String LIGHT_PANEL_FILLED_PATH =
     StrPathBuilder.build(PathFinder.getComponentsPath(),
      "ninepatch", "std", "light ninepatch FILLED.png");

    private static final String LIGHT_PANEL_HQ_PATH =
     StrPathBuilder.build(PathFinder.getComponentsPath(),
      "ninepatch", "std", "light ninepatch hq.png");

    private static final String LIGHT_PANEL_HQ_EMPTY_PATH =
     StrPathBuilder.build(PathFinder.getComponentsPath(),
      "ninepatch", "std", "light ninepatch hq empty.png");

    private static final String LIGHT_PANEL_FILLED_SMALL_PATH =
     StrPathBuilder.build(PathFinder.getComponentsPath(),
      "ninepatch", "std", "light ninepatch filled small.png");

    public static NinePatch getTooltip() {
        return getLightPanelFilled();
//        return new NinePatch(getOrCreateR("UI/components/tooltip_background.png"), 16, 16, 14, 14);
    }

    public static NinePatch get3pxBorder() {
        return new NinePatch(getOrCreateR("UI/components/background_3px_border.png"), 3, 3, 3, 3);
    }

    public static NinePatch getMainMenuFrame() {
        return new NinePatch(getOrCreateR("UI/components/MainMenu ninepatch.png"), 120, 120, 146, 128);
    }

    public static NinePatch getHqEmpty() {
        return new NinePatch(getOrCreateR(
         LIGHT_PANEL_HQ_EMPTY_PATH), 10, 10, 10, 10);
    }
    public static NinePatchDrawable getHqEmptyDrawable() {
        return new NinePatchDrawable(new NinePatch(getOrCreateR(
         LIGHT_PANEL_HQ_EMPTY_PATH), 10, 10, 10, 10));
    }

    public static NinePatch getHq() {
        return new NinePatch(getOrCreateR(
         LIGHT_PANEL_HQ_PATH), 10, 10, 10, 10);
    }
    public static NinePatchDrawable getHqDrawable() {
        return new NinePatchDrawable(new NinePatch(getOrCreateR(
         LIGHT_PANEL_HQ_PATH), 10, 10, 10, 10));
    }
    public static NinePatch getInfoPanel() {
        return new NinePatch(getOrCreateR("UI/components/panel ninepatch.png"), 50, 50, 50, 50);
    }
    public static NinePatch getLightPanelFilledSmall() {
        return new NinePatch(getOrCreateR(
         LIGHT_PANEL_FILLED_SMALL_PATH), 5, 5, 5, 5);
    }
    public static NinePatch getLightPanelFilled() {
        return new NinePatch(getOrCreateR(
         LIGHT_PANEL_FILLED_PATH), 10, 10, 10, 10);
    }
    public static NinePatch getLightPanel() {
        return new NinePatch(getOrCreateR(
         LIGHT_PANEL_PATH), 10, 10, 10, 10);
    }
    public static NinePatchDrawable getLightPanelFilledDrawable() {
        return new NinePatchDrawable(getLightPanelFilled());
    }
    public static NinePatchDrawable getLightDecorPanelFilledDrawable() {
        return new NinePatchDrawable(getLightDecorPanelFilled());
    }

    private static NinePatch getLightDecorPanel() {
        return new NinePatch(getOrCreateR(
         LIGHT_DECOR_PANEL_PATH), 10, 10, 10, 10);
    }

    private static NinePatch getLightDecorPanelFilled() {
        return new NinePatch(getOrCreateR(
         LIGHT_DECOR_FILLED_PANEL_PATH), 10, 10, 10, 10);
    }
    public static NinePatchDrawable getLightPanelDrawable() {
        return new NinePatchDrawable(getLightPanel());
    }
}
