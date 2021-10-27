package libgdx.gui.dungeon.panels.dc.unitinfo.datasource;

import libgdx.gui.generic.ValueContainer;

import java.util.List;

public interface ArmorDataSource {
    ValueContainer getArmorObj();

    List<ValueContainer> getParamValues();
}
