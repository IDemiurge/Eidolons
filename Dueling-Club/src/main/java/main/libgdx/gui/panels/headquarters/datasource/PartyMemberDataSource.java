package main.libgdx.gui.panels.headquarters.datasource;

import main.libgdx.gui.panels.dc.ValueContainer;

/**
 * Created by JustMe on 6/2/2017.
 */
public interface PartyMemberDataSource {

    ValueContainer getName();
    ValueContainer getLevel();
    ValueContainer getBackground();
    ValueContainer getFirstClass();
    ValueContainer getSecondClass();
}
