package libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Align;
import libgdx.GDX;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.panels.TabbedPanel;
import libgdx.gui.panels.TablePanelX;
import libgdx.gui.panels.dc.unitinfo.datasource.ResistSource;

import java.util.function.Supplier;

public class ResistInfoTabsPanel extends TabbedPanel<ResistPanel> {

    public ResistInfoTabsPanel() {
        super();

        addTab(new ResistPanel(), "Resistance");
        addTab(new ResistPanel(), "Armor");
        addTab(new ResistPanel(), "Durability");
        resetCheckedTab();
        setBackground(NinePatchFactory.getLightPanelFilledDrawable());
    }

    @Override
    protected Cell createContentsCell() {
        return super.createContentsCell();
    }

    protected TablePanelX createContentsTable() {
        return new TablePanelX<>(
         GDX.width(100)+150, GDX.height(150)+250);
    }
    @Override
    protected int getDefaultTabAlignment() {
        return Align.center;
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
