package main.level_editor.gui.palette;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.xml.XML_Reader;
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
        Set<String> tabNames = XML_Reader.getTreeSubGroupMap().get(group);
        for (String tabName : tabNames) {
            List<ObjType> types = DataManager.getTypesSubGroup(TYPE, tabName);
            int wrap = getWrap();
            PaletteTypesTable palette = new PaletteTypesTable(wrap, types, 0);
            addTab(palette, tabName);
        }
    }
}
