package eidolons.libgdx.gui.panels.headquarters.tabs.stats;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.game.module.herocreator.logic.PointMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import main.content.ContentValsManager;
import main.content.values.parameters.PARAMETER;

/**
 * Created by JustMe on 4/16/2018.
 */
public class HqAttributeTable extends HqStatTable {

    @Override
    protected boolean isMastery() {
        return false;
    }

    @Override
    protected PARAMS[] initDataArray() {
        return DC_ContentValsManager.ATTRIBUTES_WRAPPED;
    }

    @Override
    protected PARAMETER getModifyParam(PARAMS sub) {
        return ContentValsManager.getBaseAttribute(sub);
    }

    @Override
    protected int getPointsLeft() {
        return getUserObject().getIntParam(PARAMS.ATTR_POINTS);
    }

    @Override
    protected int getCost(PARAMS sub) {
        HqHeroDataSource hero = getUserObject();
        return PointMaster.getCost(
         hero.getEntity(),
         sub
        );
    }
}
