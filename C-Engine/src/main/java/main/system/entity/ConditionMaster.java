package main.system.entity;

import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.C_OBJ_TYPE;
import main.content.ContentManager;
import main.content.OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.ability.construct.VariableManager;
import main.elements.conditions.*;
import main.elements.conditions.standard.ClassificationCondition;
import main.elements.conditions.standard.DynamicCondition;
import main.elements.conditions.standard.OwnershipCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.system.auxiliary.ClassMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.Formula;
import main.system.math.PositionMaster;
import main.system.text.TextParser;

import java.util.LinkedList;
import java.util.List;

public class ConditionMaster {

    // public static final String condition_var_separator = "";
    public static final String condition_arg_separator = ",";
    public static final String[] numeric_condition_name_variants = {};
    public static final String[] string_comparison_name_variants = {};
    private static final String MATCH = KEYS.MATCH.name();
    private static final String SOURCE = KEYS.SOURCE.name();
    private static final String EVENT_SOURCE = KEYS.EVENT_SOURCE.name();
    private static ConditionMaster instance;

    public static boolean contains(Conditions c, Class<?> CLASS) {
        for (Condition c1 : c) {
            if (c1 instanceof Conditions) {
                if (contains((Conditions) c1, CLASS)) {
                    return true;
                }
            } else if (c1.getClass().equals(CLASS)) {
                return true;
            }
        }
        return false;
    }

    public static Condition getNotDeadCondition() {
        return new NotCondition(getStatusMatchCondition(StringMaster
                .getWellFormattedString(UnitEnums.STATUS.DEAD.name())));
    }

    public static Condition getAliveAndConsciousFilterCondition() {
        return new Conditions(new NotCondition(new PropCondition(G_PROPS.STATUS, "unconscious")),
                getNotDeadCondition());
    }

    public static Condition getAliveFilterCondition() {
        return getNotDeadCondition();
    }

    public static Condition getAliveCondition(KEYS key) {
        return new NotCondition(getStatusCondition(key, UnitEnums.STATUS.DEAD.toString()));
    }

    public static Condition getBFObjTypesCondition() {
        return new ObjTypeComparison(C_OBJ_TYPE.BF_OBJ, KEYS.MATCH.toString(), true);
    }

    public static Condition getTYPECondition(OBJ_TYPE TYPE) {
        return new ObjTypeComparison((TYPE), KEYS.MATCH.toString(), true);
        // return new StringComparison("{MATCH_" + G_PROPS.TYPE + "}", name,
        // false);
    }

    public static Condition getTargetMatchingParamCondition(String greater, String than) {
        return new NumericCondition(new Formula("{SOURCE_" + greater + "}"), new Formula("{MATCH_"
                + than + "}"), false);
    }

    public static Condition getUnit_Char_BfObj_TerrainTypeCondition() {
        return new ObjTypeComparison(C_OBJ_TYPE.BF, KEYS.MATCH.toString(), true);

    }

    public static Condition getUnit_Char_BfObjTypeCondition() {
        return new ObjTypeComparison(C_OBJ_TYPE.BF_OBJ, KEYS.MATCH.toString(), true);

    }

    public static Condition getUnit_CharTypeCondition() {
        return new ObjTypeComparison(C_OBJ_TYPE.UNITS_CHARS, KEYS.MATCH.toString(), true);

    }

    public static Condition getPropConditionSourceMatch(String s, String s2) {
        return new StringComparison("{SOURCE_" + s + "}", "{MATCH_" + s2 + "}", false);
    }

    public static Condition getPropCondition(String obj_ref, PROPERTY prop, String v, boolean base) {
        if (base) {
            v = StringMaster.FORMULA_BASE_CHAR + v;
        }
        return new StringComparison("{" + obj_ref + "_" + prop.getName() + "}", v, false);
    }

    public static Condition getPropCondition(String obj_ref, PROPERTY prop, String v) {
        return getPropCondition(obj_ref, prop, v, false);
    }

    public static NumericCondition getDistanceFilterCondition(String obj_ref, String distance) {
        return getDistanceCondition(obj_ref, MATCH, (distance));
    }

    public static NumericCondition getDistanceFilterCondition(String obj_ref, Integer distance) {
        return getDistanceFilterCondition(obj_ref, distance + "");
    }

    public static NumericCondition getDistanceFilterCondition(String obj_ref, Integer distance,
                                                              boolean equal) {
        return getDistanceFilterCondition(obj_ref, MATCH, (distance + ""), equal);
    }

