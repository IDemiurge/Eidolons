package main.swing.components.obj.drawing;

import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlefield.VisionManager;
import main.swing.components.obj.CellComp;
import main.system.graphics.FontMaster;
import main.system.text.SmartText;

import java.awt.*;

public class DrawHelper {
    public static final int PLAYER_EMBLEM_SIZE_PERC = 32;
    public static final int UNIT_EMBLEM_SIZE_PERC = 28;
    public static final int AP_ICON_X = 0;
    public static final int AP_ICON_Y = 0;
    public static final int AP_ICON_SIZE = 0;
    public static final int BORDER_WIDTH = 5;
    public static final int BORDER_HEIGHT = 5;
    public static final int OVERLAYING_OBJ_SIZE = 68;
    public static final int OVERLAYING_OBJ_SIZE_MULTI = 54;
    public static int COUNTER_POINTS_Y;
    public static int COUNTER_POINTS_X;
    public static int ACTIVE_POINTS_Y;
    public static Dimension ACTIVE_IMG_DIMENSION;
    public static Image ACTION_POINTS;
    public static Image COUNTER_POINTS;
    private static DrawMaster master;

    public static boolean isStackedPaintZoom(int zoom) {
        master.setZoom(zoom);
        return master.isStackedPaintZoom();
    }

    public static boolean isAnimationPaintZoom(int zoom) {
        master.setZoom(zoom);
        return master.isAnimationPaintZoom();
    }

    public static boolean isTextPaintZoom(int zoom) {
        master.setZoom(zoom);
        return master.isTextPaintZoom();
    }

    public static boolean isFacingPaintZoom(int zoom) {
        master.setZoom(zoom);
        return master.isFacingPaintZoom();
    }

    public static boolean isFramePaintZoom(int zoom) {
        master.setZoom(zoom);
        return master.isFramePaintZoom();
    }

    public static boolean isMinimapZoom(int zoom) {
        master.setZoom(zoom);
        return master.isMinimapZoom();
    }

    public static boolean isStackedIconMode(int zoom) {
        master.setZoom(zoom);
        return master.isStackedIconMode();
    }

    public static void init() {
        master = new DrawMaster();
        // ACTION_POINTS =
        // ImageManager.getSizedVersion(STD_IMAGES.ACTIONS.getImage(),
        // DrawHelper.ACTIVE_IMG_DIMENSION);
        // COUNTER_POINTS =
        // ImageManager.getSizedVersion(STD_IMAGES.COUNTERS.getImage(),
        // DrawHelper.ACTIVE_IMG_DIMENSION);
        // ACTIVE_POINTS_Y = (int) (DC_GuiManager.getCellSize().getHeight() -
        // ACTIVE_IMG_DIMENSION
        // .getHeight() * 3 / 2);
        // if (GuiManager.isTall())
        // ACTIVE_POINTS_Y = ACTIVE_POINTS_Y * 6 / 5 - 2;
        // COUNTER_POINTS_X = (int) (DC_GuiManager.getCellSize().getWidth() -
        // ACTIVE_IMG_DIMENSION
        // .getWidth());
        // COUNTER_POINTS_Y = (int) ACTIVE_IMG_DIMENSION.getHeight();
        // if (GuiManager.isTall())
        // COUNTER_POINTS_Y = COUNTER_POINTS_Y - 6;
    }

    public static Point getPointForDisplayedParameter(PARAMETER p, SmartText text,
                                                      Dimension compSize) {
        int x = 0;
        int y = 0;
        if (p == PARAMS.TOUGHNESS) {
            x = (int) (compSize.getWidth() - FontMaster.getStringWidth(text.getFont(), text
                    .getText()));
            y = FontMaster.getFontHeight(text.getFont());
        }
        if (p == PARAMS.ENDURANCE) {
            x = 2;
            y = (int) (compSize.getHeight() - 2 - FontMaster.getFontHeight(text.getFont()));
        }
        return new Point(x, y);
    }

    public static Point getPointForActions(SmartText text, int width, int height) {
        int x = (width - FontMaster.getStringWidth(text.getFont(), text.getText()));
        int y = height - FontMaster.getFontHeight(text.getFont()) / 2;
        return new Point(x, y);
    }

    public static Point getPointForCounters(int size, SmartText text, int width, int height) {
        int x = (width - FontMaster.getStringWidth(text.getFont(), text.getText()));
        int y = size - FontMaster.getFontHeight(text.getFont());
        return new Point(x, y);
    }

    public static boolean isFacingDrawn(CellComp cellComp, Obj obj) {
        if (obj instanceof Unit) {
            Unit unit = (Unit) obj;
            if (!VisionManager.checkDetectedEnemy((DC_Obj) obj)) {
                return false;
            }
            if (unit.isBfObj()) {
                return false;
            }
            return true;
            // !unit.checkProperty(G_PROPS.BF_OBJECT_TAGS, "" +
            // BF_OBJECT_TAGS.ASSYMETRICAL)
        }
        return false;
    }

}
