package main.ability.effects.oneshot.special;

import main.ability.effects.DC_Effect;
import main.game.event.Event.EVENT_TYPE;
import main.system.math.Formula;

public class AbsorbDamageEffect extends DC_Effect {
    /*
	 * Yes, I suppose we could do this via counters or a parameter
	 * DAMAGE_ABSORPTION ... The question is, how to control it? The type/source
	 * of dmg it blocks...
	 */

    /*
     * Bone Shield, Void Shield
     *
     * Stacking absorption? Customizing the way it works?
     *
     *
     * Let's say, there will be Absorb_X_Counters
     *
     * Non-specific Absorb will be used up on all damage... Then there would be
     * Magical, Physical, ... And DMG_TYPES of course
     *
     * Absorption Rule then! :)
     *
     * But it's quite different from *Reducing* damage, mind you. For Martyr, or
     * other such, we could: 1) Have a generic dmg_reduction parameter to be
     * used by DamageMaster Depending on where I use it, the event would have
     * reduced amount or not
     *
     * Redirect is kind of using *damage_being_dealt* and so the reduction must
     * be applied between that and is_dealt
     */
    public static EVENT_TYPE event_type;
    boolean returnDamage;

    public AbsorbDamageEffect(Formula amountFormula, Boolean percentage) {

    }

    public AbsorbDamageEffect(Formula amountFormula, Boolean useArmor,
                              Boolean useResistances) {
        this.formula = amountFormula;

    }

    @Override
    public boolean applyThis() {

        // some damage is absorbed *after* reductions, right?
        // e.g., for Martyr: redirect X% of dmg from unit to unit
        // obviously I should take from final... but what about non-percetage?

        // use normal armor/resistances?
        // I could of course use Absorption parameter in DamageMaster
        //

        // new AddTriggerEffect(event_type, conditions, effects).apply(ref);

		/*
		 * Percentage Doesn't matter when or where, I must cut the damage by %
		 * In fact it does - it should happen before events like
		 * damage_being_dealt, otherwise...
		 */

		/*
		 * Constant (shield)
		 */

        return true;
    }
}