    public static NumericCondition getDistanceCondition(String string, String string2,
                                                        String distance) {
        return getDistanceFilterCondition(string, string2, distance, false);
    }

    public static NumericCondition getDistanceFilterCondition(String string, String string2,
                                                              String distance, boolean equal) {
        return new DistanceCondition(distance, string, string2);

    }

    public static Condition getYLineCondition(Obj obj1, Obj obj2, boolean bidirectional) {
        Conditions conditions = new Conditions();
        if (!bidirectional) {
            Condition sideCondition = (!PositionMaster.isAbove(obj1, obj2)) ? new NumericCondition(
                    new Formula("{SOURCE_POS_Y}"), new Formula("{MATCH_POS_Y}"), false)
                    : new NumericCondition(new Formula("{MATCH_POS_Y}"), new Formula(
                    "{SOURCE_POS_Y}"), false);
            conditions.add(sideCondition);
        }
        Condition lineCondition = new NumericCondition(new Formula("{SOURCE_POS_X}"), new Formula(
                "{MATCH_POS_X}"), true);
        conditions.add(lineCondition);

        return conditions;
    }

    public static Condition getXLineCondition(Obj obj1, Obj obj2, boolean bidirectional) {
        Conditions conditions = new Conditions();
        if (!bidirectional) {
            Condition sideCondition = (PositionMaster.isToTheLeft(obj1, obj2)) ? new NumericCondition(
                    new Formula("{MATCH_POS_X}"), new Formula("{SOURCE_POS_X}"), false)
                    : new NumericCondition(new Formula("{SOURCE_POS_X}"), new Formula(
                    "{MATCH_POS_X}"), false);
            conditions.add(sideCondition);
        }
        Condition lineCondition = new NumericCondition(new Formula("{SOURCE_POS_Y}"), new Formula(
                "{MATCH_POS_Y}"), true);

        conditions.add(lineCondition);
        return conditions;

    }

    public static Condition getLineCondition(Obj obj1, Obj obj2, boolean bidirectional) {
        if (PositionMaster.inXLine(obj1, obj2)) {
            return getYLineCondition(obj1, obj2, bidirectional);
        }
        if (PositionMaster.inYLine(obj1, obj2)) {
            return getXLineCondition(obj1, obj2, bidirectional);
        }
        LogMaster.log(1, "getLineCondition: invalid obj positions!");
        return null;

    }

    public static Condition getDiagonalLineCondition(Obj sourceObj, Obj targetObj,
                                                     boolean bidirectional) {
        Conditions conditions = new Conditions();
        if (!bidirectional) {
            Condition sideCondition = (PositionMaster.isToTheLeft(targetObj, sourceObj)) ? new NumericCondition(
                    new Formula("{SOURCE_POS_X}"), new Formula("{MATCH_POS_X}"), false)
                    : new NumericCondition(new Formula("{MATCH_POS_X}"), new Formula(
                    "{SOURCE_POS_X}"), false);
            conditions.add(sideCondition);

            Condition sideCondition2 = (!PositionMaster.isAbove(targetObj, sourceObj)) ? new NumericCondition(
                    new Formula("{MATCH_POS_Y}"), new Formula("{SOURCE_POS_Y}"), false)
                    : new NumericCondition(new Formula("{SOURCE_POS_Y}"), new Formula(
                    "{MATCH_POS_Y}"), false);
            conditions.add(sideCondition2);
        }

        Condition diagonalCondition = new NumericCondition(new Formula(
                "[ABS({MATCH_POS_X} - {SOURCE_POS_X})]"), new Formula(
                "[ABS({MATCH_POS_Y} - {SOURCE_POS_Y})]"), true);
        conditions.add(diagonalCondition);
        return conditions;
    }

    private static Condition getStatusMatchCondition(String status) {
        return new StringComparison("{MATCH_STATUS}", status, false);
    }

    private static Condition getStatusCondition(KEYS key, String status) {
        return new StringComparison("{" + key.toString() + "_STATUS}", status, false);
    }

    public static Condition getEnemyCondition() {
        return (new OwnershipCondition(true, MATCH, SOURCE));
    }

    public static Condition getAllyCondition() {
        return (new OwnershipCondition(MATCH, SOURCE));
    }

    public static Condition getOwnershipFilterCondition(String name, boolean ally) {
        return (ally) ? new OwnershipCondition(MATCH, name) : new NotCondition(
                new OwnershipCondition(MATCH, name));

    }

