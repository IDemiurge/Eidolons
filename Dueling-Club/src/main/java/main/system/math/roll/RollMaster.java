package main.system.math.roll;

import main.ability.effects.containers.RollEffect;
import main.content.enums.GenericEnums.ROLL_TYPES;
import main.content.ContentManager;
import main.content.PARAMS;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.ability.construct.VariableManager;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.rules.action.WatchRule;
import main.rules.combat.ForceRule;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.graphics.ANIM;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.EffectAnimation;
import main.system.graphics.PhaseAnimation;
import main.system.math.Formula;
import main.system.math.MathMaster;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;
import main.system.text.LogEntryNode;

public class RollMaster {
    /*
	 * define std rolls, and maybe var refs some rolls may want to define only
	 * *one* of the refs!
	 */

    public static final int DEFAULT_MIN_ROLL_PERC = 20;
    public final static String STD_MASS = "{source_C_Carrying_Weight}+{source_Weight};"
            + "{target_C_Carrying_Weight}+{target_Weight}";
    public final static String STD_BODY_STRENGTH = "{Strength};{Strength}";
    public final static String STD_REFLEX = "{Agility};{target_Dexterity}";
    public final static String STD_ACCURACY = "{target_Dexterity};{Agility}";
    private static final String STD_SPELL_ROLL = "{Mastery}+{Spellpower}+2*{Spell_SpellDifficulty}";
    // (fail;success)
    public final static String STD_MIND_AFFECTING = STD_SPELL_ROLL + ";"
            + getSTD_SPELL_RESIST_ROLL("{Target_Willpower}+5*{Target_Spirit}");
    private static final String STD_QUICK_WIT = "{Intelligence};{target_Intelligence}";
    private static final String STD_FORTITUDE = STD_SPELL_ROLL + ";"
            + getSTD_SPELL_RESIST_ROLL("3*{Target_Vitality}");

    private static final String STD_FAITH = "{Charisma}+{Willpower};{spell_spell_difficulty}";
    private static final String STD_DETECTION = "{target_Detection};{stealth}";
    private static final String STD_STEALTH = "{Detection};{target_stealth}";
    private static final String STD_DEFENSE = "{Attack};2*{target_Defense}";

    private static final String STD_IMMATERIAL = "{Willpower};{target_Spellpower}";
    private static final String STD_DISPEL = "{spell_spell_difficulty}+{source_Mastery}+{source_Spellpower};{target_spell_difficulty}+{summoner_Mastery}+{summoner_Spellpower}";

    private static final String STD_UNLOCK = "{source_pick lock};{target_lock level}";
    private static final String STD_DISARM_TRAP = "{source_trap_skill};{target_trap level}";

    private static String logString;

    private static int rolledValue;

    private static int rolledValue2;

    private static Roll roll;

    private static String getSTD_SPELL_RESIST_ROLL(String string) {
        return string + "-((" + string + ")*{source_resistance_penetration}/100)";
    }

    public static String getStdFormula(ROLL_TYPES roll_type, Boolean success) {
        String formulas = "";
        switch (roll_type) {
            case DISARM_TRAP:
                formulas = STD_DISARM_TRAP;
                break;
            case UNLOCK:
                formulas = STD_UNLOCK;
                break;
            case ACCURACY:
                formulas = STD_ACCURACY;
                break;
            case DISPEL:
                formulas = STD_DISPEL;
                break;
            case IMMATERIAL:
                formulas = STD_IMMATERIAL;
                break;
            case BODY_STRENGTH:
                formulas = STD_BODY_STRENGTH;
                break;
            case DEFENSE:
                formulas = STD_DEFENSE;
                break;
            case STEALTH:
                formulas = STD_STEALTH;
                break;
            case DETECTION:
                formulas = STD_DETECTION;
                break;
            case FAITH:
                formulas = STD_FAITH;
                break;
            case FORTITUDE:
                formulas = STD_FORTITUDE;
                break;
            case MASS:
                formulas = STD_MASS;
                break;
            case MIND_AFFECTING:
                formulas = STD_MIND_AFFECTING;
                break;
            case QUICK_WIT:
                formulas = STD_QUICK_WIT;
                break;
            case REFLEX:
                formulas = STD_REFLEX;
                break;
            default:
                break;

        }
        return StringMaster.openContainer(formulas).get(success ? 1 : 0);
    }

