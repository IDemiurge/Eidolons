package eidolons.libgdx.gui.panels.headquarters.tabs.stats;

import eidolons.content.PROPS;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HQ_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import main.content.values.parameters.PARAMETER;

/**
 * Created by JustMe on 4/18/2018.
 */
public class HqStatMaster {
    public static boolean learnMastery(HeroDataModel entity, PARAMETER datum) {

//        int costGold = DC_MathManager.getBuyAttributeGoldCost(entity);
//        if (entity.checkParameter(PARAMS.GOLD, costGold)) {
//            FloatingTextMaster.getInstance().createFloatingText
//             (TEXT_CASES.REQUIREMENT, InfoMaster.getParamReasonString("" + costGold, PARAMS.GOLD),
//              entity, Eidolons.getScreen().getGuiStage());
//            return false;
//        }
//        int cost = DC_MathManager.getBuyAttributeXpCost(entity);
//        if (entity.checkParameter(PARAMS.XP, cost)) {
//            FloatingTextMaster.getInstance().createFloatingText
//             (TEXT_CASES.REQUIREMENT, InfoMaster.getParamReasonString("" + cost, PARAMS.XP),
//              entity, Eidolons.getScreen().getGuiStage());
//            return false;
//        }
        HqDataMaster.operation(entity, HQ_OPERATION.NEW_MASTERY, datum);

//        entity.modifyParameter(PARAMS.GOLD, -costGold);
//        entity.modifyParameter(PARAMS.XP, -cost);
        return true;

    }
}