package eidolons.libgdx.gui.panels.dc.unitinfo.tooltips;

import eidolons.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

public interface WeaponToolTipDataSource {
    List<ValueContainer> getMainParams();

    List<ValueContainer> getBuffs();
}
