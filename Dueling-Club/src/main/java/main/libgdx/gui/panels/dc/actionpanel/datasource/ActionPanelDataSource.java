package main.libgdx.gui.panels.dc.actionpanel.datasource;

import main.entity.item.DC_QuickItemObj;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.dialog.ValueTooltip;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;
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
    public List<ActionValueContainer> getQuickSlotActions() {
        List<ActionValueContainer> list = new ArrayList<>();
        final DequeImpl<DC_QuickItemObj> items = unit.getQuickItems();

        for (DC_QuickItemObj item : items) {
            final ActionValueContainer valueContainer =
                    new ActionValueContainer(
                            getOrCreateR(item.getImagePath()),
                            () -> {
                                item.activate();
                            });
            ValueTooltip tooltip = new ValueTooltip();
            tooltip.setUserObject(new ValueContainer(item.getName(), ""));
            valueContainer.addListener(tooltip.getController());
            list.add(valueContainer);
        }

        for (int i = 0; i < unit.getRemainingQuickSlots(); i++) {
            list.add(null);
        }

        return list;
    }
}
