package main.libgdx.gui.panels.dc.actionpanel.datasource;

import main.entity.item.DC_QuickItemObj;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class ActionPanelDataSource implements QuickSlotsDataSource {
    private Unit unit;

    public ActionPanelDataSource(Unit unit) {
        this.unit = unit;
    }

    @Override
    public List<ActionDataSource> getQuickSlotActions() {
        List<ActionDataSource> list = new ArrayList<>();
        final DequeImpl<DC_QuickItemObj> items = unit.getQuickItems();

        for (DC_QuickItemObj item : items) {
            final ActionDataSource dataSource = new ActionDataSource() {
                @Override
                public ValueContainer getValue() {
                    return new ValueContainer(getOrCreateR(item.getImagePath()));
                }

                @Override
                public void doAction() {
                    item.activate();
                }
            };
            list.add(dataSource);
        }

        for (int i = 0; i < unit.getRemainingQuickSlots(); i++) {
            list.add(null);
        }

        return list;
    }
}