    private static String initStdSuccess(ROLL_TYPES roll_type) {
        return getStdFormula(roll_type, true);
    }

    private static String initStdFail(ROLL_TYPES roll_type) {
        return getStdFormula(roll_type, false);
    }

    public static boolean roll(ROLL_TYPES roll_type, Ref ref) {
        return roll(roll_type, null, null, ref);
    }

    public static boolean roll(ROLL_TYPES roll_type, String success, String fail, Ref ref) {
        return roll(roll_type, success, fail, ref, null);
    }

    public static boolean roll(ROLL_TYPES roll_type, String success, String fail, Ref ref,
                               String logString) {
        return rollLogged(roll_type, success, fail, ref, logString, null);
    }

    // if (ImmunityRule.checkImmune(target, roll_type.getName())) // +
    // " roll"
    // TODO // -> map to
    // // different
    // // names
    // return false;

    // if (((DC_Game) ref.getGame()).getTestMaster().isActionFree(
    // ref.getActive().getName()))
    // return true;

    public static int getRollChance(RollEffect roll) {
        return getRollChance(roll.getRollType(), roll.getSuccess(), roll.getFail(), roll.getRef());
    }

    public static int getRollChance(ROLL_TYPES roll_type, String success, String fail, Ref ref) {
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

        int min1 = MathMaster.applyMod(max1, DEFAULT_MIN_ROLL_PERC);
        int min2 = MathMaster.applyMod(max2, DEFAULT_MIN_ROLL_PERC);

        int proportion = Math.round((min1 + max1) / 2 * 100) / ((min2 + max2) / 2);
        int perc = 50 + (100 - proportion) / 2;
        return perc;
    }

    /**
     * @param logString  to be appended to the logged string; for success by default,
     *                   for failure if contains @
     * @param rollSource special descriptor for logging, e.g. if rolling vs Ensnare
     * @returns false if target has resisted ('wins roll')
     */
    public static boolean rollLogged(ROLL_TYPES roll_type, String success, String fail, Ref ref,
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
        LogEntryNode entry = ref.getGame().getLogManager().newLogEntryNode(
                result ? ENTRY_TYPE.ROLL_LOST : ENTRY_TYPE.ROLL_WON, args);
        ref.getGame().getLogManager().log(RollMaster.logString);
        ref.getGame().getLogManager().doneLogEntryNode();

        if (ref.getActive() != null) {
            ANIM anim = new EffectAnimation((DC_ActiveObj) ref.getActive());
            anim.addPhase(new AnimPhase(PHASE_TYPE.ROLL, roll));
            entry.setLinkedAnimation(anim);
        }

        // if (ref.getActive().getAnimation() != null)
        // entry.setLinkedAnimation(ref.getActive().getAnimation().getFilteredClone(
        // PHASE_TYPE.ROLL));
        if (ref.getGame().isDummyMode()) {
            return true;
        }
        return result;
    }

    public static boolean roll(ROLL_TYPES roll_type, String success, String fail, Ref ref,
                               String logAppendix, String rollSource) {
        return roll(roll_type, success, fail, ref, logAppendix, rollSource, null);
    }

