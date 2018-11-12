package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.ArmorDataSource;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

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
            valueContainer.setBorder(getOrCreateR(
             "ui/components/ninepatch/std/background_3px_border.png"));
            addElement(valueContainer);
            row();
        }

    }
}
