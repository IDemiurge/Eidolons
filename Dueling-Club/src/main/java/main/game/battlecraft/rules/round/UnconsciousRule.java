package main.game.battlecraft.rules.round;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.common.ModifyValueEffect;
import main.ability.effects.oneshot.buff.RemoveBuffEffect;
import main.ability.effects.oneshot.rule.UnconsciousBuffEffect;
import main.ability.effects.oneshot.rule.UnconsciousFallEffect;
import main.content.PARAMS;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.action.ActionRule;
import main.game.core.game.DC_Game;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.audio.DC_SoundMaster;
import main.system.math.MathMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

public class UnconsciousRule extends RoundRule implements ActionRule {
    /*
     * For living units, when their Toughness falls to or below 0, they are knocked out and lose all current Focus
	 * Focus and Toughness will regenerate with half the speed while you are Unconscious, and you only getOrCreate up once you have
	 * 25-{focus_retainment}/2 and of x% of maximum Toughness . 
	 *  
	 *  You can still be attacked 
	 *  
	 *  getOrCreate Prone and Immobile status and lose all Defense, plus your Willpower is halved so you are more vulnerable to spells and Mind-affecting effects
	 *   
	 *  
	 *  Units with Trample will automatically crush an unconscious target if <...> 
	 * 
	 * If Toughness goes below -35%, the unit dies. 
	 * 
	 * 
	 */

    public static final Integer DEFAULT_FOCUS_REQ = 25;
    // ++ only regen part of toughness ...
    public static final int DEFAULT_DEATH_BARRIER = 35;
    public static final int DEFAULT_ANNIHILATION_BARRIER = 100;
    public static final String BUFF_NAME = null;
    public static final int MIN_FOCUS_REQ = 5;
    public static final Integer AP_PENALTY = 2;
    public static final Integer INITIATIVE_PENALTY = 75;

    public UnconsciousRule(DC_Game game) {
        super(game);
    }

    public static boolean checkUnitRecovers(Unit unit) {
        // toughness barrier... ++ focus? ++status?
        if (unit.getIntParam(PARAMS.TOUGHNESS_PERCENTAGE) >= 25) {
            if (unit.getIntParam(PARAMS.C_FOCUS) >= unit.getIntParam(PARAMS.FOCUS_RECOVER_REQ)
                // Math.min(unit.getIntParam(PARAMS.FOCUS_RECOVER_REQ),
                // DEFAULT_FOCUS_REQ )
             ) {
                return true;
            }
        }
        return false;
    }

    private static void unitRecovers(Unit unit) {
        // unit.removeBuff(BUFF_NAME);

        unit.getGame().
         fireEvent(new Event(
          STANDARD_EVENT_TYPE.UNIT_HAS_RECOVERED_FROM_UNCONSCIOUSNESS, unit.getRef()));
        getWakeUpEffect(unit).apply(); // remove buff pretty much
        unit.getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.CONSCIOUS, unit);

