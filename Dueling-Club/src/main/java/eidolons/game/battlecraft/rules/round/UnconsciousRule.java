package eidolons.game.battlecraft.rules.round;

import eidolons.ability.effects.common.ModifyPropertyEffect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.oneshot.buff.RemoveBuffEffect;
import eidolons.ability.effects.oneshot.rule.UnconsciousBuffEffect;
import eidolons.ability.effects.oneshot.rule.UnconsciousFallEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowMaster;
import eidolons.game.battlecraft.rules.DC_RuleMaster;
import eidolons.game.battlecraft.rules.action.ActionRule;
import eidolons.game.core.game.DC_Game;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effect.MOD_PROP_TYPE;
import main.ability.effects.Effects;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.RandomWizard;
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

    public static final Integer DEFAULT_FOCUS_REQ = 15;
    public static final Integer DEFAULT_FOCUS_REQ_UNIT = 25;
    // ++ only regen part of toughness ...
    public static final int DEFAULT_DEATH_BARRIER = DC_RuleMaster.isToughnessReduced()?66 : 33;
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
        if (ShadowMaster.isOn()){
            if (unit.isPlayerCharacter()) {
                return false;
            }
        }

        int req = unit.isPlayerCharacter() ? 20 : 40;
        req *= MathMaster.MULTIPLIER;//TODO
        if (unit.getIntParam(PARAMS.TOUGHNESS_PERCENTAGE) >= req) {
            if (unit.getIntParam(PARAMS.C_FOCUS) >=
             unit.getCalculator().getFocusRecoveryRequirement()
                // Math.min(unit.getIntParam(PARAMS.FOCUS_RECOVER_REQ),
                // DEFAULT_FOCUS_REQ )
             ) {
                return true;
            }
        }
        return false;
    }

    public static void unitRecovers(Unit unit) {
        // unit.removeBuff(BUFF_NAME);

        unit.getGame().
         fireEvent(new Event(
          STANDARD_EVENT_TYPE.UNIT_HAS_RECOVERED_FROM_UNCONSCIOUSNESS, unit.getRef()));
        getWakeUpEffect(unit).apply(); // remove buff pretty much
        unit.getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.CONSCIOUS, unit);

        // event

    }

    public static Effect getWakeUpEffect(Unit unit) {
        Effects e = new Effects();
        e.add(new ModifyValueEffect(PARAMS.C_N_OF_ACTIONS, MOD.MODIFY_BY_CONST, "-" + AP_PENALTY));
        e.add(new ModifyValueEffect(
         DC_Engine.isAtbMode() ?
          PARAMS.C_INITIATIVE :
          PARAMS.C_INITIATIVE_BONUS, MOD.MODIFY_BY_CONST, "-"
         + INITIATIVE_PENALTY));
        e.add(new RemoveBuffEffect("Unconscious"));
        e.add(new ModifyPropertyEffect(G_PROPS.STATUS, MOD_PROP_TYPE.REMOVE, "Unconscious"));
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

    public static void fallUnconscious(Unit unit) {
        if (unit.isPlayerCharacter()) {
            if (ShadowMaster.isShadowAlive()) {
                return;
            }
        }
        getUnconsciousEffect(unit).apply();
        unit.getAI().getCombatAI().setEngagementDuration(0);
        unit.getAI().getCombatAI().setEngaged(false);
        unit.getGame().
         fireEvent(new Event(
          STANDARD_EVENT_TYPE.UNIT_HAS_FALLEN_UNCONSCIOUS, unit.getRef()));


        DC_SoundMaster.playEffectSound(RandomWizard.chance(35)?
         SOUNDS.FALL : SOUNDS.HIT, unit);
        unit.getGame().fireEvent(
         new Event(STANDARD_EVENT_TYPE.UNIT_FALLS_UNCONSCIOUS,
          unit.getRef()));
        unit.getGame().getLogManager().log(unit.getNameIfKnown() + " falls unconscious!");
        unit.getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.UNCONSCIOUS, unit);

        // double regen? what's with focus, stamina, essence, morale? ... some
        // may be reset, others reduced, others regen
    }

    public static boolean checkUnitDies(Unit unit) {
        return checkUnitDies
         (unit, getDeathBarrier(unit), true);
    }

    public static boolean checkUnitDies(Unit unit, Integer barrier, boolean unconscious) {
        return checkUnitDies(unit.getIntParam(PARAMS.C_TOUGHNESS),
         unit.getIntParam(PARAMS.C_ENDURANCE),
         unit, barrier, unconscious
        );
    }

    public static boolean checkUnitDies(Integer toughness, Integer endurance, Unit unit,
                                        Integer barrier,
                                        boolean unconscious //false if checking Annihilation
    ) {
        if (unit.isDead() == unconscious) {
            return false;
        }
        if (endurance <= 0) {
            return true;
        }
        if (toughness > 0) {
            return false;
        }
        if (!unconscious) {
            if (!canBeAnnihilated(unit)) {
                return false;
            }
        } else {
            if (!canFallUnconscious(unit)) {
                return toughness <= 0;
            }
            if (checkFallsUnconscious(unit)) {
                fallUnconscious(unit);
                return false;
            }
        }
        Integer max_toughness = unit.getIntParam(PARAMS.TOUGHNESS);
        if (barrier == null) {//  TODO some attacks may reduce the barrier...
            barrier = getDeathBarrier(unit);   // TODO + PARAMS.DEATH_BARRIER_MOD
        }

        return toughness < -max_toughness * barrier / 100;

    }

    public static boolean checkFallsUnconscious(Unit unit) {
        return checkFallsUnconscious(unit, unit.getIntParam(PARAMS.C_TOUGHNESS));
    }

    public static boolean checkFallsUnconscious(Unit unit, int toughness) {
        if (unit.isUnconscious())
            return false;
        if (!canFallUnconscious(unit))
            return false;
        return toughness <= 0;
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

    public static int getDeathBarrier(BattleFieldObject attacked) {
        return MathMaster.applyModIfNotZero(DEFAULT_DEATH_BARRIER,
         attacked.getIntParam(PARAMS.TOUGHNESS_DEATH_BARRIER_MOD));
    }

    //returns true if unit Recovers
    public boolean checkStatusUpdate(Unit unit) {
        return checkStatusUpdate(unit, null);
    }

    public boolean checkStatusUpdate(Unit unit, DC_ActiveObj activeObj) {
        if (unit.isDead()) {
            if (unit.isAnnihilated())
                if (checkUnitAnnihilated(unit)) {
                    unit.getGame().getManager().getDeathMaster().unitAnnihilated(unit, unit);
                    return false;
                }
            return false;
        } else if (checkUnitDies(unit, getDeathBarrier(unit), true)) {
            unit.getGame().getManager().unitDies(activeObj, unit, activeObj.getOwnerUnit(), true, false);
            return false;
        }
        if (!unit.isDead()) //really...
        if (unit.isUnconscious()) {
            return checkUnitRecovers(unit);
        }

        return false;
    }

    public boolean checkUnitAnnihilated(Unit unit) {
        return checkUnitDies(unit, DEFAULT_ANNIHILATION_BARRIER, false);
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
    public void apply(Unit unit, float delta) {
        unitRecovers(unit);
    }
}
