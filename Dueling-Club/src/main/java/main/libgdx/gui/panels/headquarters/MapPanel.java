package main.libgdx.gui.panels.headquarters;


import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.gui.panels.UpdatableGroup;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;

import java.util.List;

public class MapPanel extends UpdatableGroup {
    private Image mapImage;
    private List<ActionValueContainer> markers;

    public MapPanel() {

    }

    @Override
    public void updateAct(float delta) {
        //update map here from userobject datasource
    }
}