    public static Condition getSelfTriggerCondition() {

        return new RefCondition(EVENT_SOURCE, SOURCE, false);
    }

    public static Condition getSelfFilterCondition() {
        return new RefCondition(MATCH, SOURCE, false);
    }

    public static Condition getAttackModifierConditions() {
        Conditions c = new Conditions();
        c.add(getSelfTriggerCondition());

        return c;
    }

    public static Condition getAttackConditions() {
        return new Conditions(new NotCondition(getSelfFilterCondition()), getBFObjTypesCondition(),
                getDistanceFilterCondition(SOURCE, "{ACTIVE_RANGE}"));
    }

    public static Condition getAdjacentCondition() {
        return getDistanceFilterCondition(SOURCE, 1);
    }

    public static Condition getMoraleAffectedFilterCondition() {
        return getMoraleAffectedCondition(KEYS.MATCH);

    }

    public static Condition getPassiveRetainCondition() {
        return new StringComparison("{SOURCE_PASSIVES}", "{ABILITY_NAME}", false);
    }

    public static Condition getGraveConditions() {
        Conditions c = new Conditions();
        c.add(getTYPECondition(DC_TYPE.TERRAIN));
        c.add(new NumericCondition("{MATCH_" + G_PARAMS.N_OF_CORPSES + "}", "0", false));
        return c;
    }

    public static Conditions getClaimedBfObjConditions(String BF_OBJECT_TYPE) {
        Conditions conditions = new Conditions();
        conditions.add(ConditionMaster.getPropCondition("MATCH", G_PROPS.BF_OBJECT_TYPE,
                BF_OBJECT_TYPE));
        conditions.add(ConditionMaster.getTYPECondition(DC_TYPE.BF_OBJ));

        conditions.add(new OwnershipCondition(KEYS.MATCH, KEYS.SOURCE));

        return conditions;
    }

    public static Condition getPropCondition(PROPERTY p, PROPERTY p2, String firstReference,
                                             String secondReference) {
        return getPropCondition(p, p2, firstReference, secondReference, true);
    }

    public static Condition getPropCondition(PROPERTY p, PROPERTY p2, String firstReference,
                                             String secondReference, boolean strict) {
        return new StringContainersComparison(strict, "{" + firstReference + "_" + p.getName()
                + "}", "{" + secondReference + "_" + p2.getName() + "}", false); // harken
        // thee
        // -
        // name
        // your
        // variables
        // wisely
        // lest
        // they
        // turn
        // into
        // thy
        // tormentors
    }

    /**
     * @param mod required value multiplier
     * @param p   source obj param
     * @param p2  match obj param
     * @return
     */
    public static Condition getParamCondition(double mod, PARAMETER p, PARAMETER p2) {
        return getParamCondition(mod, p, p2, 0, SOURCE, MATCH);
    }

    /**
     * @param mod required value multiplier
     * @param add required value addendum
     * @return
     */
    public static Condition getParamCondition(double mod, PARAMETER p, PARAMETER p2, double add,
                                              String OBJ_REF, String OBJ_REF2) {
        if (mod == 0) {
            mod = 1;
        }
        return new NumericCondition(false, "{" + OBJ_REF + "_" + p.getName() + "}", mod + "*{"
                + OBJ_REF2 + "_" + p2.getName() + "}+" + add);
    }

    public static Condition getParamCondition(PARAMETER p, PARAMETER p2) {
        return getParamCondition(p.getName(), "{" + MATCH + "_" + p2.getName() + "}");
    }

    public static Condition getParamCondition(String paramName, String amount, boolean base) {
        return new NumericCondition(false, "{" + ((base) ? StringMaster.BASE_CHAR : "") + SOURCE
                + "_" + paramName + "}", amount);
    }

    public static Condition getParamCondition(String paramName, String amount) {
        return getParamCondition(paramName, amount, false);
    }

    public static Condition getMoraleAffectedCondition(KEYS target) {
        return getLivingCondition(target.toString());
    }

    // public static final String

    public static Condition getLivingCondition(String key) { // TODO |
        return new NotCondition(new OrConditions(new ClassificationCondition(UnitEnums.CLASSIFICATIONS.WRAITH
                .toString(), key), new OrConditions(new ClassificationCondition(
                UnitEnums.CLASSIFICATIONS.CONSTRUCT.toString(), key), new ClassificationCondition(
                UnitEnums.CLASSIFICATIONS.ELEMENTAL.toString(), key), new ClassificationCondition(
                UnitEnums.CLASSIFICATIONS.STRUCTURE.toString(), key), new ClassificationCondition(
                UnitEnums.CLASSIFICATIONS.UNDEAD.toString(), key), new ClassificationCondition(
                UnitEnums.CLASSIFICATIONS.MECHANICAL.toString(), key))));
    }

