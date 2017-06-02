package main.libgdx.gui.panels.headquarters.datasource;

import main.libgdx.gui.panels.dc.ValueContainer;

/**
 * Created by JustMe on 6/2/2017.
 */
public interface PartyMemberDataSource {

    public ValueContainer getName();
    public ValueContainer getLevel();
    public ValueContainer getBackground();
    public ValueContainer getFirstClass();
    public ValueContainer getSecondClass();
}
