package eidolons.libgdx.gui.panels.headquarters.tabs.stats;

import eidolons.content.PARAMS;
import eidolons.libgdx.GDX;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.panels.headquarters.ValueTable;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import main.content.values.parameters.PARAMETER;

/**
 * Created by JustMe on 4/16/2018.
 */
public abstract class HqStatTable extends ValueTable<PARAMS, HqStatElement> {

    LabelX pointsLeft;
    private boolean editable;

    public HqStatTable() {
        super(2, 10);
        top();
        if (isMastery()) {
            right();
        } else {
            left();
        }
        setFixedSize(true);
        setSize(GDX.size(225), GDX.size(100));
        debug();
    }

    @Override
    public HqHeroDataSource getUserObject() {
        return (HqHeroDataSource) super.getUserObject();
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        int i = 0;
        for (PARAMS sub : data) {
            if (sub != null) {
                HqStatElement actor = actors[i++];
                if (actor == null)
                    continue;
                actor.setDisabled(getPointsLeft() > getCost(sub));
                actor.setDisplayedParam(sub);
                actor.setModifyParam(getModifyParam(sub));
                actor.setUserObject(getUserObject());
                actor.updateAct(delta);
            }
        }
    }

    protected abstract PARAMETER getModifyParam(PARAMS sub);

    protected abstract int getPointsLeft();

    protected abstract int getCost(PARAMS sub);

    @Override
    protected HqStatElement createElement(PARAMS datum) {
        return new HqStatElement(datum, isMastery(), isEditable());
    }

    protected abstract boolean isMastery();

    @Override
    protected HqStatElement[] initActorArray() {
        return new HqStatElement[10];
    }


    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
