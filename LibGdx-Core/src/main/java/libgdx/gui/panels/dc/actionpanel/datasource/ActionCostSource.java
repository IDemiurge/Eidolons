package libgdx.gui.panels.dc.actionpanel.datasource;

import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.dc.unitinfo.tooltips.CostTableSource;

public interface ActionCostSource extends CostTableSource {
    ValueContainer getDescription();

    ValueContainer getName();
}
