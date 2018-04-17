package eidolons.libgdx.gui.panels.headquarters.tabs.stats;

import eidolons.content.PARAMS;
import eidolons.game.module.herocreator.logic.PointMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.system.math.DC_MathManager;
import main.content.ContentValsManager;
import main.content.values.parameters.PARAMETER;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 4/16/2018.
 */
public class HqMasteryTable extends HqStatTable {
    @Override
    protected PARAMETER getModifyParam(PARAMS sub) {
        return ContentValsManager.getMasteryFromScore(sub);
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

    @Override
    protected boolean isMastery() {
        return true;
    }

    @Override
    protected PARAMS[] initDataArray() {
        HqHeroDataSource hero = getUserObject();
        List<PARAMETER> list = DC_MathManager.getUnlockedMasteries(hero.getEntity());
        list =list.stream().map(p -> ContentValsManager.getMasteryFromScore(p)).collect(Collectors.toList());
        return   list.toArray(new PARAMS[list.size()]);
    }
}
