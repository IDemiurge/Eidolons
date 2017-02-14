package main.client.cc.gui.lists;

import main.client.cc.CharacterCreator;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.system.graphics.GuiManager;

import java.util.List;

public class TabListPanel extends HeroListPanel {

    public static final int TAB_LIST_SLOT_COUNT = CharacterCreator.STD_COLUMN_NUMBER;
    public static final int TAB_LIST_WRAP = 2;

    public TabListPanel(String title, DC_HeroObj hero, boolean responsive,
                        boolean v, int rowCount, int size, List<ObjType> data) {
        super(title, hero, responsive, v, rowCount, size, data);
    }

    public TabListPanel(String title, boolean responsive, boolean v,
                        int rowCount, DC_HeroObj hero, List<ObjType> data) {
        this(title, hero, responsive, v, rowCount,
                GuiManager.getSmallObjSize(), data);
    }

    // public ListItem<ObjType> getDefaulListComp(ObjType value, boolean
    // isSelected, boolean cellHasFocus, int obj_size) {
    // return new SpellListItem(item, isSelected, cellHasFocus);
    // }

    public int getListWrap() {
        return TAB_LIST_WRAP;
    }

    public int getListSlotCount() {
        return TAB_LIST_SLOT_COUNT;
    }
}
