package main.ability.effects.oneshot.rpg;

import main.ability.effects.DC_Effect;
import main.system.math.Formula;

public class ForceEffect extends DC_Effect {

    private Boolean attack;

    public ForceEffect(String forceFormula, Boolean attack) {
        this.attack = attack;
        this.formula = new Formula(forceFormula);
    }

    @Override
    public boolean applyThis() {
        int force = getFormula().getInt(ref);
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
