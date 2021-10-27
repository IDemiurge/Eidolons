package libgdx.gui.dungeon.panels.dc.unitinfo.tooltips;

import libgdx.gui.generic.ValueContainer;
import libgdx.gui.dungeon.panels.dc.unitinfo.old.MultiValueContainer;

import java.util.List;

public interface ActionTooltipSource {
    MultiValueContainer getHead();

    List<MultiValueContainer> getBase();

    List<MultiValueContainer> getRange();

    List<List<ValueContainer>> getText();

    CostTableSource getCostsSource();

    ValueContainer getPrecalcRow();

    String getDescription();
}
