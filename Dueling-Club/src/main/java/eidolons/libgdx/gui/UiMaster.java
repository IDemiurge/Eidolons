package eidolons.libgdx.gui;

import eidolons.libgdx.GdxMaster;

/**
 * Created by JustMe on 12/4/2017.
 */
public class UiMaster {

    private static Float speedFactor= 0.7f;

    public static float getSpeedFactor() {
//        if (speedFactor == null) {
//       TODO another option value for it
//        speedFactor =new Float(100)
//             / OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.SPEED)            ;
//        }
        return speedFactor;
    }

    public static float getDuration(UI_ACTIONS action) {
        float v = 0;
        switch (action) {
            case SCALE_ACTION_ICON:
                v = 0.7f;
                break;
        }
        return v + v * (getSpeedFactor() - 1) * action.sensitivity;
    }

    public static int getSmallIconSize() {
        if (!isIconsScaled())
            return 32;
        return (int) (32 * new Float((((int) (GdxMaster.getFontSizeMod() * 100)) / 10)) / 10);
    }

    public static int getIconSize() {
        return getIconSize(false);
    }

    // use  String.format("%.1f", x);?
    public static int getSpellIconSize() {
        if (!isIconsScaled())
                return 80;
        return (int) (80 * new Float((((int) (GdxMaster.getFontSizeMod() * 100)) / 10)) / 10);
    }

    public static int getIconSize(boolean smaller) {
        if (!isIconsScaled())
        if (smaller)
            return 42;
       else
           return 64;

        if (smaller)
            return (int) (42 * new Float((((int) (GdxMaster.getFontSizeMod() * 100)) / 10)) / 10);
        return (int) (64 * new Float((((int) (GdxMaster.getFontSizeMod() * 100)) / 10)) / 10);
    }

    private static boolean isIconsScaled() {
        return false;
    }

    public static int getBottomQuickItemIconSize() {
        return getIconSize(false);
    }
    public static int getBottomSpellIconSize() {
        return getIconSize(false);
    }

    public static int getHqSpellIconSize() {
        return 64;
    }

    public static String getSprite(String imagePath) {
        return imagePath.replace("icons", "sprites");
    }

    public enum UI_ACTIONS {
        SCALE_ACTION_ICON(0),
        SCALE_UNIT_VIEW(0),
        MOVE_QUEUE_VIEW(0),
        OPEN_RADIAL(0),;

        float sensitivity; //coef of speed option

        UI_ACTIONS(float sensitivity) {
            this.sensitivity = sensitivity;
        }
    }
}
