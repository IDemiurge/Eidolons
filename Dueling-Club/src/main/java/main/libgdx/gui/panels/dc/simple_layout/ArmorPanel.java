package main.libgdx.gui.panels.dc.simple_layout;

import java.util.Iterator;
import java.util.List;

public class ArmorPanel extends TablePanel {
    public ArmorPanel(List<ValueContainer> values) {

        Iterator<ValueContainer> iter = values.iterator();

        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 2; i++) {
                if (iter.hasNext()) {
                    addElement(iter.next());
                }
            }
            row();
        }
    }
}
