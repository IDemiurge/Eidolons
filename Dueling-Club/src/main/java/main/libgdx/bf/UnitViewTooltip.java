package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import main.libgdx.gui.tooltips.ValueTooltip;

public class UnitViewTooltip extends ValueTooltip {

    @Override
    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        updateRequired = true;
        super.onMouseEnter(event, x, y, pointer, fromActor);
    }
}
