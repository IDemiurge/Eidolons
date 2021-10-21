package eidolons.system;

import eidolons.ability.conditions.req.ClassTreeCondition;
import eidolons.ability.conditions.req.MultiClassCondition;
import eidolons.ability.conditions.req.ValueGroupCondition;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.values.DC_ValueManager;
import eidolons.content.values.DC_ValueManager.VALUE_GROUP;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.core.Core;
import eidolons.game.module.herocreator.HeroManager;
import eidolons.game.module.herocreator.logic.passives.HeroClassMaster;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.SkillEnums;
import main.content.enums.entity.SkillEnums.MASTERY_RANK;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.elements.conditions.*;
import main.entity.Entity;
import main.entity.Ref.KEYS;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.entity.ConditionMaster;
import main.system.launch.CoreEngine;
import main.system.math.Formula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DC_RequirementsManager implements RequirementsManager {
    public final static String OBJ_REF = KEYS.MATCH.toString();
    private static final String OR = " or ";
    private static final String NOT = "!=";
    private static final int VERBATIM_MODE = RequirementsManager.VERBATIM_MODE;

    private Map<Entity, Requirements> reqMap;

    private Map<Entity, Requirements> altReqMap;

    private Entity hero;

    private static MASTERY_RANK getRank(Integer score) {
        MASTERY_RANK rank = SkillEnums.MASTERY_RANK.NONE;
        for (MASTERY_RANK r : SkillEnums.MASTERY_RANK.values()) {
            if (r.getMasteryReq() > score) {
                break;
            }
            rank = r;
        }
        return rank;
    }

    @Override
    public String check(Entity hero, Entity type) {
        return check(hero, type, NORMAL_MODE);
    }

    @Override
    public String check(Entity hero, Entity type, int mode) {
        if (CoreEngine.isArcaneVault()) {
            return null;
        }
        this.setHero(hero);
        String reason;
        Requirements requirements = getRequirements(type, mode);
        if (requirements == null) {
            return null;
        }
        reason = requirements.checkReason(hero.getRef(), type);
        return reason;
    }

    @Override
    public Requirements getRequirements(Entity type, int mode) {

        // if (HqMaster.isDisabled(type)) {
        //     Requirements req = new Requirements();
        //     req.add(new Requirement(new ImpossibleCondition(), "Not available yet, sorry!"));
        // }
        Map<Entity, Requirements> map = getReqMap(mode);
        if (map != null) {
            if (map.get(type) != null) // TODO
            {
                return map.get(type);
            }
        }

        Requirements req = null;
        OBJ_TYPE TYPE = type.getOBJ_TYPE_ENUM();
        if (TYPE instanceof DC_TYPE) {
            switch ((DC_TYPE) TYPE) {

                case ARMOR:
                case WEAPONS:
                case ITEMS:
                    req = generateItemRequirements(type, mode);
                    break;
                case CLASSES:
                    req = generateClassRequirements(type, mode);
                    break;
                case SKILLS:
                    req = generateSkillRequirements(type, mode);
                    break;
                case SPELLS:
                    req = generateSpellRequirements(type, mode);
                    break;
                default:
                    break;
            }
        }

        if (req == null) {
            return null;
        }

        String additionalRequirements = type.getProperty(PROPS.REQUIREMENTS);
        if (!StringMaster.isEmpty(additionalRequirements)) {
            try {
                req.addAll(toRequirements(additionalRequirements));
            } catch (Exception e) {
                LogMaster.log(1, type + "'s req failed! - "
                        + additionalRequirements);
                // main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (type.isUpgrade() && TYPE != null) {
            Requirement baseTypeRequirement = getBaseTypeRequirement(type, TYPE);
            if (baseTypeRequirement != null) {
                req.add(baseTypeRequirement);
            }
        }
        if (map != null) {
            map.put(type, req);
        }
        return req;
    }

    private Requirement getBaseTypeRequirement(Entity type, OBJ_TYPE TYPE) {
        PROPERTY prop = TYPE.getUpgradeRequirementProp();
        if (prop == null) {
            return null;
        }
        Condition condition = ConditionMaster.getPropConditionSourceMatch(prop.toString(),
                G_PROPS.BASE_TYPE.toString());
        String altBases = type.getProperty(PROPS.ALT_BASE_TYPES);
        if (!altBases.isEmpty()) {
            OrConditions orCondition = new OrConditions(condition);
            for (String s : ContainerUtils.open(altBases)) {
                orCondition.add(new PropCondition(PROPS.ALT_BASE_TYPES, s, false));
            }
            condition = orCondition;
        }
        // String typeName = VariableManager.removeVarPart(s);

        return new Requirement(condition, InfoMaster.BASE
                + InfoMaster.getPropReasonString(type.getProperty(G_PROPS.BASE_TYPE), prop));
    }

    private Requirements toRequirements(String string) {
        Requirements requirements = new Requirements();
        for (String subString : ContainerUtils.open(string)) {

            subString = subString.trim();
            if (StringMaster.isEmpty(subString)) {
                continue;
            }
            String t;
            Conditions c;
            if (StringMaster.contains(subString, Strings.OR)) {
                // REFACTOR
                List<String> parts = ContainerUtils.split(subString, Strings.OR, false);
                c = new OrConditions();
                StringBuilder tBuilder = new StringBuilder();
                for (String part : parts) {
                    // String valRef = part
                    // .split(StringMaster.CONDITION_SEPARATOR)[0];
                    // String value =
                    // part.split(StringMaster.CONDITION_SEPARATOR)[1];
                    Condition condition = parseCondition(part);
                    c.add(condition);
                    tBuilder.append(parseReasonString(part)).append(OR);
                }
                t = tBuilder.toString();
                t = t.substring(0, t.length() - OR.length());
            } else {
                String valRef;
                String value;
                boolean not = false;

                if (subString.contains(NOT)) {
                    valRef = subString.split(NOT)[0];
                    value = subString.split(NOT)[1];
                    not = true;
                } else if (!subString.contains(Strings.REQ_VALUE_SEPARATOR)) {
                    // TODO
                    valRef = subString.substring(0, subString.lastIndexOf(" "));
                    value = subString.substring(subString.lastIndexOf(" "))
                            .trim();
                } else {
                    valRef = subString.split(Strings.REQ_VALUE_SEPARATOR)[0];
                    value = subString.split(Strings.REQ_VALUE_SEPARATOR)[1];
                }
                c = new Conditions(getCustomCondition(valRef, value));
                if (not) {
                    c.setNegative(true);
                    value = value + InfoMaster.NEGATIVE_CODE;
                }
                t = getReasonString(valRef, value);
            }

            Requirement req = new Requirement(c, t);
            requirements.add(req);
        }
        return requirements;
    }

    private String getReasonString(String valRef, String value) {
        if (NumberUtils.isInteger(value)) {
            return InfoMaster.getParamReasonString(valRef, value);
        }
        return InfoMaster.getPropReasonString(valRef, value);
    }

    private String parseReasonString(String s) {
        String[] array = getValuePair(s);
        String valRef = array[0];
        String value = array[1];
        if (s.contains(NOT)) {
            return getReasonString(valRef, value + InfoMaster.NEGATIVE_CODE);
        }
        return getReasonString(valRef, value);
    }

    private Condition parseCondition(String s) {
        String[] array = getValuePair(s);
        String valRef = array[0];
        String value = array[1];
        if (s.contains(NOT)) {
            return new NotCondition(getCustomCondition(valRef, value));
        }
        return getCustomCondition(valRef, value);
    }

    private String[] getValuePair(String s) {
        String separator = getSeparator(s);
        String valRef = s.split(separator)[0];
        String value = s.split(separator)[1];
        if (separator.equals(" ")) {
            valRef = s.substring(0, s.lastIndexOf(" "));
            value = s.substring(s.lastIndexOf(" ")).trim();
        }
        return new String[]{valRef, value};
    }

    private String getSeparator(String s) {
        String separator = Strings.REQ_VALUE_SEPARATOR;
        if (s.contains(separator)) {
            return separator;
        }
        separator = NOT;
        if (s.contains(separator)) {
            return separator;
        }
        separator = " ";
        if (s.contains(separator)) {
            return separator;
        }
        return "";
    }

    private Condition getCustomCondition(String valRef, String value) {
        if (StringMaster.contains(valRef, "total")) {
            valRef = StringMaster.replace(true, valRef, "total", "");
            return getTotalCondition(valRef, value);
        }
        VALUE_GROUP template = DC_ValueManager.getValueGroup(valRef);


        if (template == null) {
            if (!checkSimpleValRef(valRef)) {

                LogMaster.log(1, "requirement not found: " + valRef);
            }
            return getCondition(valRef, value);
        }
        // TODO
        // OrConditions conditions = new OrConditions();
        // for (PARAMETER portrait : template.getParams()) {
        // conditions.add(getCondition(portrait.getName(), value));
        // }
        return new ValueGroupCondition(template, value, false);
        // return conditions;
    }

    private Condition getTotalCondition(String req, PARAMETER... params) {
        StringBuilder valRef = new StringBuilder();
        for (PARAMETER param : params) {
            valRef.append(param.getName()).append(Strings.VAR_SEPARATOR);
        }
        return getTotalCondition(valRef.toString(), req);

    }

    private Condition getTotalCondition(String valRef, String value) {
        List<PARAMETER> params;
        String str1;
        if (valRef.contains(Strings.VAR_SEPARATOR)) {
            params = new ArrayList<>();
            for (String s : ContainerUtils.open(valRef, Strings.VAR_SEPARATOR)) {

                PARAMETER p = ContentValsManager.getPARAM(s);
                if (p == null) {
                    p = ContentValsManager.getMastery(s);
                }
                if (p != null) {
                    params.add(p);
                } else {
                    VALUE_GROUP template = DC_ValueManager.getValueGroup(s);
                    params.addAll(new ListMaster<PARAMETER>().getList(template.getParams()));
                }
            }
        } else { // TODO can we use VG_Condition here?
            VALUE_GROUP template = DC_ValueManager.getValueGroup(valRef);
            params = new ListMaster<PARAMETER>().getList(template.getParams());
        }
        StringBuilder str1Builder = new StringBuilder();
        for (PARAMETER p : params) {
            str1Builder.append(StringMaster.getValueRef(KEYS.SOURCE, p)).append("+");
        }
        str1 = str1Builder.toString();
        str1 = StringMaster.cropLast(str1, 1);

        return new NumericCondition(false, str1, value);
    }

    private Condition getCondition(String valRef, String value) {
        if (NumberUtils.isInteger(value)) {
            return ConditionMaster.getParamCondition(valRef, value);
        }
        return new StringComparison("{SOURCE_" + valRef + "}", value, false);
    }

    private boolean checkSimpleValRef(String valRef) {
        return ContentValsManager.isParameterExtendedSearch(valRef)
                || ContentValsManager.isProperty(valRef);
    }

    // TODO preCheck upgrade
    public Requirements generateSpellRequirements(Entity type, int mode) {
        Requirements req = new Requirements();

        String cost = (mode != NORMAL_MODE) ? HeroManager.getCost(type, getHero(), type
                .getOBJ_TYPE_ENUM(), PROPS.VERBATIM_SPELLS) : HeroManager.getCost(type, getHero());

        Condition paramCondition = ConditionMaster.getParamCondition(1, PARAMS.SPELL_POINTS_UNSPENT, PARAMS.CIRCLE);
        ((NumericCondition) paramCondition).setComparingValue(new Formula("" + cost));

        Requirement ptsReq = new Requirement(paramCondition, InfoMaster.getParamReasonString(type,
                PARAMS.SPELL_POINTS_UNSPENT, cost));

        req.add(ptsReq);

        PARAMETER spellMastery = ContentValsManager.getSpellMasteryForSpell(type);

        if (mode != NORMAL_MODE) {
            req.add(new Requirement(ConditionMaster.getParamCondition(spellMastery.getName(), "1",
                    true), InfoMaster.getSpellMasteryReason(spellMastery)));
            if (mode != VERBATIM_MODE) {
                req.add(getParamRequirements(PARAMS.MEMORY_REMAINING, PARAMS.SPELL_DIFFICULTY, type));
            }
            if (type.isUpgrade()) {
                String base = type.getProperty(G_PROPS.BASE_TYPE);
                req.add(new Requirement(ConditionMaster.getPropCondition(KEYS.SOURCE
                        .toString(), PROPS.VERBATIM_SPELLS, base), InfoMaster.SPELL_BASE + base));
            }

            return req;
        }
        if (spellMastery != null) {
            String amount = ""
                    + new Formula("2*"
                    + StringMaster.getValueRef(KEYS.SOURCE, PARAMS.SPELL_DIFFICULTY))
                    .getInt(type.getRef());
            req.add(new Requirement(getTotalCondition(amount, spellMastery, PARAMS.INTELLIGENCE),
                    // new OrConditions(ConditionMaster
                    // .getParamCondition(PARAMS.INTELLIGENCE,
                    // PARAMS.SPELL_DIFFICULTY),
                    // ConditionMaster
                    // .getParamCondition(spellMastery,
                    // PARAMS.SPELL_DIFFICULTY)),
                    InfoMaster.getTotalReasonString
                            // getOrParamReasonString
                                    (amount, PARAMS.INTELLIGENCE, spellMastery))

            );
        }

        // req.add(new Requirement(new NotCondition(new
        // NumericCondition(StringMaster.getValueRef(
        // KEYS.MATCH, PARAMS.SPELL_DIFFICULTY), "0", true)),
        // InfoMaster.UNDER_CONSTRUCTION));
        Requirement unknownReq = new Requirement(new NotCondition(ConditionMaster.getPropCondition(
                PROPS.KNOWN_SPELLS, G_PROPS.NAME, KEYS.SOURCE.toString(), KEYS.MATCH.toString())),
                InfoMaster.SPELL_KNOWN);
        req.add(unknownReq);
        return req;

    }

    public Requirements generateClassRequirements(Entity type, int mode) {
        // preCheck has class of this Base Type of equal or greater Circle
        // multi :
        Requirements requirements = new Requirements();
        for (PARAMS mastery : DC_ContentValsManager.getMasteryParams()) {
            PARAMETER req = ContentValsManager.getReqParam(mastery);
            int param = type.getIntParam(req);
            if (param <= 0) {
                continue;
            }
            Condition c = ConditionMaster.getParamCondition(0, mastery, req);
            String t = InfoMaster.getParamReasonString(type, mastery, req);
            Requirement r = new Requirement(c, t);
            requirements.add(r);
        }
        //TODO check has 1 rank to spend

        if (HeroClassMaster.isMulticlass(type)) {
            // TODO changing to simpler form with baseType?
            requirements.add(getBaseTypeRequirement(type, type.getOBJ_TYPE_ENUM()));
            requirements.add(new Requirement(new PropCondition(PROPS.CLASSES, type
                    .getProperty(PROPS.BASE_CLASSES_TWO), false),
                    InfoMaster.MULTICLASS_SECOND_CLASS
                            + StringMaster.cropLast(type.getProperty(PROPS.BASE_CLASSES_TWO), 2,
                            ";").replace(";", " or ")));

            requirements.add(new Requirement(new MultiClassCondition(type.getName()),
                    InfoMaster.MULTICLASS));

        } else {
            requirements.add(new Requirement(new ClassTreeCondition(type.getName()),
                    InfoMaster.CLASS_TREE));

            Conditions conditions = new OrConditions();
            conditions.add(new EmptyStringCondition(StringMaster.getValueRef(KEYS.SOURCE,
                    PROPS.FIRST_CLASS)));
            conditions.add(new EmptyStringCondition(StringMaster.getValueRef(KEYS.SOURCE,
                    PROPS.SECOND_CLASS)));
            conditions.add(new StringComparison(type.getProperty(G_PROPS.CLASS_GROUP), StringMaster
                    .getValueRef(KEYS.SOURCE, PROPS.FIRST_CLASS), true));
            conditions.add(new StringComparison(type.getProperty(G_PROPS.CLASS_GROUP), StringMaster
                    .getValueRef(KEYS.SOURCE, PROPS.SECOND_CLASS), true));

            requirements.add(new Requirement(conditions, InfoMaster.MAX_CLASSES));
        }
        return requirements;
    }

    private Requirements generateItemRequirements(Entity type, int mode) {
        // str/agi/int req!
        Requirement paramRequirements = getParamRequirements(PARAMS.GOLD, PARAMS.GOLD_COST, type);

        ((NumericCondition) paramRequirements.getCondition()).setComparingValue(new Formula(""
                + HeroManager.getCost(type, getHero())));

        return new Requirements(paramRequirements);
    }


    public Requirements generateSkillRankRequirements(Entity type) {
        Requirements reqs = new Requirements();
        String ptsCost=type.getParam(PARAMS.CIRCLE);
        reqs.add(new Requirement(getCondition(PARAMS.SKILL_POINTS_UNSPENT + "", ptsCost + ""), InfoMaster
                .getParamReasonString(PARAMS.SKILL_POINTS_UNSPENT + "", ptsCost + "")));
        return reqs;

    }

    public Requirements generateSkillRequirements(Entity type, int mode) {
        if (mode == RANK_MODE) {
            return generateSkillRankRequirements(type);
        }
        String cost = HeroManager.getCost(type, getHero());
        Condition ptsReq = ConditionMaster.getParamCondition(1, PARAMS.SKILL_POINTS_UNSPENT, PARAMS.CIRCLE);
        String mastery = type.getProperty(G_PROPS.MASTERY);
        MASTERY_RANK rank = getRank(type.getIntParam("SKILL_DIFFICULTY"));

        Condition rankReq = ConditionMaster.getParamCondition(mastery, rank.getMasteryReq() + "");
        ((NumericCondition) ptsReq).setComparingValue(new Formula("" + cost));
        return new Requirements(new Conditions(ConditionMaster
                .getPropCondition(PROPS.SKILLS, PROPS.SKILL_REQUIREMENTS, KEYS.SOURCE.toString(),
                        KEYS.MATCH.toString()), rankReq, ptsReq
                // TODO OR CONDITION!
                , ConditionMaster.getPropCondition(PROPS.SKILLS, PROPS.SKILL_OR_REQUIREMENTS,
                KEYS.SOURCE.toString(), KEYS.MATCH.toString())),

                InfoMaster.getPropReasonString(type, PROPS.SKILL_REQUIREMENTS), InfoMaster
                .getSkillRankReqString(mastery, type, rank), InfoMaster.getParamReasonString(type,
                PARAMS.SKILL_POINTS_UNSPENT, cost), InfoMaster.getOrReasonStringFromContainer(
                PROPS.SKILL_OR_REQUIREMENTS, type.getProperty(PROPS.SKILL_OR_REQUIREMENTS)));

    }

    public Requirement getParamRequirements(PARAMETER p, PARAMETER p_cost, Entity type) {
        return new Requirement(ConditionMaster.getParamCondition(0, p, p_cost), InfoMaster
                .getParamReasonString(type, p, p_cost));
    }

    // to condition master
    public Map<Entity, Requirements> getReqMap(int mode) {
        if (mode == ALT_MODE) {
            if (altReqMap == null) {
                altReqMap = new HashMap<>();
            }
            return altReqMap;
        }
        if (mode == NORMAL_MODE) {
            if (reqMap == null) {
                reqMap = new HashMap<>();
            }

            return reqMap;

        }
        return null;
    }


    public Entity getHero() {
        if (hero == null) {
            return Core.getMainHero();
        }
        return hero;
    }

    @Override
    public void setHero(Entity hero) {
        this.hero = hero;
    }
}
