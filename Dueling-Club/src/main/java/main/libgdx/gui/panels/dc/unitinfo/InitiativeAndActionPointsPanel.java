package main.libgdx.gui.panels.dc.unitinfo;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.VerticalValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.InitiativeAndActionPointsSource;
import main.libgdx.texture.TextureCache;

public class InitiativeAndActionPointsPanel extends TablePanel {
    private final VerticalValueContainer initiativeContainer;
    private final VerticalValueContainer actionContainer;

    public InitiativeAndActionPointsPanel() {
        rowDirection = TOP_RIGHT;
        initiativeContainer = new VerticalValueContainer(TextureCache.getOrCreateR("UI/value icons/n_of_counters_s.png"), "n/a");
        actionContainer = new VerticalValueContainer(TextureCache.getOrCreateR("UI/value icons/n_of_actions_s.png"), "n/a");

        addElement(initiativeContainer).fill(0, 0).left();
        addElement(actionContainer).fill(0, 0).right();
    }

    @Override
    public void updateAct(float delta) {
        InitiativeAndActionPointsSource source = (InitiativeAndActionPointsSource) getUserObject();

        initiativeContainer.updateValue(source.getInitiative());
        actionContainer.updateValue(source.getActionPoints());
    }
}
