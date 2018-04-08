package eidolons.libgdx.gui.panels.dc.unitinfo.tooltips;

import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;

import java.util.List;

public class CostTableTooltip extends TablePanel {

    public CostTableTooltip() {
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);

        final List<ValueContainer> sources = ((CostTableSource) getUserObject()).getCostsList();

        for (int i = 0; i < sources.size(); i++) {
            final ValueContainer valueContainer = sources.get(i);
            if (i > 0 && i % 2 == 0) {
                row();
            }

            add(valueContainer).expand(1, 0).fill(1, 0);
        }
    }
}


