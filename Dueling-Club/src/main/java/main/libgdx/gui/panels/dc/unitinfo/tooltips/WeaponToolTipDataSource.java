package main.libgdx.gui.panels.dc.unitinfo.tooltips;

import java.util.List;

public interface WeaponToolTipDataSource {
    List<main.libgdx.gui.panels.dc.ValueContainer> getMainParams();

    List<main.libgdx.gui.panels.dc.ValueContainer> getBuffs();
}
