package main.libgdx.gui.panels.dc.unitinfo.datasource;

import main.libgdx.gui.panels.dc.ValueContainer;

public interface CounterAndActionPointsSource {
    ValueContainer getCounterPoints();

    ValueContainer getActionPoints();
}