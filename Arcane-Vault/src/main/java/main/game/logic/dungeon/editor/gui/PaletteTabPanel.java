package main.game.logic.dungeon.editor.gui;

import main.client.cc.gui.neo.tabs.HC_Tab;
import main.client.cc.gui.neo.tabs.HC_TabComp;
import main.client.cc.gui.neo.tabs.HC_TabPanel;
import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.logic.dungeon.editor.gui.LE_Palette.UPPER_PALETTE;
import main.swing.generic.components.ComponentVisuals;
import main.system.entity.FilterMaster;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;
import main.utilities.workspace.Workspace;

import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PaletteTabPanel extends HC_TabPanel {

    public PaletteTabPanel(UPPER_PALETTE p) {
        if (p.TYPES != null) {
            for (OBJ_TYPE t : p.TYPES) {
                List<ObjType> types = DataManager.getTypes(t);
                PagedPaletteTab sub = new PagedPaletteTab(new Workspace(t.getName(), types));
                addTab(t.getName(), "", sub);
            }
        } else {

            OBJ_TYPE type = p.getTYPE();
            List<ObjType> types = DataManager.getTypes(type);
            DequeImpl<ObjType> deque = new DequeImpl<>();
            if (p.filterProp == null) {
                deque.addAll(types);
            } else {
                deque.addAllCast(FilterMaster.filterByProp(new LinkedList<>(types), p.filterProp
                        .getName(), p.filterValue));
            }

            for (String s : StringMaster.openContainer(p.subPalettes, ", ")) {
                List<ObjType> typeList = p.upper ? DataManager.getTypesGroup(type, StringMaster
                        .getWellFormattedString(s)) : DataManager.getTypesSubGroup(type,
                        StringMaster.getWellFormattedString(s));
                if (typeList.isEmpty()) {
                    DequeImpl<ObjType> subdeque = new DequeImpl<>();
                    String name = p.groupProp.getName();
                    Collection<?> list = FilterMaster
                            .filterByProp(new LinkedList<>(deque), name, s);
                    subdeque.addAllCast(list);
                    typeList = new LinkedList<>(subdeque);
                }

                PagedPaletteTab sub = new PagedPaletteTab(new Workspace(s, typeList));
                addTab(s, "", sub);
            }
        }
    }

    @Override
    public ComponentVisuals getTAB() {
        return VISUALS.TAB_SMALL;
    }

    @Override
    public ComponentVisuals getTAB_SELECTED() {
        return VISUALS.TAB_SMALL_SELECTED;
    }

    @Override
    public int getPageSize() {
        return 20;
    }

    protected String getTabsOffsetX() {
        return "42";
    }

    @Override
    protected String getCompOffsetY() {
        if (getCurrentComp() instanceof PagedPaletteTab) {
            if (((PagedPaletteTab) getCurrentComp()).getWs().getTypeList().size() > PagedPaletteTab.PAGE_SIZE) {
                return "(-16)";
            }
        }
        return super.getCompOffsetX();
    }

    @Override
    public Component generateTabComp(HC_Tab tab) {
        HC_TabComp tabComp = (HC_TabComp) super.generateTabComp(tab);
        tabComp.setFONT_SIZE(15);
        tabComp.setFONT_SIZE_SELECTED(16);
        return tabComp;
    }

}
