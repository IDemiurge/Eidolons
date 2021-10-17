package eidolons.game.eidolon.chain;

import eidolons.entity.obj.unit.Unit;

/**
 * Passive Delta SF Events Checks Critical events - burn up on 0 SF
 */
public class SoulforceRule {
    /*
    Where to keep SF value?
    It's purely dynamic
    would we ever apply it to non-PC? For bosses?
    maybe bind it to a unit indeed?
     */

    ChainMaster master;

    private boolean growing;
    private int value;
    private boolean transformDisplayed;
    private Unit trueForm;
    SoulforceRule pcInstance;

    public void timePassed(float seconds) {
        float delta = getFx() * seconds;
        offset(delta);
    }

    private float getFx() {
        if (growing) {
            //depends on enemy proximity?
            return 3f;
        } else {
            return -2f;
        }
    }

    private void offset(float delta) {
        value += delta;
        valueChanged();
    }

    private void toggle() {
        growing = !growing;
        transformDisplayed = false;
    }

    private void valueChanged() {
        if (growing) {
            if (value >= getTransformValue())
                if (!transformDisplayed) {
                    // showTransformDisplayed(); event
                    transformDisplayed = true;
                }
        } else {
            if (value <= getMinValue()) {
                critical();
            }
        }
    }


    private void critical() {
        toggle();
        value = getMinValue();
    }

    public void transformedBack() {
        trueForm = master.respawnTrueForm();

    }

    public void transformed() {
        toggle();
        value = getMaxValue();

    }

    private int getMinValue() {
        return 0;
    }

    private int getTransformValue() {
        return 100; //modify meta-progression
    }

    private int getMaxValue() {
        return 100;
    }

    boolean isTransformAllowed() {
        return value == getMaxValue();
    }


}
