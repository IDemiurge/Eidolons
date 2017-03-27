package main.libgdx.gui.panels.dc.unitinfo;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.unitinfo.datasource.InitiativeAndActionPointsSource;

public class InitiativeAndActionPointsPanel extends TablePanel {

    public InitiativeAndActionPointsPanel() {
    }

    @Override
    public void updateAct(float delta) {
        clear();

        InitiativeAndActionPointsSource source = (InitiativeAndActionPointsSource) getUserObject();

        addElement(source.getInitiative()).fill(0, 0).left();
        addElement(source.getActionPoints()).fill(0, 0).right();
    }
}
