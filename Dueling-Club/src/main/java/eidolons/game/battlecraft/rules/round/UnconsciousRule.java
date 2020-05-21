package eidolons.game.battlecraft.rules.round;

import eidolons.ability.effects.common.ModifyPropertyEffect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.oneshot.buff.RemoveBuffEffect;
import eidolons.ability.effects.oneshot.rule.UnconsciousBuffEffect;
import eidolons.ability.effects.oneshot.rule.UnconsciousFallEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.rules.action.ActionRule;
import eidolons.game.core.atb.AtbController;
import eidolons.game.core.game.DC_Game;
import eidolons.game.netherflame.main.death.ShadowMaster;
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
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

public class UnconsciousRule extends RoundRule implements ActionRule {
    /*
     * For living units, when their Toughness falls to or below 0, they are knocked out and lose all current Focus
     * Focus and Toughness will regenerate with half the speed while you are Unconscious, and you only getOrCreate up once you have
     * 25-{focus_retainment}/2 and of x% of maximum Toughness .
     *
     *  You can still be attacked
     *
     *   Prone and Immobile status and lose all Defense,
     * plus your Willpower is halved so you are more vulnerable to spells and Mind-affecting effects
     *
     */

    public static final Integer DEFAULT_TOUGHNESS_RECOVER = 50;
    public static final Integer DEFAULT_INITIATIVE_PENALTY = 75;
    public static final int DEFAULT_ATB_FALL_TO = (int) (-20* AtbController.TIME_LOGIC_MODIFIER);
    public static final int DEFAULT_FOCUS_REQ_UNIT = 0;
    public static final int DEFAULT_FOCUS_REQ = 0;

    public UnconsciousRule(DC_Game game) {
        super(game);
    }

    public   void unitRecovers(Unit unit) {
        // unit.removeBuff(BUFF_NAME);
        unit.getGame().
                fireEvent(new Event(
                        STANDARD_EVENT_TYPE.UNIT_HAS_RECOVERED_FROM_UNCONSCIOUSNESS, unit.getRef()));
        getWakeUpEffect(unit).apply(); // remove buff pretty much
        unit.getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.CONSCIOUS, unit);
        unit.setUnconscious(false);
        // event

    }

    public static Effect getWakeUpEffect(Unit unit) {
        Effects e = new Effects();
        e.add(new ModifyValueEffect(PARAMS.TOUGHNESS_PERCENTAGE, MOD.SET, DEFAULT_TOUGHNESS_RECOVER + ""));
        e.add(new RemoveBuffEffect("Unconscious"));
        e.add(new ModifyPropertyEffect(G_PROPS.STATUS, MOD_PROP_TYPE.REMOVE, "Unconscious"));
        e.setRef(Ref.getSelfTargetingRefCopy(unit));
        return e;
    }

    private static Effect getUnconsciousEffect(Unit unit) {
        Effects e = new Effects();
        e.add(new UnconsciousFallEffect());
        e.add(new UnconsciousBuffEffect());
        e.setRef(Ref.getSelfTargetingRefCopy(unit));
        return e;
    }

    public static void fallUnconscious(Unit unit) {
        if (unit.isDead()) {
            return;
        }
        if (unit.isPlayerCharacter()) {
            if (ShadowMaster.isShadowAlive()) {
                return;
            }
        }
        getUnconsciousEffect(unit).apply();
        unit.setUnconscious(true);
        unit.getGame().
                fireEvent(new Event(
                        STANDARD_EVENT_TYPE.UNIT_HAS_FALLEN_UNCONSCIOUS, unit.getRef()));

        unit.getGame().getLogManager().log(unit.getNameIfKnown() + " falls unconscious!");
        unit.getGame().getLogManager().newLogEntryNode(ENTRY_TYPE.UNCONSCIOUS, unit);

        // double regen? what's with focus, stamina, essence, morale? ... some
        // may be reset, others reduced, others regen
    }


    public static boolean checkUnitDies(Unit unit) {
        return checkUnitDies(unit.getIntParam(PARAMS.C_TOUGHNESS),
                unit.getIntParam(PARAMS.C_ENDURANCE),
                unit
        );
    }

    public static boolean checkUnitAnnihilated(Integer endurance, Unit unit) {
        if (!unit.isDead()) {
            return false;
        }
        if (!canBeAnnihilated(unit)) {
            return false;
        }
        return endurance <= -unit.getIntParam(PARAMS.ENDURANCE) / 2;
    }

    public static boolean checkUnitDies(Integer toughness, Integer endurance, Unit unit
    ) {
        if (EidolonsGame.getVar("ENDURANCE") || !unit.isPlayerCharacter())
            if (endurance <= 0) {
                return true;
            }
        if (toughness > 0) {
            return false;
        }
        if (!canFallUnconscious(unit)) {
            return toughness <= 0;
        }
        if (checkFallsUnconscious(unit)) {
            if (!new Event(STANDARD_EVENT_TYPE.UNIT_IS_FALLING_UNCONSCIOUS, unit.getRef()).fire()) {
                return true;
            }
            fallUnconscious(unit);
            return false;
        }
        return false;

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
        return !unit.checkClassification(UnitEnums.CLASSIFICATIONS.WRAITH);
        // special? vampires and such...
    }

    private static boolean canFallUnconscious(Unit unit) {
        return unit.isLiving();
        // special? vampires and such...
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
        } else if (checkUnitDies(unit)) {
            //            unit.getGame().getManager().unitDies(activeObj, unit, activeObj.getOwnerUnit(), true, false);
            unit.getGame().getManager().unitDies(unit, unit, true, false);
            return false;
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
    public void apply(Unit unit, float delta) {
        unitRecovers(unit);
    }

    public boolean checkUnitAnnihilated(Unit attacked) {
        return checkUnitAnnihilated(attacked.getIntParam(PARAMS.C_ENDURANCE), attacked);
    }
}
