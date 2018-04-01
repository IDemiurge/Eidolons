package eidolons.libgdx.gui.panels.dc.unitinfo.datasource;

import eidolons.content.PARAMS;

public interface ResourceSource {
    String getToughness();

    String getEndurance();

    String getStamina();

    String getMorale();

    String getEssence();

    String getFocus();

    String getParam(PARAMS param);
}
