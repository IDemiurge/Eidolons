package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.Iterator;
import java.util.List;

public class StatsPanel extends TablePanel {
    public StatsPanel(List<ValueContainer>... values) {

        for (int i = 0; i < values.length; i++) {
            TablePanel tablePanel = new TablePanel();
            tablePanel.setHeight(getPrefHeight());
            tablePanel.fill().center().bottom();

            List<ValueContainer> valueContainerList = values[i];

            Iterator<ValueContainer> iter = valueContainerList.iterator();
            for (int x = 0; x < 2; x++) {
                tablePanel.addCol();
                for (int y = 0; y < 4; y++) {
                    if (iter.hasNext()) {
                        tablePanel.addElement(iter.next().fill().left().bottom().pad(0, 0, 0, 0));
                    }
                }
            }
            addElement(tablePanel);

            Actor a = new Actor();
            a.setHeight(10);
            addElement(new Container(a).fill().left().bottom());
        }

        setHeight(getPrefHeight());
        fill().center().bottom();


//        UNIT_INFO_PARAMS_SIMPLE;
    }
}
