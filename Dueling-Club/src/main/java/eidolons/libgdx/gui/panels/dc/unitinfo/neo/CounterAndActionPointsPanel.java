package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.CounterAndActionPointsSource;

public class CounterAndActionPointsPanel extends TablePanel {

    public CounterAndActionPointsPanel() {
    }

    @Override
    public void updateAct(float delta) {
        clear();

        CounterAndActionPointsSource source =
         (CounterAndActionPointsSource) getUserObject();

        addElement(source.getCounterPoints()).fill(0, 0).left();
        addElement(source.getActionPoints()).fill(0, 0).right();
    }
}
