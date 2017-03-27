package main.libgdx.gui.panels.dc.unitinfo;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.ArmorDataSource;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class ArmorPanel extends TablePanel {
    public ArmorPanel() {
    }

    @Override
    public void updateAct(float delta) {
        clear();

        final ArmorDataSource source = (ArmorDataSource) getUserObject();

        addElement(source.getArmorObj()).center().fill(false);
        row();

        for (ValueContainer valueContainer : source.getParamValues()) {
            valueContainer.setBorder(getOrCreateR("UI/components/infopanel/simple_value_border.png"));
            addElement(valueContainer);
            row();
        }

    }
}
