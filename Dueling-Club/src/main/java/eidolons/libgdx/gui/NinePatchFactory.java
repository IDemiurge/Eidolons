package eidolons.libgdx.gui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class NinePatchFactory {

    private static final String HIGHLIGHT_PATH =
            StrPathBuilder.build(PathFinder.getComponentsPath(),
                    "ninepatch", "std", "hl.png");
    private static final String HIGHLIGHT_SMALL_PATH =
            StrPathBuilder.build(PathFinder.getComponentsPath(),
                    "ninepatch", "std", "hl 64.png");
    private static final String ZARK_FRAME_PATH =
            StrPathBuilder.build(PathFinder.getComponentsPath(),
                    "ninepatch", "std", "frame.png");
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
    private static final java.lang.String SCROLL = "ui/components/generic/scroll/scroll.png";
    private static final java.lang.String SCROLL_KNOB_H = "ui/components/generic/scroll/scroll_knob h.png";
    private static final java.lang.String SCROLL_H = "ui/components/generic/scroll/scroll h.png";
    private static final java.lang.String SCROLL_KNOB = "ui/components/generic/scroll/scroll_knob.png";

    public static NinePatch getTooltip() {
        return getLightPanelFilled();
//        return new NinePatch(getOrCreateR("ui/components/tooltip_background.png"), 16, 16, 14, 14);
    }

    public static NinePatch get3pxBorder() {
        return new NinePatch(getOrCreateR(
                StrPathBuilder.build(PathFinder.getComponentsPath(),
                        "ninepatch", "std", "background_3px_border.png")), 3, 3, 3, 3);
    }

    public static NinePatch getMainMenuFrame() {
        return new NinePatch(getOrCreateR("ui/components/MainMenu ninepatch.png"), 120, 120, 146, 128);
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
        return new NinePatch(getOrCreateR("ui/components/panel ninepatch.png"), 50, 50, 50, 50);
    }

    public static NinePatch getLightPanelFilledSmall() {
        return new NinePatch(getOrCreateR(
                LIGHT_PANEL_FILLED_SMALL_PATH), 5, 5, 5, 5);
    }

    public static NinePatchDrawable getLightPanelFilledSmallDrawable() {
        return new NinePatchDrawable(getLightPanelFilledSmall());
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

    public static NinePatchDrawable getLightDecorPanelFilledDrawableNoMinSize() {
        return getDrawableNoMinSize(getLightDecorPanelFilled());
    }

    public static NinePatchDrawable getDrawableNoMinSize(NinePatch patch) {
        return new NinePatchDrawable(patch) {
            @Override
            public float getMinWidth() {
                return 0;
            }

            @Override
            public float getMinHeight() {
                return 0;
            }
        };
    }

    public static NinePatchDrawable getHighlightDrawable() {
        return new NinePatchDrawable(getHighlight());
    }

    private static NinePatch getZarkFrame() {
        return new NinePatch(getOrCreateR(
                ZARK_FRAME_PATH), 104, 104, 92, 92);
    }

    private static NinePatch getHighlight() {
        return new NinePatch(getOrCreateR(
                HIGHLIGHT_PATH), 10, 10, 10, 10);
    }

    private static NinePatch getHighlightSmall() {
        return new NinePatch(getOrCreateR(
                HIGHLIGHT_SMALL_PATH), 5, 5, 5, 5);
    }

    public static NinePatchDrawable getHighlightSmallDrawable() {
        return new NinePatchDrawable(getHighlightSmall());
    }

    public static NinePatchDrawable getLightDecorPanelDrawable() {
        return new NinePatchDrawable(getLightDecorPanel());
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

    public static NinePatchDrawable getScrollH() {
        return new NinePatchDrawable(new NinePatch(getOrCreateR(
                SCROLL_H), 0, 0, 30, 30));
    }

    public static NinePatchDrawable getScrollKnobH() {
        return new NinePatchDrawable(new NinePatch(getOrCreateR(
                SCROLL_KNOB_H), 0, 0, 0, 0));
    }

    public static NinePatchDrawable getScrollV() {
        return new NinePatchDrawable(new NinePatch(getOrCreateR(
                SCROLL), 0, 0, 30, 30));
    }

    public static NinePatchDrawable getScrollKnobV() {
        return new NinePatchDrawable(new NinePatch(getOrCreateR(
                SCROLL_KNOB), 0, 0, 0, 0));
    }

}