    public static boolean roll(ROLL_TYPES roll_type, String success, String fail, Ref ref,
                               String logAppendix, String rollSource, Boolean logged) {
        Obj source = ref.getSourceObj();
        Obj target = ref.getTargetObj();
        // if (roll == null)
        roll = new Roll(roll_type, success, fail, 0);

        // ref.getActive()

        if (StringMaster.isEmpty(fail)) {
            fail = initStdFail(roll_type);
        }
        Formula failFormula = new Formula(fail);
        if (source != null) {
            failFormula.applyFactor(getFailFactor(roll_type));
        }

        Boolean result;

        int max1 = failFormula.getInt(ref);

        if (StringMaster.isEmpty(success)) {
            success = initStdSuccess(roll_type);
        }
        Formula successFormula = new Formula(success);
        successFormula.applyFactor(getSuccessFactor(roll_type));
        int max2 = successFormula.getInt(ref);

        int min1 = MathMaster.applyMod(max1, DEFAULT_MIN_ROLL_PERC);
        int min2 = MathMaster.applyMod(max2, DEFAULT_MIN_ROLL_PERC);

        if (min1 >= max2) {
            return true;
        }
        if (min2 >= max1) {
            return false;
        }
        // per die?
        rolledValue = RandomWizard.getRandomIntBetween(min1, max1);
        rolledValue2 = RandomWizard.getRandomIntBetween(min2, max2);

        roll.setRolledValue(rolledValue);
        roll.setRolledValue2(rolledValue2);
        result = rolledValue > rolledValue2;

        if (target == null) {
            target = ref.getEvent().getRef().getTargetObj();
        }
        // TODO display roll formulas if FULL_INFO is on!
        if (rollSource == null) {
            rollSource = rolledValue + " out of " + max1;
        } else {
            rollSource += StringMaster.wrapInParenthesis(rolledValue + " out of " + max1);
        }
        String rollTarget = rolledValue2 + " out of " + max2;
        logString = target.getName() + ((result) ? " fails" : " wins") + " a "
                + roll_type.getName() + " roll with " + rollTarget + " vs " + source.getName()
                + "'s " + rollSource;

        if (logAppendix != null) {
            if (isAppendixAdded(logAppendix, result)) {
                logString = logString + logAppendix.replace(getSuccessAppendixIdentifier(), "");
            }
        }
        if (logged == null) {
            logged = checkRollLogged(roll_type, result);
        }
        if (logged) {
            ref.getGame().getLogManager().log(logString);
        }

        ref.getGame().getLogManager();
        ref.getGame().getLogManager();
        if (!ref.isAnimationDisabled()) {
            PhaseAnimation anim = null;
            if (ref.getActive() != null) {
                // else ?
                anim = ((DC_ActiveObj) ref.getActive()).getAnimation();
            }
            if (anim != null) {
                anim.addPhaseArgs(PHASE_TYPE.ROLL, roll);
            }
        }
        roll.setResult(result);
        roll.setLogAppendix(logAppendix);
        roll.setRollSource(rollSource);
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

    private static boolean checkRollLogged(ROLL_TYPES roll_type, Boolean result) {
        if (roll_type == GenericEnums.ROLL_TYPES.STEALTH) {
            return result;
        }
        if (roll_type == GenericEnums.ROLL_TYPES.DETECTION) {
            return result;
        }

        return true;
    }

    private static String getSuccessFactor(ROLL_TYPES roll_type) {
        // TODO LUCK RULE!
        // int mod = ref.getTargetObj().getIntParam(PARAMS.LUCK_MOD);
        // int bonus = ref.getTargetObj().getIntParam(PARAMS.LUCK_BONUS);
        // String successFormula = null;
        // if (!StringMaster.isEmpty(getSuccess()))
        // successFormula = new Formula(getSuccess())
        // .getAppended("+(" + bonus + ")")
        // .getAppendedByFactor(mod).toString();
        // mod = ref.getSourceObj().getIntParam(PARAMS.LUCK_MOD);
        // bonus = ref.getSourceObj().getIntParam(PARAMS.LUCK_BONUS);
        // String failFormula = null;
        // if (!StringMaster.isEmpty(getFail()))
        // failFormula = new Formula(getFail())
        // .getAppended("+(" + bonus + ")")
        // .getAppendedByFactor(mod).toString();

        PARAMETER param = ContentManager.getPARAM(roll_type.name() + "_ROLL_SAVE_BONUS");
        if (param == null) {
            return "0";
        }
        return StringMaster.getValueRef(KEYS.TARGET, param);
    }

    public static boolean checkRollType(ROLL_TYPES rollType, Ref ref) {
        Unit unit = (Unit) ref.getTargetObj();
        if (unit == null) {
            return false;
        }
        boolean result = true;
        switch (rollType) {
            case MASS:
                if (unit.checkPassive(UnitEnums.STANDARD_PASSIVES.IMMATERIAL)) {
                    result = false;
                }
                break;
            case FAITH:
            case MIND_AFFECTING:
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

    private static String getFailFactor(ROLL_TYPES roll_type) {
        PARAMETER param = ContentManager.getPARAM(roll_type.name() + "_ROLL_BEAT_BONUS");
        if (param == null) {
            return "0";
        }
        return StringMaster.getValueRef(KEYS.SOURCE, param);
    }

    public static Rolls generateRollsFromString(String rollString) {
        boolean or = false;
        if (rollString.contains(StringMaster.AND_SEPARATOR)) {
            or = true;
        }
        Rolls rolls = new Rolls(or);
        String separator = StringMaster.AND_SEPARATOR;
        if (!or) {
            separator = StringMaster.AND;
        }
        for (String s : StringMaster.openContainer(rollString, separator)) {
            String varPart = VariableManager.getVarPart(s);
            if (StringMaster.isEmpty(varPart)) {
                rolls.add(new Roll(new EnumMaster<ROLL_TYPES>().retrieveEnumConst(ROLL_TYPES.class,
                        s), null, null));
                continue;
            }
            s = s.replace(varPart, "");
            varPart = StringMaster.cropParenthesises(varPart);
            String success = StringMaster.openContainer(varPart, StringMaster.VAR_SEPARATOR).get(0);
            String fail = StringMaster.openContainer(varPart, StringMaster.VAR_SEPARATOR).get(1);
            ROLL_TYPES type = new EnumMaster<ROLL_TYPES>().retrieveEnumConst(ROLL_TYPES.class, s);
            rolls.add(new Roll(type, success, fail));
        }
        return rolls;
    }

    /*
     * apply *Push* effect always if sufficient force
     * apply *Knockdown* instead plus damage for force if roll (?) failed
      TODO reflex? defense vs force? forceResistance - strength, armor,
         shield,
         rollLogged(roll_type, success, fail, ref, logString, rollSource)
         resist if reflex win
         draw if strength win
     */
    public static Boolean rollForceKnockdown(Unit target, DC_ActiveObj attack, int force) {
        ROLL_TYPES roll_type = GenericEnums.ROLL_TYPES.FORCE;

        String success = "3*" + getStdFormula(GenericEnums.ROLL_TYPES.BODY_STRENGTH, true);
        String fail = "" + force / ForceRule.ROLL_FACTOR;

        // Ref ref = Ref.getSelfTargetingRefCopy(target);
        // ref.setSource(attack.getRef().getSource());
        Boolean result = new Roll(GenericEnums.ROLL_TYPES.FORCE, success, fail, 25).roll(attack.getRef());
        if (result == null) {

        }
        return result;
    }

    public static Boolean makeReactionRoll(Unit unit, DC_ActiveObj action, DC_ActiveObj attack) {
        String fail = StringMaster.getValueRef(KEYS.TARGET, PARAMS.INITIATIVE_MODIFIER) + "/"
                + (1 + action.getIntParam(PARAMS.AP_COST)) + "*"
                + getDexterousModifier(unit, action);

        String success = StringMaster.getValueRef(KEYS.SOURCE, PARAMS.INITIATIVE_MODIFIER) + "/"
                + (1 + attack.getIntParam(PARAMS.CP_COST)) + "*"
                + getVigilanceModifier(unit, action);
        String successTooltip = "to avoid Attack of Opportunity";

        Roll dex = new Roll(GenericEnums.ROLL_TYPES.REACTION, success, fail, 25);
        Ref ref = action.getRef().getCopy();
        ref.setTarget(unit.getId());
        boolean result = !dex.roll(ref);
        if (unit.getGame().isDummyMode()) {
            return true;
        }
        return result;
    }

    public static int getDexterousModifier(Unit unit, DC_ActiveObj action) {
        int mod = 25;
        if (action.checkPassive(UnitEnums.STANDARD_PASSIVES.DEXTEROUS)) {
            mod += 25;
        }
        if (action.getOwnerObj().checkPassive(UnitEnums.STANDARD_PASSIVES.DEXTEROUS)) {
            mod += 25;
        }
        if (WatchRule.checkWatched(action.getOwnerObj(), unit)) {
            mod += 25;
        }
        if (action.getOwnerObj().checkParam(PARAMS.VIGILANCE_MOD)) {
            mod += action.getOwnerObj().getIntParam(PARAMS.VIGILANCE_MOD);
        }

        return mod;

    }

    public static int getVigilanceModifier(Unit unit, DC_ActiveObj action) {
        int mod = 10;
        if (unit.checkPassive(UnitEnums.STANDARD_PASSIVES.VIGILANCE)) {
            mod += 40;
        }
        if (WatchRule.checkWatched(unit, action.getOwnerObj())) {
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

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }

}
