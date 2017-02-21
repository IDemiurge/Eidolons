package main.libgdx.gui.panels.dc.unitinfo;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.Iterator;
import java.util.List;

public class StatsPanel extends TablePanel {
    public StatsPanel(List<ValueContainer> values) {
        Iterator<ValueContainer> iter = values.iterator();
        for (int i = 0; i < 2; i++) {
            addCol();
            for (int j = 0; j < 4; j++) {
                if (iter.hasNext()) {
                    addElement(iter.next().fill().left().bottom().pad(0, 10, 10, 0));
                }
            }
        }
    }
}
