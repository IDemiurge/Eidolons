package eidolons.system.math.roll;

import eidolons.ability.effects.oneshot.mechanic.RollEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.action.WatchRule;
import main.content.ContentValsManager;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.RollType;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.system.auxiliary.*;
import main.system.launch.Flags;
import main.system.math.Formula;
import main.system.math.MathMaster;

public class RollMaster {
    public final static String STD_MASS = "{source_C_Carrying_Weight}+{source_Weight};"
            + "{target_C_Carrying_Weight}+{target_Weight}";

    private static String logString;
    private static int rolledValue;
    private static int rolledValue2;
    private static Roll roll;

    public static String getStdFormula(RollType roll_type, Boolean success) {
        String formulas = "";
        //TODO
        switch (roll_type) {

        }
        return ContainerUtils.openContainer(formulas).get(success ? 1 : 0);
    }

    private static String initStdSuccess(RollType roll_type) {
        return getStdFormula(roll_type, true);
    }

    private static String initStdFail(RollType roll_type) {
        return getStdFormula(roll_type, false);
    }

    public static boolean roll(RollType roll_type, Ref ref) {
        return roll(roll_type, null, null, ref);
    }

    public static boolean roll(RollType roll_type, String success, String fail, Ref ref) {
        return roll(roll_type, success, fail, ref, null);
    }

    public static boolean roll(RollType roll_type, String success, String fail, Ref ref,
                               String logString) {
        return rollLogged(roll_type, success, fail, ref, logString, null);
    }

    //TODO
    public static int getRollChance(RollEffect roll) {
        return getRollChance(roll.getRollType(), roll.getSourceValue(), roll.getTargetValue(), roll.getRef());
    }

    public static int getRollChance(RollType roll_type, String success, String fail, Ref ref) {
        if (StringMaster.isEmpty(fail)) {
            fail = initStdFail(roll_type);
        }
        Formula failFormula = new Formula(fail);
        failFormula.applyFactor(getFailFactor(roll_type));

        int max1 = failFormula.getInt(ref);

        if (StringMaster.isEmpty(success)) {
            success = initStdSuccess(roll_type);
        }
        Formula successFormula = new Formula(success);
        successFormula.applyFactor(getSuccessFactor(roll_type));
        int max2 = successFormula.getInt(ref);

        int min1 = MathMaster.applyPercent(max1, DEFAULT_MIN_ROLL_PERC);
        int min2 = MathMaster.applyPercent(max2, DEFAULT_MIN_ROLL_PERC);

        int proportion = Math.round((min1 + max1) / 2 * 100) / ((min2 + max2) / 2);
        return 50 + (100 - proportion) / 2;
    }

    /**
     * @param logString  to be appended to the logged string; for success by default, for failure if contains @
     * @param rollSource special descriptor for logging, e.g. if rolling vs Ensnare
     * @returns false if target has resisted ('wins roll')
     */
    public static boolean rollLogged(RollType roll_type, String success, String fail, Ref ref,
                                     String logString, String rollSource) {
        boolean result = roll(roll_type, success, fail, ref, logString, rollSource, false);
        if (!checkRollLogged(roll_type, result)) {
            return result;
        }

        Object[] args = {roll_type.getName(), ref.getSourceObj().getName(),
                ref.getTargetObj().getName()};

        if (roll_type.isLogToTop()) {
            args = new Object[]{main.system.text.LogManager.WRITE_TO_TOP, roll_type.getName(),
                    ref.getSourceObj().getName(), ref.getTargetObj().getName()};
        }
        ref.getGame().getLogManager().log(RollMaster.logString);
        ref.getGame().getLogManager().doneLogEntryNode();
        if (Flags.isPhaseAnimsOn()) {
            //TODO
        }
        if (ref.getGame().isDummyMode()) {
            return true;
        }
        return result;
    }

    public static boolean roll(RollType roll_type, Ref ref,
                               GenericEnums.DieType die,
                               String logAppendix, String rollHint, Boolean logged) {
        String sourceValue = getStdFormula(roll_type, true);
        String targetValue = getStdFormula(roll_type, true);
        //what if we could modify Roll object instead of keeping this infernal chain!!!!
        return
    }

    public static boolean roll(RollType roll_type, Ref ref, String sourceValue, String targetValue,
                               GenericEnums.DieType die,
                               String logAppendix, String rollHint, Boolean logged) {
sDice = getStdDiceNumber(ref.getSourceObj());
    }

