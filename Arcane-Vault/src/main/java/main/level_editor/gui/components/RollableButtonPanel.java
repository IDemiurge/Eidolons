package main.level_editor.gui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.gui.RollDecorator;
import main.game.bf.directions.FACING_DIRECTION;

public class RollableButtonPanel extends RollDecorator.RollableGroup {


    public RollableButtonPanel(Actor contents, FACING_DIRECTION direction) {
        super(contents, direction);
    }
}
