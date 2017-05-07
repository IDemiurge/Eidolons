package main.game.module.adventure.party;

import main.content.PARAMS;
import main.content.values.parameters.MACRO_PARAMS;
import main.entity.obj.unit.Unit;
import main.game.module.adventure.travel.MacroParty;
import main.system.math.MathMaster;

public class MacroPartyManager {

    public static void reduceSharedGold(MacroParty party, int amount) {

        // per shares?
        party.resetGoldShares();
        for (Unit m : party.getMembers()) {
            Integer share = m
                    .getIntParam(MACRO_PARAMS.C_SHARED_GOLD_PERCENTAGE);
            int fractionValue = MathMaster.getFractionValue(amount, share);
            m.modifyParameter(PARAMS.GOLD, -fractionValue);
            // leader has 'share' too?
        }

    }
}
