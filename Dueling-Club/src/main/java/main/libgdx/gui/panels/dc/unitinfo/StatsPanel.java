package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class StatsPanel extends TablePanel {
    public StatsPanel() {
        fill().center().bottom();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (updatePanel) {
            clear();

            List<List<ValueContainer>> valueContainers = ((Supplier<List<List<ValueContainer>>>) getUserObject()).get();
            List<TablePanel> miniTables = new ArrayList<>();
            for (int i = 0; i < valueContainers.size(); i++) {
                TablePanel tablePanel = new TablePanel();
                miniTables.add(tablePanel);
                tablePanel.setHeight(getPrefHeight());
                tablePanel.fill().center().bottom();

                List<ValueContainer> valueContainerList = valueContainers.get(i);

                Iterator<ValueContainer> iter = valueContainerList.iterator();

                int rows = valueContainerList.size() / 2;
                if (valueContainerList.size() % 2 != 0) {
                    rows++;
                }

                for (int x = 0; x < 2; x++) {
                    tablePanel.addCol();
                    for (int y = 0; y < rows; y++) {
                        if (iter.hasNext()) {
                            ValueContainer next = iter.next();
                            next.setBorder(getOrCreateR("UI/components/infopanel/simple_value_border.png"), true);
                            next.cropName();
                            tablePanel.addElement(next.fill().left().bottom());
                        }
                    }
                }

                addElement(tablePanel);

                Actor a = new Actor();
                a.setHeight(10);
                addElement(new Container<>(a).fill().left().bottom());
            }

            setHeight(getPrefHeight());
            updatePanel = false;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
