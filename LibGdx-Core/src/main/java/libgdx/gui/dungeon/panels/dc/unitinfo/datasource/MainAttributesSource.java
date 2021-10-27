package libgdx.gui.dungeon.panels.dc.unitinfo.datasource;

import libgdx.gui.generic.VerticalValueContainer;

public interface MainAttributesSource {
    VerticalValueContainer getResistance();

    VerticalValueContainer getDefense();

    VerticalValueContainer getArmor();

    VerticalValueContainer getFortitude();

    VerticalValueContainer getSpirit();
}
