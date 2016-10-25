package main.game.logic.macro.party;

import main.content.PARAMS;
import main.content.parameters.MACRO_PARAMS;
import main.entity.obj.DC_HeroObj;
import main.game.logic.macro.travel.MacroParty;
import main.system.math.MathMaster;

public class MacroPartyManager {

    public static void reduceSharedGold(MacroParty party, int amount) {

        // per shares?
        party.resetGoldShares();
        for (DC_HeroObj m : party.getMembers()) {
            Integer share = m
                    .getIntParam(MACRO_PARAMS.C_SHARED_GOLD_PERCENTAGE);
            int fractionValue = MathMaster.getFractionValue(amount, share);
            m.modifyParameter(PARAMS.GOLD, -fractionValue);
            // leader has 'share' too?
        }

    }
}
