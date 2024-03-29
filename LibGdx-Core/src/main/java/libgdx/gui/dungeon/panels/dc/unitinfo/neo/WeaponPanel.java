package libgdx.gui.dungeon.panels.dc.unitinfo.neo;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.dungeon.panels.TabbedPanel;
import libgdx.gui.dungeon.panels.TablePanel;

import java.util.List;

public class WeaponPanel extends TabbedPanel {
    public WeaponPanel() {
        super();
        pad(50, 10, 5, 10);
    }

    protected void addWeapons(ValueContainer mainWeapon, List<ValueContainer> list, String tabName) {
        final TablePanel panel = new TablePanel();
        Cell<ValueContainer> lastCell = panel.add(mainWeapon).left().bottom();
        for (ValueContainer valueContainer : list) {
            lastCell = panel.addElement(valueContainer).expand(0, 0).fill(false).left().bottom().padLeft(5);
        }

        if (lastCell != null) {
            lastCell.expand();
        }

        addTab(panel, tabName);
    }
}
