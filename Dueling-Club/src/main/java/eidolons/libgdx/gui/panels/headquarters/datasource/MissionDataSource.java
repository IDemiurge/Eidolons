package eidolons.libgdx.gui.panels.headquarters.datasource;

import eidolons.libgdx.gui.panels.dc.ValueContainer;

/**
 * Created by JustMe on 6/2/2017.
 */
public interface MissionDataSource {
    ValueContainer getPosition();

    ValueContainer getName();

    ValueContainer getTooltip();

    ValueContainer getMapIcon();

    ValueContainer getTooltipIcon();

}
