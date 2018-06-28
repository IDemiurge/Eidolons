package eidolons.libgdx.gui.panels.headquarters.tabs.stats;

import eidolons.content.PARAMS;
import eidolons.game.module.herocreator.logic.PointMaster;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import main.content.ContentValsManager;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
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
        return getUserObject().getIntParam(PARAMS.MASTERY_POINTS);
    }

    @Override
    protected int getCost(PARAMS sub) {
        HqHeroDataSource hero = getUserObject();
        return PointMaster.getCost(
         hero.getEntity(),
         sub);
    }
    @Override
    protected String getPointsText() {
        return "Mastery pts             ";
    }
    @Override
    protected boolean isMastery() {
        return true;
    }

    @Override
    protected PARAMS[] initDataArray() {
        HqHeroDataSource hero = getUserObject();
        List<PARAMETER> list = new ArrayList<>(SkillMaster.getUnlockedMasteries(hero.getEntity()));
        list =list.stream().map(p -> ContentValsManager.getMasteryFromScore(p)).
         collect(Collectors.toList());
        ListMaster.fillWithNullElements(list, size);
        return   list.toArray(new PARAMS[list.size()]);
    }
}
