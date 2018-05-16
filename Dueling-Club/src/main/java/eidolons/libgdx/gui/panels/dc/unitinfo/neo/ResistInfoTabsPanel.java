package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.ResistSource;

import java.util.function.Supplier;

public class ResistInfoTabsPanel extends InfoPanelTabsPanel<ResistPanel> {

    public ResistInfoTabsPanel() {
        super();

        addTab(new ResistPanel(), "Resistance");
        addTab(new ResistPanel(), "Armor");
        addTab(new ResistPanel(), "Durability");
        resetCheckedTab();
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);

        ResistSource source = (ResistSource) userObject;

        tabsToNamesMap.get("Resistance").setUserObject((Supplier) source::getMagickResists);
        tabsToNamesMap.get("Armor").setUserObject((Supplier) source::getArmorResists);
        tabsToNamesMap.get("Durability").setUserObject((Supplier) source::getDurabilityResists);
    }
}
