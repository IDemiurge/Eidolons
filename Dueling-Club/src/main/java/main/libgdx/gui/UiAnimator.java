package main.libgdx.gui;

import main.system.options.AnimationOptions.ANIMATION_OPTION;
import main.system.options.OptionsMaster;

/**
 * Created by JustMe on 12/4/2017.
 */
public class UiAnimator {

    private static Float speedFactor;

    public static float getSpeedFactor() {
        if (speedFactor == null) {
            speedFactor =
             new Float(100)/ OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.SPEED)
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
        return v +v* (getSpeedFactor()-1)*action.sensitivity;
    }

    public static float getSmallIconSize() {
//        GraphicsManager
        return 32;
    }

    public static float getIconSize() {
        return 64;
    }

    public enum UI_ACTIONS {
        SCALE_ACTION_ICON(0),
        SCALE_UNIT_VIEW(0),
        MOVE_QUEUE_VIEW(0),
        OPEN_RADIAL(0),

        ;

        UI_ACTIONS(float sensitivity) {
            this.sensitivity = sensitivity;
        }

        float sensitivity; //coef of speed option
    }
}