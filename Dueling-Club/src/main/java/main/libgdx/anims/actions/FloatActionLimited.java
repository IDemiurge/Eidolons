package main.libgdx.anims.actions;

import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;

/**
 * Created by JustMe on 11/26/2017.
 */
public class FloatActionLimited extends FloatAction {


    protected void update (float percent) {
        super.update(percent);
        if (getStart()>getEnd())
        setValue(Math.min(getValue(), getEnd()));
        else
        setValue(Math.max(getValue(), getEnd()));
    }
}
