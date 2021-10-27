package libgdx.gui.dungeon.panels.headquarters.datasource.hero;

import libgdx.gui.generic.ValueContainer;

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