    public static Condition getLivingMatchCondition() {
        return getLivingCondition(KEYS.MATCH.toString());
    }

    public static Condition getHasPassiveCondition(KEYS ref, String name) {
        return new StringComparison(StringMaster.getValueRef(ref, G_PROPS.PASSIVES), name, false);
    }

    public static boolean checkStringCondition(String condition, Ref ref) {
        return toConditions(condition).preCheck(ref);
    }

    public static Condition toConditions(String string) {
        Conditions conditions = new Conditions();
        for (String conditionString :
         StringMaster.openContainer(string, StringMaster.AND)) {
            Condition c;
            if (conditionString.contains(StringMaster.OR)) {
                String[] parts = conditionString.split(StringMaster.OR);
                c = new OrConditions();
                for (String part : parts) {
                    Condition condition = parseCondition(part);
                    ((OrConditions) c).add(condition);
                }

            } else {
                c = parseCondition(conditionString);
            }
            conditions.add(c);
        }

        return conditions;
    }

    private static Condition parseTemplateCondition(String string) {
        // TODO use std templates, e.g. "Living"
        String vars = (VariableManager.getVarPart(string));
        String templateName = string.replace(vars, "");
        vars = StringMaster.cropParenthesises(vars);
        String[] args = vars.split(condition_arg_separator);
        if (args.length < 2) {
            return null;
        }

        boolean negative = false;
        if (templateName.contains("!") || templateName.contains("not")) {
            templateName = templateName.replace("!", "").replace("not", "");
            if (StringMaster.isEmpty(templateName)) {
                templateName = CONDITION_TEMPLATES.STRING.toString();
            }
            negative = true;
        }
        CONDITION_TEMPLATES template = new EnumMaster<CONDITION_TEMPLATES>().retrieveEnumConst(
                CONDITION_TEMPLATES.class, templateName);
        if (template == null) {
            return null;
        }
        String str1 = args[0];
        String str2 = args[1];
        str1 = parseArg(str1);
        str2 = parseArg(str2); // different for each template?! TODO
        Condition c = getInstance().getConditionFromTemplate(template, str1, str2);
        if (negative) {
            return new NotCondition(c);
        }
        return c;
    }

    private static String parseArg(String arg) {
        if (TextParser.isRef(arg)) { // TODO sometimes it's SOURCE!!!
            if (!arg.contains(StringMaster.FORMULA_REF_SEPARATOR)) {
                arg = KEYS.MATCH.toString() + StringMaster.FORMULA_REF_SEPARATOR
                        + StringMaster.cropRef(arg);
                arg = StringMaster.wrapInCurlyBraces(arg);
            }
        }
        return arg;
    }

    public static String checkAddRef(String prop) {
        boolean result = false;
        if (ContentManager.getPROP(prop) != null) {
            result = true;
        }
        if (!result) {
            if (ContentManager.getPARAM(prop) != null) {
                result = true;
            }
        }
        if (result) {
            prop = KEYS.MATCH.toString() + StringMaster.FORMULA_REF_SEPARATOR + prop;
        }
        return prop;
    }

    private static Condition parseCondition(String string) {
        Condition c = parseTemplateCondition(string);
        if (c != null) {
            return c;
        }

        c = findConditionTemplate(string);
        if (c != null) {
            return c;
        }

        // String vars = VariableManager.getVarPart(string);
        // String className = string.replace(vars, "");
        // className = getMappedClassName(className);
        // List<Construct> constructs = new LinkedList<>();
        //
        // for (String var : vars.split(condition_arg_separator)) {
        // // primitives only! but for those, I gotta get the arg classes right
        // // for constructor...
        // constructs.add(new Construct(var, true));
        // }
        // Construct conditionConstruct = new Construct(className, constructs);
        //
        // Condition condition = (Condition) conditionConstruct.construct();

        return c;
    }

    public static Condition getWorkspaceCondition(boolean negative,
                                                  WORKSPACE_GROUP... filterWorkspaceGroup) {
        String value = "";
        for (WORKSPACE_GROUP ws : filterWorkspaceGroup) {
            value += ws.toString();
        }
        return getPropCondition(KEYS.MATCH.toString(), G_PROPS.WORKSPACE_GROUP, value);
    }

