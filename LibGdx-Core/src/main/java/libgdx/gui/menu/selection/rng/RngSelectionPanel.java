package libgdx.gui.menu.selection.rng;

import eidolons.game.core.Core;
import libgdx.gui.menu.selection.ItemListPanel;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.menu.selection.SelectableItemDisplayer;
import libgdx.gui.menu.selection.scenario.ScenarioSelectionPanel;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.SortMaster;
import main.system.auxiliary.NumberUtils;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 8/30/2018.
 */
public class RngSelectionPanel extends ScenarioSelectionPanel {
    public RngSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super(dataSupplier);
    }

    protected Comparator<? super SelectableItemData> getDataSorter() {
        return new SortMaster<SelectableItemData>().getSorterByExpression_(t ->
         NumberUtils.getIntParse(t.getEntity().getProperty(G_PROPS.ID)));
    }
    @Override
    protected void scenarioChosen(final ObjType scenario) {
        Core.onThisOrNonGdxThread(() -> {
            //TODO macro Review
            // ObjType type = ScenarioGenerator.generateRandomLevelScenario(
            //   500, scenario.getName(),
            //  scenario.getName());
             super.scenarioChosen(scenario);
         }
        );
    }

    @Override
    protected String getTitle() {
        return "Select a Destination";
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new RngListPanel();
    }

    @Override
    protected SelectableItemDisplayer createInfoPanel() {
        return new RngInfoPanel(null);
    }


}
