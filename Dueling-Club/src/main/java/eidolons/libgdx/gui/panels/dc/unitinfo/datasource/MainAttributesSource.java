package eidolons.libgdx.gui.panels.dc.unitinfo.datasource;

import eidolons.libgdx.gui.generic.VerticalValueContainer;

public interface MainAttributesSource {
    VerticalValueContainer getResistance();

    VerticalValueContainer getDefense();

    VerticalValueContainer getArmor();

    VerticalValueContainer getFortitude();

    VerticalValueContainer getSpirit();
}
