package main.game.battlefield.options;

import main.game.battlefield.options.UIOptions.UI_OPTIONS;
import main.system.net.data.DataUnit;

public class UIOptions extends DataUnit<UI_OPTIONS> {

    public boolean isShowBuffsOn() {
        return getBooleanValue(UI_OPTIONS.SHOW_BUFFS);
    }

    public int getAnimationSpeed() {
        return getIntValue(UI_OPTIONS.ANIMATION_SPEED);
    }

    public enum UI_OPTIONS {
        ANIMATION_SPEED, SHOW_BUFFS,;
    }

}
