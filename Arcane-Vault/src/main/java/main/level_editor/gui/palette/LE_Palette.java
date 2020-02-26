package main.level_editor.gui.palette;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.TabbedPanel;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;

import java.util.List;

public class LE_Palette extends TabbedPanel {

    public void init(){


//    ScrollPanel scrollPanel = new ScrollPanel(); //for tabs?
}

    protected int getWrap() {
        return 30;
    }

    @Override
    public void addTab(Actor actor, String tabName) {
        super.addTab(actor, tabName);
    }
}
