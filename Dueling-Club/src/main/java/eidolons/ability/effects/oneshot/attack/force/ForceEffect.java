package eidolons.ability.effects.oneshot.attack.force;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.rules.RuleMaster;
import eidolons.game.battlecraft.rules.RuleMaster.RULE;
import eidolons.game.battlecraft.rules.combat.mechanics.ForceRule;
import main.ability.effects.OneshotEffect;
import main.data.ability.AE_ConstrArgs;
import main.system.math.Formula;

public class ForceEffect extends DC_Effect implements OneshotEffect {

    private Boolean attack;

    @AE_ConstrArgs(argNames = {
     "formula", "attack"
    })
    public ForceEffect(String forceFormula, Boolean attack) {

        this.attack = attack;
        this.formula = new Formula(forceFormula);
    }

    @Override
    public boolean applyThis() {
        if (!RuleMaster.isRuleOn(RULE.FORCE)) return false;
        int force = getFormula().getInt(ref);
        ForceRule.applyForceEffects(force, (DC_ActiveObj) ref.getActive());
        Boolean result = null;
        if (attack) // include stamina into this roll somehow...
//            result = RollMaster.rollForce(getTarget(), getActiveObj(), force);
        {
            if (result == null) {

            } else {

            }
        }
        /*
         * TODO
		 * 
		 * Reduce Stamina if push/knock 'resisted'!  
		 * 
		 * 
		 * deal force damage, calculate 'remaining force' (force damage mod, 'resistance'?)
		 * tryPush - proceed if 'critical' (flies)
		 * >> Reduce Stamina 
		 * tryKnock 
		 * >> Delay - Reduce Initiative
		 * deal fall damage 
		 * >> Stun - Reduce Focus/AP
		 * 
		 */
//        new TossUnitEffect(force, true).apply(ref);

        return true;
    }

}
