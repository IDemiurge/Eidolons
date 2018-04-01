package eidolons.libgdx.gui.panels.headquarters.datasource;

import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.panels.dc.ValueContainer;

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
