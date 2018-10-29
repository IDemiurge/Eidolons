package eidolons.libgdx.gui.menu.selection.rng;

import eidolons.game.core.Eidolons;
import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.scenario.ScenarioSelectionPanel;
import eidolons.macro.generation.ScenarioGenerator;
import main.entity.Entity;
import main.entity.type.ObjType;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 8/30/2018.
 */
public class RngSelectionPanel extends ScenarioSelectionPanel {
    public RngSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super(dataSupplier);
    }

    @Override
    protected void scenarioChosen(final ObjType scenario) {
        Eidolons.onThisOrNonGdxThread(() -> {
            ObjType type = ScenarioGenerator.generateRandomLevelScenario(
              500, scenario.getName(),
             scenario.getName());
             super.scenarioChosen(type);
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
    protected ItemInfoPanel createInfoPanel() {
        return new RngInfoPanel(null);
    }


}
