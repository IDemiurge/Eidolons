package eidolons.system.math.roll;

import eidolons.ability.effects.oneshot.mechanic.RollEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.unit.Unit;
import main.content.ContentValsManager;
import main.content.VALUE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.RollType;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.system.auxiliary.*;
import main.system.math.Formula;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RollMaster {
    public final static String STD_MASS = "{source_C_Carrying_Weight}+{source_Weight};"
            + "{target_C_Carrying_Weight}+{target_Weight}";

    private static int rolledValue;
    private static int rolledValue2;
    private static Roll roll;

    public static String getStdFormula(RollType roll_type, boolean source) {
        KEYS objRef = source ? KEYS.SOURCE : KEYS.TARGET;
        if (roll_type.getMulti() != null) {
            List<VALUE> list = Arrays.stream(roll_type.getMulti()).map(multi ->
                    ContentValsManager.getPARAM(multi.toString())).collect(Collectors.toList());
            return StringMaster.getValueRefs(objRef, list);
        }
        return StringMaster.getValueRef(objRef, ContentValsManager.getPARAM(roll_type.toString()));
    }

    private static String initStdSuccess(RollType roll_type) {
        return getStdFormula(roll_type, true);
    }

    private static String initStdFail(RollType roll_type) {
        return getStdFormula(roll_type, false);
    }

    public static boolean roll(Roll roll, Ref ref) {
        RollMaster.setRoll(roll);
        return roll(roll.type,
                roll.getsValue(),
                roll.gettValue(), ref,
                roll.getDie(),
                roll.getsDice(),
                roll.gettDice(),
                roll.getLogAppendix(),
                roll.getRollHint(),
                true);
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
        // if (rollHint == null) {
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
        // if (logged == null) {
        //     logged = checkRollLogged(roll_type, result);
        // }
        // if (logged) {
        //     ref.getGame().getLogManager().log(logString);
        // }

        ref.getGame().getLogManager();
        ref.getGame().getLogManager();
        if (!ref.isAnimationDisabled()) {
            //TODO
        }
        roll.setResult(result);
        roll.setLogAppendix(logAppendix);
        roll.setRollHint(rollHint);

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

        int min1 =  1;
        int min2 =  1;

        int proportion = Math.round((min1 + max1) / 2 * 100) / ((min2 + max2) / 2);
        return 50 + (100 - proportion) / 2;
    }
}
