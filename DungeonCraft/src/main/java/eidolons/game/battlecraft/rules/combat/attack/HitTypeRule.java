package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.content.DC_Formulas;
import main.content.enums.entity.NewRpgEnums;
import main.content.enums.entity.UnitEnums;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 3/12/2017.
 */
public class HitTypeRule {

    private static final int DEFAULT_GRAZE_FACTOR = 50;
    private static final int DEFAULT_CRITICAL_FACTOR = 200;
    private static final int DEFAULT_DEADEYE_FACTOR =300 ;

    public static int getDamagePercentage(DC_ActiveObj action, BattleFieldObject attacked, NewRpgEnums.HitType hitType) {
        if (attacked.checkPassive(UnitEnums.STANDARD_PASSIVES.CRITICAL_IMMUNE)) {
            return 100;
        }
        int base =0;
        switch (hitType) {
            case graze:
                base =DEFAULT_GRAZE_FACTOR;
                break;
            case critical_hit:
                base =DEFAULT_CRITICAL_FACTOR;
                break;
            case deadeye:
                base =DEFAULT_DEADEYE_FACTOR;
                break;
        }
        int mod = action.getFinalModParam(PARAMS.CRITICAL_MOD);
        if (attacked != null) {
            mod -= attacked.getIntParam(PARAMS.CRITICAL_REDUCTION);
        }
        return MathMaster.applyPercent(
                base, mod);
    }
}
