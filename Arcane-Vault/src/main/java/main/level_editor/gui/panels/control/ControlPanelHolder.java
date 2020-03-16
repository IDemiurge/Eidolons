package main.level_editor.gui.panels.control;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.gui.panels.TablePanelX;

public class ControlPanelHolder extends TablePanelX {

    public ControlPanelHolder() {
        super(400, 800);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        clearChildren();
        add((Actor) getUserObject());
    }
}
