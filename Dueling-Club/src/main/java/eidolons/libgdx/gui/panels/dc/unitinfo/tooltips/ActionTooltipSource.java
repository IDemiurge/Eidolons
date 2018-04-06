package eidolons.libgdx.gui.panels.dc.unitinfo.tooltips;

import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.dc.unitinfo.MultiValueContainer;

import java.util.List;

public interface ActionTooltipSource {
    MultiValueContainer getHead();

    List<MultiValueContainer> getBase();

    List<MultiValueContainer> getRange();

    List<List<ValueContainer>> getText();

    CostTableSource getCostsSource();

    ValueContainer getPrecalcRow();
}
