package main.system;

import main.ability.conditions.req.ClassTreeCondition;
import main.ability.conditions.req.MultiClassCondition;
import main.ability.conditions.req.SkillPointCondition;
import main.ability.conditions.req.ValueGroupCondition;
import main.client.cc.CharacterCreator;
import main.client.cc.HeroManager;
import main.client.cc.gui.views.ClassView;
import main.content.*;
import main.content.DC_ValueManager.VALUE_GROUP;
import main.content.enums.entity.SkillEnums;
import main.content.enums.entity.SkillEnums.MASTERY_RANK;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.XLinkedMap;
import main.elements.conditions.*;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.attach.DC_FeatObj;
import main.game.core.game.DC_Game;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.entity.ConditionMaster;
import main.system.launch.CoreEngine;
import main.system.math.Formula;

import java.util.HashMap;
import java.util.LinkedList;
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
    private Map<Entity, Requirements> rankReqMap;

    public DC_RequirementsManager(DC_Game game) {

    }

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

    // private String specialCheck(Entity type, Entity heroObj) {
    // switch ((OBJ_TYPES) type.getOBJ_TYPE_ENUM()) {
    // case SKILLS:
    // PARAMETER masteryParam =
    // ContentManager.getPARAM(type.getProperty(G_PROPS.MASTERY));
    // if (masteryParam == null)
    // masteryParam =
    // ContentManager.getMastery(type.getProperty(G_PROPS.MASTERY));
    // if (masteryParam != null) {
    // DC_HeroObj hero = (DC_HeroObj) heroObj;
    // int result = DC_MathManager.getFreeMasteryPoints(hero, masteryParam)
    // - type.getIntParam(PARAMS.SKILL_DIFFICULTY);
    // if (result < 0)
    // return type.getProperty(G_PROPS.MASTERY)
    // + InfoMaster.NOT_ENOUGH_MASTERY_SLOTS + (-result);
    // }
    // }
    // return null;
    // }

    @Override
    public String check(Entity hero, Entity type) {
        return check(hero, type, NORMAL_MODE);
    }

    @Override
    public String check(Entity hero, Entity type, int mode) {
        if (CoreEngine.isArcaneVault()) {
            return null;
        }
        // Chronos.mark(type.getName() + " req preCheck");
        this.setHero(hero);
        String reason;
        // specialCheck(type, hero);
        // if (reason != null)
        // return reason;

        // Chronos.mark(type.getName() + " req build");
        Requirements requirements = getRequirements(type, mode);
        // Chronos.logTimeElapsedForMark(type.getName() + " req build");
        if (requirements == null) {
            return null;
        }
        reason = requirements.checkReason(hero.getRef(), type);
        // Chronos.logTimeElapsedForMark(type.getName() + " req preCheck");
        return reason;
    }

    @Override
    public Requirements getRequirements(Entity type, int mode) {

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
                    req = generateItemRequirements(type, mode);
                    break;
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
                case WEAPONS:
                    req = generateItemRequirements(type, mode);
                    break;
                case UNITS:
                    break;
                case ACTIONS:
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
                // e.printStackTrace();
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
            for (String s : StringMaster.openContainer(altBases)) {
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
        for (String subString : StringMaster.openContainer(string)) {

            subString = subString.trim();
            if (StringMaster.isEmpty(subString)) {
                continue;
            }
            String t = "";
            Conditions c;
            if (StringMaster.contains(subString, StringMaster.OR)) {
                // REFACTOR
                List<String> parts = StringMaster.split(subString, StringMaster.OR, false);
                c = new OrConditions();
                for (String part : parts) {
                    // String valRef = part
                    // .split(StringMaster.CONDITION_SEPARATOR)[0];
                    // String value =
                    // part.split(StringMaster.CONDITION_SEPARATOR)[1];
                    Condition condition = parseCondition(part);
                    c.add(condition);
                    t += parseReasonString(part) + OR;
                }
                t = t.substring(0, t.length() - OR.length());
            } else {
                String valRef;
                String value;
                boolean not = false;

                if (subString.contains(NOT)) {
                    valRef = subString.split(NOT)[0];
                    value = subString.split(NOT)[1];
                    not = true;
                } else if (!subString.contains(StringMaster.REQ_VALUE_SEPARATOR)) {
                    // TODO
                    valRef = subString.substring(0, subString.lastIndexOf(" "));
                    value = subString.substring(subString.lastIndexOf(" "), subString.length())
                            .trim();
                } else {
                    valRef = subString.split(StringMaster.REQ_VALUE_SEPARATOR)[0];
                    value = subString.split(StringMaster.REQ_VALUE_SEPARATOR)[1];
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
        if (StringMaster.isInteger(value)) {
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
            value = s.substring(s.lastIndexOf(" "), s.length()).trim();
        }
        return new String[]{valRef, value};
    }

    private String getSeparator(String s) {
        String separator = StringMaster.REQ_VALUE_SEPARATOR;
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
        // for (PARAMETER p : template.getParams()) {
        // conditions.add(getCondition(p.getName(), value));
        // }
        return new ValueGroupCondition(template, value, false);
        // return conditions;
    }

    private Condition getTotalCondition(String req, PARAMETER... params) {
        String valRef = "";
        for (PARAMETER param : params) {
            valRef += param.getName() + StringMaster.VAR_SEPARATOR;
        }
        return getTotalCondition(valRef, req);

    }

    private Condition getTotalCondition(String valRef, String value) {
        List<PARAMETER> params;
        String str1 = "";
        if (valRef.contains(StringMaster.VAR_SEPARATOR)) {
            params = new LinkedList<>();
            for (String s : StringMaster.openContainer(valRef, StringMaster.VAR_SEPARATOR)) {

                PARAMETER p = ContentManager.getPARAM(s);
                if (p == null) {
                    p = ContentManager.getMastery(s);
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
        for (PARAMETER p : params) {
            str1 += StringMaster.getValueRef(KEYS.SOURCE, p) + "+";
        }
        str1 = StringMaster.cropLast(str1, 1);

        return new NumericCondition(false, str1, value);
    }

    private Condition getCondition(String valRef, String value) {
        if (StringMaster.isInteger(value)) {
            return ConditionMaster.getParamCondition(valRef, value);
        }
        return new StringComparison("{SOURCE_" + valRef + "}", value, false);
    }

    private boolean checkSimpleValRef(String valRef) {
        return ContentManager.isParameterExtendedSearch(valRef)
                || ContentManager.isProperty(valRef);
    }

    // TODO preCheck upgrade
    public Requirements generateSpellRequirements(Entity type, int mode) {
        Requirements req = new Requirements();

        String cost = (mode != NORMAL_MODE) ? HeroManager.getCost(type, getHero(), type
                .getOBJ_TYPE_ENUM(), PROPS.VERBATIM_SPELLS) : HeroManager.getCost(type, getHero());

        Condition paramCondition = ConditionMaster.getParamCondition(1, PARAMS.XP, PARAMS.XP_COST);
        ((NumericCondition) paramCondition).setComparingValue(new Formula("" + cost));

        Requirement xpReq = new Requirement(paramCondition, InfoMaster.getParamReasonString(type,
                PARAMS.XP, cost));

        req.add(xpReq);

        PARAMETER spellMastery = ContentManager.getSpellMasteryForSpell(type);

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
        if (mode == RANK_MODE) {
            return generateClassRankRequirements(type);
        }
        Requirements requirements = new Requirements();

        for (PARAMS mastery : DC_ContentManager.getMasteryParams()) {
            PARAMETER req = ContentManager.getReqParam(mastery);
            int param = type.getIntParam(req);
            if (param <= 0) {
                continue;
            }
            Condition c = ConditionMaster.getParamCondition(0, mastery, req);
            String t = InfoMaster.getParamReasonString(type, mastery, req);
            Requirement r = new Requirement(c, t);
            requirements.add(r);
        }
        String cost = HeroManager.getCost(type, getHero());

        Requirement xpReq = new Requirement(ConditionMaster.getParamCondition(0, PARAMS.XP,
                PARAMS.XP_COST), InfoMaster.getParamReasonString(type, PARAMS.XP, cost));
        ((NumericCondition) xpReq.getCondition()).setComparingValue(new Formula("" + cost));

        requirements.add(xpReq);

        if (ClassView.isMulticlass(type)) {
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

    public Requirements generateClassRankRequirements(Entity type) {
        Requirements reqs = getRequirements(type, 0);
        Map<String, Condition> reqMap = new XLinkedMap<>();
        Requirements rankedReqs = new Requirements(reqMap);
        for (String string : reqs.getReqMap().keySet()) {
            Condition req = reqs.getReqMap().get(string);
            String tip = new MapMaster<String, Condition>().getKeyForValue(reqs.getReqMap(), req);
            modifyRankReq(type, reqs, tip, reqMap, req, type.getIntParam(PARAMS.RANK));
        }
        rankedReqs = new Requirements(reqMap);
        return rankedReqs;

    }

    public Requirements generateSkillRankRequirements(Entity type) {
        Requirements reqs = new Requirements();

        int xpCost = type.getIntParam(PARAMS.XP_COST) * type.getIntParam(PARAMS.RANK_XP_MOD) / 100;

        reqs.add(new Requirement(getCondition(PARAMS.XP + "", xpCost + ""), InfoMaster
                .getParamReasonString(PARAMS.XP + "", xpCost + "")));
        reqs.add(new Requirement(new SkillPointCondition(), InfoMaster.NOT_ENOUGH_MASTERY));
        return reqs;

    }

    public Requirements generateSkillRequirements(Entity type, int mode) {
        if (mode == RANK_MODE) {
            return generateSkillRankRequirements(type);
        }
        String cost = HeroManager.getCost(type, getHero());
        Condition xpReq = ConditionMaster.getParamCondition(1, PARAMS.XP, PARAMS.XP_COST);
        String mastery = type.getProperty(G_PROPS.MASTERY);
        MASTERY_RANK rank = getRank(type.getIntParam("SKILL_DIFFICULTY"));

        Condition rankReq = ConditionMaster.getParamCondition(mastery, rank.getMasteryReq() + "");
        ((NumericCondition) xpReq).setComparingValue(new Formula("" + cost));
        return new Requirements(new Conditions(new SkillPointCondition(), ConditionMaster
                .getPropCondition(PROPS.SKILLS, PROPS.SKILL_REQUIREMENTS, KEYS.SOURCE.toString(),
                        KEYS.MATCH.toString()), rankReq, xpReq
                // TODO OR CONDITION!
                , ConditionMaster.getPropCondition(PROPS.SKILLS, PROPS.SKILL_OR_REQUIREMENTS,
                KEYS.SOURCE.toString(), KEYS.MATCH.toString())),

                InfoMaster.NOT_ENOUGH_MASTERY,

                InfoMaster.getPropReasonString(type, PROPS.SKILL_REQUIREMENTS), InfoMaster
                .getSkillRankReqString(mastery, type, rank), InfoMaster.getParamReasonString(type,
                PARAMS.XP, cost), InfoMaster.getOrReasonStringFromContainer(
                PROPS.SKILL_OR_REQUIREMENTS, type.getProperty(PROPS.SKILL_OR_REQUIREMENTS)));

    }

    public Requirement getParamRequirements(PARAMETER p, PARAMETER p_cost, Entity type) {
        return new Requirement(ConditionMaster.getParamCondition(0, p, p_cost), InfoMaster
                .getParamReasonString(type, p, p_cost));
    }

    // to condition master
    public Map<Entity, Requirements> getReqMap(int mode) {
        if (mode == RANK_MODE) {
            if (rankReqMap == null) {
                rankReqMap = new HashMap<>();
            }
            return rankReqMap;
        }
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

    public List<String> checkRankReqs(DC_FeatObj feat) {
        Requirements reqs = getRequirements(feat, RANK_MODE);
        Ref ref = new Ref(feat.getOwnerObj());
        ref.setMatch(feat.getId());
        reqs.check(ref, true);
        return reqs.getReasons();
    }

    private Requirements modifyRankReq(Entity feat, Requirements reqs, String tip,
                                       Map<String, Condition> reqMap, Condition condition, int rank) {
        if (condition instanceof Conditions) {
            Conditions conditions = (Conditions) condition;
            OrConditions orCondition = null;
            String[] tipParts = null;
            if (tip.contains(" or ")) {
                orCondition = new OrConditions();
                tipParts = tip.split(" or ");
            }
            String reqTip = tip;
            int i = 0;
            String prevValue = null;
            for (Condition c : conditions) {
                if (orCondition != null) {
                    if (reqTip.equals(tip)) {
                        reqTip = "";
                    }
                    Requirements modifiedReqs = modifyRankReq(feat, reqs, tipParts[i],
                            new HashMap<>(), c, rank);
                    // NumericCondition numericCondition = (NumericCondition) c;
                    // String value =
                    // StringMaster.getLastPart(numericCondition.getComparingValue()
                    // .toString(), " ");
                    // // TODO OR if param changed!
                    // if (prevValue != null)
                    // if (!prevValue.equals(value))
                    // i++;

                    for (String t : modifiedReqs.getReqMap().keySet()) {
                        Condition c2 = modifiedReqs.getReqMap().get(t);
                        orCondition.add(c2);
                        // if (prevValue != null)
                        // if (!prevValue.equals(value))
                        reqTip += t + " or ";
                    }
                    i++;
                    // prevValue = value;
                } else {
                    modifyRankReq(feat, reqs, reqTip, reqMap, c, rank);
                }
            }
            if (orCondition != null) {
                reqMap.put(StringMaster.cropLast(reqTip, 4), orCondition);
            }

        } else if (condition instanceof NumericCondition) {
            NumericCondition numericCondition = (NumericCondition) condition;
            Formula f = new Formula(numericCondition.getComparingValue().toString());
            String originalValue = "" + f.getInt();
            Integer mod = (rank + 1) * feat.getIntParam(PARAMS.RANK_SD_MOD);
            if (tip.contains("Xp")) {
                f.applyModifier(feat.getIntParam(PARAMS.RANK_XP_MOD));
            } else {
                f.applyFactor(mod);
            }
            String value = "" + f.getInt();

            // TODO if Conditions, how to getOrCreate the tip?

            // String tip = new MapMaster<String,
            // Condition>().getKeyForValue(reqs.getReqMap(), req);
            // try {
            // tip =
            // InfoMaster.getModifiedParamReasonString(numericCondition.getComparedValue()
            // .toString(), value);
            // } catch (Exception e) {
            // tip.replace(originalValue, value);
            // }
            if (tip.contains("otal "))// total...
            {
                tip.replace(originalValue, value);
            }
            tip = tip.replace(originalValue, value);

            LogMaster.log(1, tip + " for " + feat + " = " + value);

            numericCondition.setComparingValue(new Formula(value));
            reqs = new Requirements(new Requirement(numericCondition, tip));
            reqMap.put(tip, condition);

        }
        return reqs;
    }

    public Entity getHero() {
        if (hero == null) {
            return CharacterCreator.getHero();
        }
        return hero;
    }

    @Override
    public void setHero(Entity hero) {
        this.hero = hero;
    }
}
