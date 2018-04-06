package eidolons.libgdx.gui.panels.dc.actionpanel.datasource;

import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.dc.unitinfo.tooltips.CostTableSource;

public interface ActionCostSource extends CostTableSource {
    ValueContainer getDescription();

    ValueContainer getName();
}
