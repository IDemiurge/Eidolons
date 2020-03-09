package main.level_editor.gui.palette;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import eidolons.libgdx.StyleHolder;
import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;

import java.util.*;

public class PaletteTabs extends TabbedPane implements TabbedPaneListener {

    private static final String GROUP_PREF = "---";
    private final PaletteTypesTable holder;
    private final int groupTabsEnd;
    private final List<LE_Tab> currentTabs;
    Map<LE_Tab, List<LE_Tab>> tabMap = new LinkedHashMap<>();
    private Tab lastGroupTab;

    public PaletteTabs(PaletteTypesTable holder, OBJ_TYPE TYPE) {
        super(StyleHolder.getHorTabStyle());
        this.holder = holder;
        addListener(this);
        getTable().setWidth(1200);
        getTabsPane().setWidth(600);
        List<String> tabs = DataManager.getTabGroups(TYPE);
        groupTabsEnd = tabs.size();
        for (String tabName : tabs) {
            LE_Tab tab;
            getTabs().add(tab = new LE_Tab(StringMaster.wrap(GROUP_PREF, tabName), null));
            addTab(tab, 0);
        }
        for (Tab t : getTabs()) {
            LE_Tab tab = (LE_Tab) t;
            List<LE_Tab> groupTabs = new ArrayList<>();

            Set<String> tabNames = DataManager.getSubGroups(tab.getTabTitle().substring(GROUP_PREF.length(), tab.getTabTitle().length() - GROUP_PREF.length()));
            for (String subGroup : tabNames) {
                List<ObjType> types = DataManager.getTypesSubGroup(TYPE, subGroup);
                groupTabs.add(new LE_Tab(subGroup, types));
            }
            tabMap.put(tab, groupTabs);

        }
        currentTabs = new ArrayList<>();
    }

    @Override
    public TabbedPaneTable getTable() {
        return super.getTable();
    }

    @Override
    public void switchedTab(Tab tab) {

        if (tab instanceof LE_Tab) {
            if (tab.getTabTitle().contains(GROUP_PREF)) {
                if (lastGroupTab != null) {
                    for (Tab subTab : currentTabs){// tabMap.get(lastGroupTab)) {
                        try {
                            remove(subTab);
                            getTabs().removeValue(subTab, true);
                            main.system.auxiliary.log.LogMaster.log(1,">>> Removed tab: " +subTab.getTabTitle());
                        } catch (Exception e) {
                            main.system.ExceptionMaster.printStackTrace(e);
                        }
                    }
                    currentTabs.clear();
                }
                List<LE_Tab> newTabs = tabMap.get(tab);
                for (LE_Tab newTab : newTabs) {
                    addTab(newTab, groupTabsEnd);
                    getTabs().add(newTab);
                    currentTabs.add(newTab);
                    main.system.auxiliary.log.LogMaster.log(1,">>> Added tab: " +newTab.getTabTitle());
                }
//                switchTab(newTabs.get(0));
                lastGroupTab = tab;
            } else
                holder.setUserObject(((LE_Tab) tab).getTypes());
        }
    }

    @Override
    protected void addTab(Tab tab, int index) {
        super.addTab(tab, index);
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
