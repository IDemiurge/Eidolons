package main.rules.mechanics;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.RemoveBuffEffect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.ability.effects.standard.UnconsciousBuffEffect;
import main.ability.effects.standard.UnconsciousFallEffect;
import main.content.CONTENT_CONSTS.CLASSIFICATIONS;
import main.content.PARAMS;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.rules.action.ActionRule;
import main.rules.generic.RoundRule;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

public class UnconsciousRule extends RoundRule implements ActionRule {
    /*
	 * For living units, when their Toughness falls to or below 0, they are knocked out and lose all current Focus
	 * Focus and Toughness will regenerate with half the speed while you are Unconscious, and you only get up once you have 
	 * 25-{focus_retainment}/2 and of x% of maximum Toughness . 
	 *  
	 *  You can still be attacked 
	 *  
	 *  get Prone and Immobile status and lose all Defense, plus your Willpower is halved so you are more vulnerable to spells and Mind-affecting effects 
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
    private static final int DEFAULT_DEATH_BARRIER = 35;
    private static final int DEFAULT_ANNIHILATION_BARRIER = 100;
    private static final String BUFF_NAME = null;
    private static final int MIN_FOCUS_REQ = 5;
    private static final Integer AP_PENALTY = 2;
    private static final Integer INITIATIVE_PENALTY = 75;

    public UnconsciousRule(DC_Game game) {
        super(game);
    }

    public static boolean checkUnitWakesUp(DC_HeroObj unit) {
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

    private static void wakeUp(DC_HeroObj unit) {
        // unit.removeBuff(BUFF_NAME);
        getWakeUpEffect(unit).apply(); // remove buff pretty much
        unit.getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.CONSCIOUS, unit);

        // event

    }

    private static Effect getWakeUpEffect(DC_HeroObj unit) {
        Effects e = new Effects();
        e.add(new ModifyValueEffect(PARAMS.C_N_OF_ACTIONS, MOD.MODIFY_BY_CONST, "-" + AP_PENALTY));
        e.add(new ModifyValueEffect(PARAMS.C_INITIATIVE_BONUS, MOD.MODIFY_BY_CONST, "-"
                + INITIATIVE_PENALTY));
        e.add(new RemoveBuffEffect("Unconscious"));
        e.setRef(Ref.getSelfTargetingRefCopy(unit));
        return e;
    }

    private static Effect getUnconsciousEffect(DC_HeroObj unit) {
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

    private static void fallUnconscious(DC_HeroObj unit) {
        SoundMaster.playEffectSound(SOUNDS.DEATH, unit);
        SoundMaster.playEffectSound(SOUNDS.FALL, unit);
        getUnconsciousEffect(unit).apply();
        unit.getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.UNCONSCIOUS, unit);
        // double regen? what's with focus, stamina, essence, morale? ... some
        // may be reset, others reduced, others regen
    }

    public static boolean checkUnitDies(DC_HeroObj unit) {
        return checkUnitDies(unit, DEFAULT_DEATH_BARRIER, true);
    }

    public static boolean checkUnitDies(DC_HeroObj unit, int barrier, boolean unconscious) {
        if (0 >= unit.getIntParam(PARAMS.C_ENDURANCE))
            return true;
        Integer toughness = unit.getIntParam(PARAMS.C_TOUGHNESS);
        if (toughness > 0)
            return false;
        if (!unconscious) {
            if (!canBeAnnihilated(unit))
                return false;
        } else if (!canFallUnconscious(unit))
            return toughness <= 0;
        // some attacks may reduce the barrier...
        Integer max_toughness = unit.getIntParam(PARAMS.TOUGHNESS);
        // TODO + PARAMS.DEATH_BARRIER_MOD
        if (toughness < -max_toughness * barrier / 100)
            return true;
        if (unconscious)
            if (!unit.isUnconscious())
                fallUnconscious(unit);
        return false;
    }

    private static boolean canBeAnnihilated(DC_HeroObj unit) {
        if (unit.checkClassification(CLASSIFICATIONS.WRAITH))
            return false;
        // special? vampires and such...
        return true;
    }

    private static boolean canFallUnconscious(DC_HeroObj unit) {
        if (!unit.isLiving())
            return false;
        // special? vampires and such...
        return true;
    }

    public boolean checkStatusUpdate(DC_HeroObj unit) {
        if (unit.isDead()) {
            if (checkUnitDies(unit, DEFAULT_ANNIHILATION_BARRIER, false)) {
                unit.getGame().getManager().unitAnnihilated(unit, unit);
                return false;
            }
        }
        if (checkUnitDies(unit, DEFAULT_DEATH_BARRIER, true)) {
            unit.getGame().getManager().unitDies(unit, unit, true, false);
            return false;
        }
        if (unit.isUnconscious())
            return checkUnitWakesUp(unit);

        return false;
    }

    @Override
    public void actionComplete(ActiveObj activeObj) {
        for (DC_HeroObj unit : game.getUnits()) {
            if (checkStatusUpdate(unit))
                wakeUp(unit);
        }
    }

    @Override
    public boolean unitBecomesActive(DC_HeroObj unit) {
        return true;
    }

    @Override
    public boolean check(DC_HeroObj unit) {
        return checkStatusUpdate(unit);
    }

    @Override
    public void apply(DC_HeroObj unit) {
        wakeUp(unit);
    }

}
