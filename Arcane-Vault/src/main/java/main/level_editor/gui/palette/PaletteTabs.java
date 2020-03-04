package main.level_editor.gui.palette;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;

import java.util.*;

public class PaletteTabs extends TabbedPane implements TabbedPaneListener {

    private static final String GROUP_PREF = "---";
    private final PaletteTypesTable holder;
    private final int groupTabsEnd;
    Map<LE_Tab, List<LE_Tab>> tabMap = new LinkedHashMap<>();

    public PaletteTabs(PaletteTypesTable holder, OBJ_TYPE TYPE) {
        this.holder = holder;
        addListener(this);
        getTable().setWidth(1200);

        List<String> tabs = DataManager.getTabGroups(TYPE);
        groupTabsEnd = tabs.size();
        for (String tabName : tabs) {
            addTab(  new LE_Tab(StringMaster.wrap(GROUP_PREF, tabName), null), getLastTabIndex());
        }
        getTable().row();
        for (Tab t : getTabs()) {
            LE_Tab tab = (LE_Tab) t;
            List<LE_Tab>  groupTabs = new ArrayList<>();

            Set<String> tabNames = DataManager.getSubGroups( tab.getTabTitle().substring(GROUP_PREF.length()));
            for (String subGroup : tabNames) {
            List<ObjType> types = DataManager.getTypesSubGroup(TYPE, subGroup);
            groupTabs.add(new LE_Tab(subGroup, types));
            tabMap.put(tab, groupTabs);
            }

        }
}

    @Override
    public TabbedPaneTable getTable() {
        return super.getTable();
    }

    @Override
    public void switchedTab(Tab tab) {
        if (tab instanceof LE_Tab) {
            if (tab.getTabTitle().contains(GROUP_PREF)) {
                List<LE_Tab> newTabs = tabMap.get(tab);
                getTabs().removeRange(groupTabsEnd, getLastTabIndex());
                for (LE_Tab newTab : newTabs) {
                    addTab(newTab, getLastTabIndex());
                }
                switchTab(newTabs.get(0));
            } else
                holder.setUserObject(((LE_Tab) tab).getTypes());
        }
    }

    private int getLastTabIndex() {
        return getTabs().size ;
    }

    @Override
    public void removedTab(Tab tab) {

    }

    @Override
    public void removedAllTabs() {

    }


public class LE_Tab extends Tab {


    private String name;
    List<ObjType> types;

    public LE_Tab(String name, List<ObjType> types) {
        this.name = name;
        this.types = types;
    }

    @Override
    public boolean isCloseableByUser() {
        return false;
    }

    public List<ObjType> getTypes() {
        return types;
    }

    @Override
    public String getTabTitle() {
        return name;
    }

    @Override
    public Table getContentTable() {
        return null;
    }
}
}
