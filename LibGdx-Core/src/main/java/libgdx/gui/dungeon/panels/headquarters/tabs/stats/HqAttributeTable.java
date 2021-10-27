package libgdx.gui.dungeon.panels.headquarters.tabs.stats;

import eidolons.content.ContentConsts;
import eidolons.content.PARAMS;
import eidolons.netherflame.eidolon.heromake.model.PointMaster;
import libgdx.gui.dungeon.panels.headquarters.datasource.hero.HqHeroDataSource;
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
        return ContentConsts.ATTRIBUTES_WRAPPED;
    }

    @Override
    protected String getPointsText() {
        if (isEditable()) {
            return "       Attributes         pts.";
        }
        return "        Attributes          ";
    }

    @Override
    protected PARAMETER getModifyParam(PARAMS sub) {
        return ContentValsManager.getBaseAttribute(sub);
    }

    @Override
    protected int getPointsLeft() {
        if (getUserObject() == null) {
            return 0;
        }
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
