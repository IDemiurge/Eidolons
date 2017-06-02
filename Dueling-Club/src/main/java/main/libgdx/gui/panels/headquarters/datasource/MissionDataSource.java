package main.libgdx.gui.panels.headquarters.datasource;

import main.libgdx.gui.panels.dc.ValueContainer;

/**
 * Created by JustMe on 6/2/2017.
 */
public interface MissionDataSource {
    public ValueContainer getPosition();
    public ValueContainer getName();
    public ValueContainer getTooltip();
    public ValueContainer getMapIcon();
    public ValueContainer getTooltipIcon();

}