    public static List<Condition> removeConditionsOfClass(Conditions conditions, Class<?> clazz) {
        List<Condition> list = new LinkedList<>();
        for (Condition c : new Conditions(conditions)) {
            if (c instanceof Conditions) {
                removeConditionsOfClass((Conditions) c, clazz);
            } else if (ClassMaster.isInstanceOf(c, clazz)) {
                list.add(c);
                conditions.remove(c);
            }

        }
        return list;
    }

    public static Conditions getFilteredConditions(Conditions conditions,
                                                   Class<?>... removedClasses) {
        Conditions result = new Conditions();
        if (conditions instanceof OrConditions) {
            result = new OrConditions();
        }
        loop:
        for (Condition c1 : conditions) {
            if (c1 instanceof Conditions) {
                result.add(getFilteredConditions((Conditions) c1, removedClasses));
            } else {
                for (Class<?> CLASS : removedClasses) {
                    if (ClassMaster.isInstanceOf(c1, CLASS)) {
                        continue loop;
                    }
                }
                result.add(c1);
            }
        }
        return result;
    }

    private static Condition findConditionTemplate(String string) {
        TARGETING_MODE TARGETING_MODE = new EnumMaster<TARGETING_MODE>().retrieveEnumConst(
                TARGETING_MODE.class, string);
        // in DC_ only...
        return null;
    }

    private static String getMappedClassName(String className) {
        // TODO or i could actually map to constructors directly! Annotate

        NumericCondition.class.getConstructors();
        return null;
    }

    public static Condition getStdRaiseConditions() {
        // TODO anything else?
        return getLivingMatchCondition();
    }

    public static Condition getItemBaseTypeFilterCondition() {
        return new EmptyStringCondition(StringMaster.getValueRef(KEYS.MATCH, G_PROPS.BASE_TYPE));
    }

    public static ConditionMaster getInstance() {
        if (instance == null) {
            instance = new ConditionMaster();
        }
        return instance;
    }

    public static void setInstance(ConditionMaster instance) {
        ConditionMaster.instance = instance;
    }

    public Condition getConditionFromTemplate(CONDITION_TEMPLATES template, String str1, String str2) {
        Condition c = null;
        switch (template) {
            case INVALID_ABILITIES:
                return new InvalidCondition();
            case REF:
                c = new RefCondition(str1, str2);
                break;
            case CONTAINER:
                c = new StringContainersComparison(str1, str2);
                break;
            case CONTAINER_STRICT:
                c = new StringContainersComparison(str1, str2, true);
                break;
            case NUMERIC:
                c = new NumericCondition(str1, str2);
                break;
            case NUMERIC_EQUAL:
                c = new NumericCondition(str1, str2, true);
                break;
            case NUMERIC_LESS:
                c = new NotCondition(new NumericCondition(str1, str2));
                break;
            case STRING:
                c = new StringComparison(str1, str2, false);
                break;
            case STRING_STRICT:
                c = new StringComparison(str1, str2, true);
                break;

            case CAN_ACTIVATE:
                c = new DynamicCondition("CostCondition;" + str1 + "");
        }
        return c;
    }

    public Condition getDynamicCondition(String s) {

        return null;
    }

    public enum CONDITION_SHORTCUTS {
isMe("ref(source, match)"),
        ;
CONDITION_SHORTCUTS(String s){

}

    }
        public enum CONDITION_TEMPLATES {
        STRING("string", "fullString", "has"),
        STRING_STRICT("strStrict", "strEqual", "strict"),
        CONTAINER("contains"),
        CONTAINER_STRICT("containerStrict"),
        REF("ref", "refCheck"),
        NUMERIC("num", "numeric", "greater"),
        NUMERIC_EQUAL("numEqual", "numeric equal", "equal"),
        NUMERIC_LESS("numLess", "numeric less", "less"),
        ITEM("item", "slot"),
        INVALID_ABILITIES("item", "slot"),
        CAN_ACTIVATE("cost", "can activate", "can pay");

        private String[] names;

        CONDITION_TEMPLATES(String... names) {
            this.names = names;

        }

        public boolean matchString(String string) {
            for (String name : names) {
                if (string.equalsIgnoreCase(name)) {
                    return true;
                }
            }
            for (String name : names) {
                if (StringMaster.compare(string, name)) {
                    return true;
                }
            }
            // TODO
            return false;
        }
    }

}
