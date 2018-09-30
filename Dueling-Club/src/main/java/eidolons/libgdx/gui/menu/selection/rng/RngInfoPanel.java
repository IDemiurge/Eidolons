package eidolons.libgdx.gui.menu.selection.rng;

import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.scenario.ScenarioInfoPanel;

/**
 * Created by EiDemiurge on 9/30/2018.
 */
public class RngInfoPanel extends ScenarioInfoPanel {

    public RngInfoPanel(ItemListPanel.SelectableItemData item) {
        super(item);
    }

    @Override
    protected String getDefaultText() {
        return "It's procedural...";
    }

    @Override
    protected String getDefaultTitle() {
        return "Random level X";
    }
}
