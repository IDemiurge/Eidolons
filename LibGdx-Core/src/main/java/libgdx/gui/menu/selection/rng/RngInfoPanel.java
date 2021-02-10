package libgdx.gui.menu.selection.rng;

import eidolons.content.PROPS;
import libgdx.gui.menu.selection.ItemListPanel;
import libgdx.gui.menu.selection.scenario.ScenarioInfoPanel;
import main.content.values.properties.G_PROPS;

/**
 * Created by EiDemiurge on 9/30/2018.
 */
public class RngInfoPanel extends ScenarioInfoPanel {

    public RngInfoPanel(ItemListPanel.SelectableItemData item) {
        super(item);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);

        String text = "Locations: " +
         item.getEntity().getProperty(PROPS.SCENARIO_MISSIONS).replace(";", ", ");
        if (text.endsWith(";"))
            text = text.substring(0, text.length() - 1);
        missionsInfo.setText(text);
        mainInfo.setText(item.getEntity().getProperty(G_PROPS.FLAVOR));
        partyInfo.setText(item.getEntity().getProperty(G_PROPS.TOOLTIP));


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
