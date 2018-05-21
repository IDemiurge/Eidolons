package eidolons.libgdx.gui.panels.dc.inventory;

import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.QuickSlotDataSource;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;

import java.util.List;

public class InventoryQuickSlotPanel extends TablePanel {


    public InventoryQuickSlotPanel() {
        setBackground(NinePatchFactory.getLightPanelDrawable());
    }

    @Override
    public void afterUpdateAct(float delta) {
        if (getUserObject() == null)
            return;
        clear();
        super.afterUpdateAct(delta);
        final List<InventoryValueContainer> quickSlots =
         ((QuickSlotDataSource) getUserObject()).getQuickSlots();

        int maxLength = Math.min(8, quickSlots.size());

        for (int i = 0; i < maxLength; i++) {
            ValueContainer valueContainer = quickSlots.get(i);
            if (valueContainer == null) {
                valueContainer = new ValueContainer(TextureCache.getOrCreateR(
                 Images.EMPTY_QUICK_ITEM ));
            }

            addElement(valueContainer).fill(0, 1).expand(0, 1).center();
        }
    }
}
