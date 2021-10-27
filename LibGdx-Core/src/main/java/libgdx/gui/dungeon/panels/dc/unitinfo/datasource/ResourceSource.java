package libgdx.gui.dungeon.panels.dc.unitinfo.datasource;

import eidolons.content.PARAMS;

public interface ResourceSource {
    String getToughness();

    String getEndurance();

    String getEssence();

    String getFocus();

    String getParam(PARAMS param);
}