        // event

    }

    private static Effect getWakeUpEffect(Unit unit) {
        Effects e = new Effects();
        e.add(new ModifyValueEffect(PARAMS.C_N_OF_ACTIONS, MOD.MODIFY_BY_CONST, "-" + AP_PENALTY));
        e.add(new ModifyValueEffect(PARAMS.C_INITIATIVE_BONUS, MOD.MODIFY_BY_CONST, "-"
         + INITIATIVE_PENALTY));
        e.add(new RemoveBuffEffect("Unconscious"));
        e.setRef(Ref.getSelfTargetingRefCopy(unit));
        return e;
    }

    private static Effect getUnconsciousEffect(Unit unit) {
        Effects e = new Effects();
        // Effects effects = new Effects(new
        // AddStatusEffect(STATUS.UNCONSCIOUS));
        // e.add(new ModifyValueEffect(PARAMS.C_FOCUS, MODVAL_TYPE.SET, "0"));
        // e.add(new AddBuffEffect(BUFF_NAME, effects));
        e.add(new UnconsciousFallEffect());
        e.add(new UnconsciousBuffEffect());
        e.setRef(Ref.getSelfTargetingRefCopy(unit));
        return e;
    }

    private static void fallUnconscious(Unit unit) {
        unit.getGame().
         fireEvent(new Event(
          STANDARD_EVENT_TYPE.UNIT_HAS_FALLED_UNCONSCIOUS, unit.getRef()));

        DC_SoundMaster.playEffectSound(SOUNDS.DEATH, unit);
        DC_SoundMaster.playEffectSound(SOUNDS.FALL, unit);
        getUnconsciousEffect(unit).apply();
        unit.getGame().fireEvent(
         new Event(STANDARD_EVENT_TYPE.UNIT_FALLS_UNCONSCIOUS,
          unit.getRef()));
        unit.getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.UNCONSCIOUS, unit);
        // double regen? what's with focus, stamina, essence, morale? ... some
        // may be reset, others reduced, others regen
    }

    public static boolean checkUnitDies(Unit unit) {
        return checkUnitDies
         (unit, getDeathBarrier(unit), true);
    }

    public static boolean checkUnitDies(Unit unit, int barrier, boolean unconscious) {
        return checkUnitDies(unit.getIntParam(PARAMS.C_TOUGHNESS),
         unit.getIntParam(PARAMS.C_ENDURANCE),
         unit, barrier, unconscious
        );
    }

    public static boolean checkUnitDies(Integer toughness, Integer endurance, Unit unit,
                                        Integer barrier,
                                        boolean unconscious //false if checking Annihilation
    ) {
        if (0 >= endurance) {
            return true;
        }
        if (toughness > 0) {
            return false;
        }
        if (!unconscious) {
            if (!canBeAnnihilated(unit)) {
                return false;
            }
        } else
            if (!canFallUnconscious(unit)) {
            return toughness <= 0;
        }
            if (checkFallsUnconscious(unit))
            {
                fallUnconscious(unit);
                return false;
            }

        Integer max_toughness = unit.getIntParam(PARAMS.TOUGHNESS);
        if (barrier == null) {//  TODO some attacks may reduce the barrier...
            barrier = getDeathBarrier(unit);   // TODO + PARAMS.DEATH_BARRIER_MOD
        }

        return toughness < -max_toughness * barrier / 100;

    }

    public static boolean checkFallsUnconscious(Unit unit) {
        if ( unit.isUnconscious())
            return false;
        if (!canFallUnconscious(unit))
            return false;
        return unit.getIntParam(PARAMS.C_TOUGHNESS) <=0;
    }

    private static boolean canBeAnnihilated(Unit unit) {
        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.WRAITH)) {
            return false;
        }
        // special? vampires and such...
        return true;
    }

    private static boolean canFallUnconscious(Unit unit) {
        if (!unit.isLiving()) {
            return false;
        }
        // special? vampires and such...
        return true;
    }

    public static int getDeathBarrier(Unit attacked) {
        return MathMaster.applyModIfNotZero(DEFAULT_DEATH_BARRIER,
         attacked.getIntParam(PARAMS.TOUGHNESS_DEATH_BARRIER_MOD));
    }

    //returns true if unit Recovers
    public boolean checkStatusUpdate(Unit unit) {
        if (unit.isDead()) {
            if (checkUnitDies(unit, DEFAULT_ANNIHILATION_BARRIER, false)) {
                unit.getGame().getManager().getDeathMaster().unitAnnihilated(unit, unit);

            }
            return false;
        }
        else
        if (checkUnitDies(unit, getDeathBarrier(unit), true)) {
            unit.getGame().getManager().unitDies(unit, unit, true, false);
            return false;
        }
        if (unit.isUnconscious()) {
            return checkUnitRecovers(unit);
        }

        return false;
    }

    @Override
    public void actionComplete(ActiveObj activeObj) {
        for (Unit unit : game.getUnits()) {
            if (checkStatusUpdate(unit)) {
                unitRecovers(unit);
            }
        }
    }

    @Override
    public boolean unitBecomesActive(Unit unit) {
        return true;
    }

    @Override
    public boolean check(Unit unit) {
        return checkStatusUpdate(unit);
    }

    @Override
    public void apply(Unit unit) {
        unitRecovers(unit);
    }
}
