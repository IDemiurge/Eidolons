package main.level_editor.gui.palette;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.TabbedPanel;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;

import java.util.List;

public class LE_Palette extends TabbedPanel {

public void init(){

    List<ObjType> types = DataManager.getTypesGroup(DC_TYPE.UNITS, "Death");
    int wrap=getWrap();
    PaletteTable palette = new PaletteTable(wrap, types, 0);
    ScrollPanel scrollPanel = new ScrollPanel();
    scrollPanel.setActor(palette);
}

    private int getWrap() {
        return 30;
    }

    @Override
    public void addTab(Actor actor, String tabName) {
        super.addTab(actor, tabName);
    }
}
