package libgdx.gui.dungeon.panels.headquarters.datasource.hero;

import eidolons.entity.unit.Unit;
import libgdx.gui.generic.ValueContainer;

/**
 * Created by JustMe on 6/2/2017.
 */
public class PartyMemberDataSourceImpl implements PartyMemberDataSource {
    Unit hero;

    public PartyMemberDataSourceImpl(Unit hero) {
        this.hero = hero;
    }

    @Override
    public ValueContainer getName() {
        return null;
    }

    @Override
    public ValueContainer getLevel() {
        return null;
    }

    @Override
    public ValueContainer getBackground() {
        return null;
    }

    @Override
    public ValueContainer getFirstClass() {
        return null;
    }

    @Override
    public ValueContainer getSecondClass() {
        return null;
    }
}
