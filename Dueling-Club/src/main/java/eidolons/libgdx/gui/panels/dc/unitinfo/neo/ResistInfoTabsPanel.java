package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.ResistSource;

import java.util.function.Supplier;

public class ResistInfoTabsPanel extends eidolons.libgdx.gui.panels.TabbedPanel<ResistPanel> {

    public ResistInfoTabsPanel() {
        super();

        addTab(new ResistPanel(), "Resistance");
        addTab(new ResistPanel(), "Armor");
        addTab(new ResistPanel(), "Durability");
        resetCheckedTab();
    }

    @Override
    protected Cell createContentsCell() {
        return super.createContentsCell();
    }

    protected TablePanelX createContentsTable() {
        return new TablePanelX<>(350, 500);
    }
    @Override
    protected int getDefaultTabAlignment() {
        return super.getDefaultTabAlignment();
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);

        ResistSource source = (ResistSource) userObject;

        tabsToNamesMap.get("Resistance").setUserObject((Supplier) source::getMagicResistList);
        tabsToNamesMap.get("Armor").setUserObject((Supplier) source::getArmorResists);
        tabsToNamesMap.get("Durability").setUserObject((Supplier) source::getDurabilityResists);
    }
}
