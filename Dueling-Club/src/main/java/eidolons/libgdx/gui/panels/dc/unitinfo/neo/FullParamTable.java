package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.UnitDataSource;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.tooltips.DynamicTooltip;
import main.content.VALUE;
import main.data.XLinkedMap;

import java.util.Map;

/**
 * Created by JustMe on 5/14/2018.
 * 2 columns?
 */
public class FullParamTable extends HqElement {
    ScrollPanel scrollPanel;
    TablePanelX table;
    Map<ValueContainer, VALUE> containerMap = new XLinkedMap<>();
    private UnitDataSource unitDataSource;

    public FullParamTable() {
        scrollPanel = new ScrollPanel();
        scrollPanel.addElement(table = new TablePanelX());

    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        unitDataSource = new UnitDataSource(dataSource.getEntity());
    }

    @Override
    protected void update(float delta) {
        if (containerMap.isEmpty()) {
            for (VALUE value : unitDataSource.getStatsValues()) {
                String name = "";
                if (value != null)
                    name = value.getName();
                ValueContainer container = new ValueContainer(name, "") {
                    @Override
                    protected Drawable getDefaultBackground() {
                        return null;
                    }
                };
                DynamicTooltip tooltip = new DynamicTooltip(() -> container.getValueText());
                if (value != null)
                    container.addListener(tooltip.getController());

                containerMap.put(container, value);

                table.add(container).row();
            }
        }

        for (ValueContainer container : containerMap.keySet()) {
            VALUE value = containerMap.get(container);
            if (value == null)
                continue;
            String val = dataSource.getValue(value);
            container.setValueText(val);

        }
    }


}
