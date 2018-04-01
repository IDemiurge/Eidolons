package eidolons.libgdx.gui.panels.dc.unitinfo.datasource;

import eidolons.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

public interface ArmorDataSource {
    ValueContainer getArmorObj();

    List<ValueContainer> getParamValues();
}
