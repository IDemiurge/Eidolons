package macro.entity.party;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import main.content.values.parameters.MACRO_PARAMS;
import main.system.math.MathMaster;

public class MacroPartyUtils {

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