    /**
     * @return true if {failure} formula rolls more, false otherwise
     */
    public static boolean roll(RollType roll_type, String sourceValue, String targetValue, Ref ref,
                               GenericEnums.DieType die, String sDice, String tDice,
                               String logAppendix, String rollHint, Boolean logged) {
        Obj source = ref.getSourceObj();
        Obj target = ref.getTargetObj();

        // if (roll == null)
        //     roll = new Roll(roll_type,die, sourceValue, targetValue,  );

        Boolean result;

        if (StringMaster.isEmpty(targetValue)) targetValue = initStdFail(roll_type);
        if (StringMaster.isEmpty(sourceValue)) sourceValue = initStdSuccess(roll_type);
        Formula failFormula = new Formula(targetValue);
        if (source != null) {
            failFormula.applyFactor(getFailFactor(roll_type));
        }
        Formula successFormula = new Formula(sourceValue);
        successFormula.applyFactor(getSuccessFactor(roll_type));
        //TODO make const bonus better - for tabletop

        int tValue = failFormula.getInt(ref);
        int sValue = successFormula.getInt(ref);

        rolledValue = sValue + DiceMaster.roll(die, source, new Formula(sDice).getInt(ref), logged);
        rolledValue2 = tValue + DiceMaster.roll(die, target, new Formula(tDice).getInt(ref), logged);

        roll.setRolledValue(rolledValue);
        roll.setRolledValue2(rolledValue2);
        result = rolledValue > rolledValue2;

        if (target == null) {
            target = ref.getEvent().getRef().getTargetObj();
        }
        // TODO display roll formulas if FULL_INFO is on!
        // if (rollSource == null) {
        //     rollSource = rolledValue + " out of " + max1;
        // } else {
        //     rollSource += StringMaster.wrapInParenthesis(rolledValue + " out of " + max1);
        // }
        // String rollTarget = rolledValue2 + " out of " + max2;
        // logString = target.getName() + ((result) ? " fails" : " wins") + " a "
        //         + roll_type.getName() + " roll with " + rollTarget + " vs " + source.getName()
        //         + "'s " + rollSource;
        //
        // if (logAppendix != null) {
        //     if (isAppendixAdded(logAppendix, result)) {
        //         logString = logString + logAppendix.replace(getSuccessAppendixIdentifier(), "");
        //     }
        // }
        if (logged == null) {
            logged = checkRollLogged(roll_type, result);
        }
        if (logged) {
            ref.getGame().getLogManager().log(logString);
        }

        ref.getGame().getLogManager();
        ref.getGame().getLogManager();
        if (!ref.isAnimationDisabled()) {
            //TODO
        }
        roll.setResult(result);
        roll.setLogAppendix(logAppendix);
        roll.setRollSource(rollHint);
        roll.setRollTarget(rollTarget);

        return result;

    }

    public static boolean isAppendixAdded(String logAppendix, Boolean result) {
        return (result && logAppendix.contains(getSuccessAppendixIdentifier()))
                || (!result && !logAppendix.contains(getSuccessAppendixIdentifier()));
    }

    public static String getSuccessAppendixIdentifier() {
        return "@";
    }


    //TODO
    public static Boolean rollForceKnockdown(Unit target, DC_ActiveObj attack, int force) {
        return null;
    }

    //TODO
    public static Boolean makeReactionRoll(Unit unit, DC_ActiveObj action, DC_ActiveObj attack) {
        return null;
    }

    private static boolean checkRollLogged(RollType roll_type, Boolean result) {
        switch (roll_type) {
            case stealth:
            case perception:
                return result;
        }

        return true;
    }

    private static String getSuccessFactor(RollType roll_type) {
        // TODO LUCK RULE? NF CYCLE PENALTY?

        PARAMETER param = ContentValsManager.getPARAM(roll_type.name() + "_ROLL_SAVE_BONUS");
        if (param == null) {
            return "0";
        }
        return StringMaster.getValueRef(KEYS.TARGET, param);
    }

    private static String getFailFactor(RollType roll_type) {
        PARAMETER param = ContentValsManager.getPARAM(roll_type.name() + "_ROLL_BEAT_BONUS");
        if (param == null) {
            return "0";
        }
        return StringMaster.getValueRef(KEYS.SOURCE, param);
    }

    public static boolean checkRollAutoResolves(RollType rollType, Ref ref) {
        Unit unit = (Unit) ref.getTargetObj();
        if (unit == null) {
            return false;
        }
        boolean result = true;
        switch (rollType) {
            case reflex:
                //immobilized
                break;
            case fortitude:
                if (unit.checkPassive(UnitEnums.STANDARD_PASSIVES.IMMATERIAL)) {
                    result = false;
                }
                break;
            case grit:
            case spirit:
                if (!unit.isLiving()) {
                    result = false;
                }
                if (unit.checkPassive(UnitEnums.STANDARD_PASSIVES.MIND_AFFECTING_IMMUNE)) {
                    result = false;
                }
                break;
            default:
                break;
        }
        if (!result) {
            result = !unit.checkProperty(G_PROPS.IMMUNITIES, rollType.getName());
        }
        if (!result) {
            ref.getGame().getLogManager().logAlert(
                    unit.getName() + " is immune to " + rollType.getName() + " !");
        }
        return result;
    }


    public static int getDexterousModifier(Unit unit, DC_ActiveObj action) {
        int mod = 25;
        if (action.checkPassive(UnitEnums.STANDARD_PASSIVES.DEXTEROUS)) {
            mod += 25;
        }
        if (action.getOwnerUnit().checkPassive(UnitEnums.STANDARD_PASSIVES.DEXTEROUS)) {
            mod += 25;
        }
        if (WatchRule.checkWatched(action.getOwnerUnit(), unit)) {
            mod += 25;
        }
        if (action.getOwnerUnit().checkParam(PARAMS.VIGILANCE_MOD)) {
            mod += action.getOwnerUnit().getIntParam(PARAMS.VIGILANCE_MOD);
        }

        return mod;

    }

    public static int getVigilanceModifier(Unit unit, DC_ActiveObj action) {
        int mod = 10;
        if (unit.checkPassive(UnitEnums.STANDARD_PASSIVES.VIGILANCE)) {
            mod += 40;
        }
        if (WatchRule.checkWatched(unit, action.getOwnerUnit())) {
            mod += 50;
        }
        if (unit.checkParam(PARAMS.VIGILANCE_MOD)) {
            mod += unit.getIntParam(PARAMS.VIGILANCE_MOD);
        }

        return mod;

    }

    public static int getRolledValue() {
        return rolledValue;
    }

    public static int getRolledValue2() {
        return rolledValue2;
    }

    public static Boolean getLuck(int value, int average) {
        Boolean luck = null;

        if (value > average * 3 / 2) {
            luck = true;
        }
        if (value < average * 2 / 3) {
            luck = false;
        }
        return luck;
    }

    public static Roll getLastRoll() {
        return roll;
    }


    public static void setRoll(Roll roll) {
        RollMaster.roll = roll;
    }
}
