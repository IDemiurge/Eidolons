package eidolons.libgdx.gui;

import eidolons.libgdx.GdxMaster;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;

/**
 * Created by JustMe on 12/4/2017.
 */
public class UiMaster {

    private static Float speedFactor;

    public static float getSpeedFactor() {
        if (speedFactor == null) {
            speedFactor =
             new Float(100) / OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.SPEED)
            ;
        }
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
        return (int) (32 * new Float((((int) (GdxMaster.getFontSizeMod() * 100)) / 10)) / 10);
    }

    public static int getIconSize() {
        return (int) (64 * new Float((((int) (GdxMaster.getFontSizeMod() * 100)) / 10)) / 10);
    }

    // use  String.format("%.1f", x);?
    public static int getSpellIconSize() {
        return (int) (80 * new Float((((int) (GdxMaster.getFontSizeMod() * 100)) / 10)) / 10);
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
