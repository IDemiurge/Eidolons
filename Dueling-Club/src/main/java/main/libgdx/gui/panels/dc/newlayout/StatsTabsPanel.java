package main.libgdx.gui.panels.dc.newlayout;

import main.libgdx.gui.panels.dc.unitinfo.datasource.StatsDataSource;

import java.util.function.Supplier;

public class StatsTabsPanel extends InfoPanelTabsPanel {

    private static final String GENERAL = "General";
    private static final String COMBAT = "Combat";
    private static final String MAGIC = "Magic";
    private static final String MISC = "Misc";

    public StatsTabsPanel() {
        super();
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);

        StatsDataSource source = (StatsDataSource) getUserObject();

        addTab(new StatsPanel(), GENERAL);
        addTab(new StatsPanel(), COMBAT);
        addTab(new StatsPanel(), MAGIC);
        addTab(new StatsPanel(), MISC);
        resetCheckedTab();

        tabsToNamesMap.get(GENERAL).setUserObject((Supplier) source::getGeneralStats);
        tabsToNamesMap.get(COMBAT).setUserObject((Supplier) source::getCombatStats);
        tabsToNamesMap.get(MAGIC).setUserObject((Supplier) source::getMagicStats);
        tabsToNamesMap.get(MISC).setUserObject((Supplier) source::getMiscStats);
    }
}
