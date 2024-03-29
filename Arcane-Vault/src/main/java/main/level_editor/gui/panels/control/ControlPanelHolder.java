package main.level_editor.gui.panels.control;

import com.badlogic.gdx.scenes.scene2d.Actor;
import libgdx.gui.dungeon.panels.TablePanelX;

public class ControlPanelHolder extends TablePanelX {

    public ControlPanelHolder() {
        super();
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        clearChildren();
        add((Actor) getUserObject());
    }
}
