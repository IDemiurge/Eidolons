package main.level_editor.gui.panels.palette;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;

import java.util.List;
import java.util.Set;

public class ObjectPalette extends LE_Palette {

    DC_TYPE TYPE;
    String group;

    public ObjectPalette(DC_TYPE TYPE, String group) {
        this.TYPE = TYPE;
        this.group = group;
        init();
    }

    public void init() {
        Set<String> tabNames = DataManager.getSubGroups( group);
        for (String tabName : tabNames) {
            List<ObjType> types = DataManager.getTypesSubGroup(TYPE, tabName);
//            PaletteTypesTable palette = new PaletteTypesTable(  types, 0);
//            addTab(palette, tabName);
        }
    }
}
