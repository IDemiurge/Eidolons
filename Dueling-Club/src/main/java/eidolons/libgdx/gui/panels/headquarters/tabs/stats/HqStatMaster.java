package eidolons.libgdx.gui.panels.headquarters.tabs.stats;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import eidolons.system.math.DC_MathManager;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.secondary.InfoMaster;

/**
 * Created by JustMe on 4/18/2018.
 */
public class HqStatMaster {
    public static boolean learnMastery(HeroDataModel entity, PARAMETER datum) {

        int costGold = DC_MathManager.getBuyAttributeGoldCost(entity);
        if (entity.checkParameter(PARAMS.GOLD, costGold)) {
            FloatingTextMaster.getInstance().createFloatingText
             (TEXT_CASES.REQUIREMENT, InfoMaster.getParamReasonString("" + costGold, PARAMS.GOLD),
              entity, Eidolons.getScreen().getGuiStage());
            return false;
        }
        int cost = DC_MathManager.getBuyAttributeXpCost(entity);
        if (entity.checkParameter(PARAMS.XP, cost)) {
            FloatingTextMaster.getInstance().createFloatingText
             (TEXT_CASES.REQUIREMENT, InfoMaster.getParamReasonString("" + cost, PARAMS.XP),
              entity, Eidolons.getScreen().getGuiStage());
            return false;
        }
        entity.getType().addProperty(PROPS.UNLOCKED_MASTERIES, datum.getName(), true);
        entity.addParam(datum, "1", true);
        entity.modifyParameter(PARAMS.GOLD, -costGold);
        entity.modifyParameter(PARAMS.XP, -cost);
        return true;

    }
}
