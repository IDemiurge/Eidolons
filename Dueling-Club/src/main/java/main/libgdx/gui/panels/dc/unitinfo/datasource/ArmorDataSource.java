package main.libgdx.gui.panels.dc.unitinfo.datasource;

import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

public interface ArmorDataSource {
    ValueContainer getArmorObj();

    List<ValueContainer> getParamValues();
}
