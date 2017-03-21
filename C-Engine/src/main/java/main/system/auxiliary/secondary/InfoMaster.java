package main.system.auxiliary.secondary;

import main.content.enums.entity.SkillEnums.MASTERY_RANK;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.ContentManager;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.math.Parameter;

public class InfoMaster {
    public static final String SKILL_RANK_REQUIRED = " rank required: ";
    public static final String TOOLTIP_SEPARATOR = ": ";
    public final static String PARAM_REASON_STRING = " needed: ";
    public final static String PROP_REASON_STRING = " required: ";
    public final static String NEGATIVE_REASON_STRING = " prohibited: ";
    public final static String NEGATIVE_PROP_REASON_STRING = " must not be ";
    public final static String SPELL_KNOWN = "This spell is already known to the hero.";
    public static final CharSequence ITEM_REASON_BLOCKED = "tadan!";
    public static final String MAX_CLASSES = "Hero already has maximum of two base classes!";
    public static final String UNDER_CONSTRUCTION = "Under construction!";
    public static final String COOLDOWN_REASON = "Wait for cooldown!";
    public static final String CHOOSE_PARTY = "Select a party";
    public static final String CHOOSE_MEMBER = "Choose a new party member";
    public static final String CHOOSE_DEITY = "Choose a Deity";
    public static final String CHOOSE_DUNGEON = "Choose a dungeon to explore";
    public static final String CHOOSE_EMBLEM = "Choose an emblem";
    public static final String CHOOSE_BACKGROUND = "Choose a background";
    public static final String CHOOSE_DEITY_PRINCIPLE = "Choose a Principle of hero's free will...";
    public static final String CHOOSE_PRINCIPLE = "Choose a Principle of hero's conditioning...";
    public static final String CHOOSE_PORTRAIT = "Choose a portrait";
    public static final String INPUT_HERO_NAME = "Who is this mysterious new hero?";
    public static final String NEGATIVE_CODE = "NIGGGAAATIIIFFF!!!";
    public static final String CLASS_TREE = "Hero already chose another class path!";
    public static final String MULTICLASS_SECOND_CLASS = "Secondary class required: ";
    public static final String MULTICLASS = "Hero does not have the right class combination for this multiclass!";

    public static final String MAX_CLASS_NUMBER = "Hero has maximum number of base classes. "
            + "Two base classes can be merged into a Multiclass.";
    public static final String NOT_ENOUGH_MASTERY_SLOTS = "Mastery needs more points invested: ";
    // public static final String NOT_ENOUGH_MASTERY_SLOTS =
    // "Additional skill points required: ";
    public static final String NOT_ENOUGH_MASTERY = "Need more Mastery points invested!";
    public static final String SPELL_BASE = "Base spell version required: ";
    public static final String SILENCE = "Cannot cast while Silenced!";
    public static final String CHOOSE_SOUNDSET = "Choose your personality";
    public static final String MIDDLE_HERO =
            // "Select the hero to be in the middle of party formation";
            "Select the middle hero of party formation";
    public static final String CHOOSE_ARCADE = "Select arcade to continue";
    public static final String CHOOSE_HERO = "Select a leader";
    public static final String CHOOSE_SCENARIO = "Select a Scenario";
    public static final String BASE = "[base]";
    // public static final String NOT_ENOUGH_MASTERY =
    // "Not enough skill points!";
    private static final String OR = " or ";

    public static String getSpellMasteryReason(PARAMETER spellMastery) {
        return "Must have " + spellMastery.getName() + " unlocked!";
    }

    public static String getPropReasonString(Entity type, PROPERTY p) {
        return p.getName()
                + PROP_REASON_STRING
                + StringMaster.cropLast(type.getProperty(p).replace(
                StringMaster.CONTAINER_SEPARATOR, StringMaster.VAR_SEPARATOR + " "), 2);
    }

    public static String getParamReasonString(Entity type, PARAMETER p, PARAMETER p_cost) {
        return p.getName() + PARAM_REASON_STRING + type.getParam(p_cost);
    }

    public static String getParamReasonString(Entity type, PARAMETER p, String amount) {
        return p.getName() + PARAM_REASON_STRING + amount;

    }

    public static String getPropReasonString(String val, PROPERTY p) {
        return getPropReasonString(p.getName(), val);
    }

    public static String getPropReasonString(String valName, String val) {
        if (val.contains(NEGATIVE_CODE)) {
            return getNegativePropReasonString(valName, val);
        }
        return valName + PROP_REASON_STRING + val;
    }

    public static String getNegativePropReasonString(String valName, String val) {
        return valName + NEGATIVE_REASON_STRING + val.replace(NEGATIVE_CODE, "");

    }

    public static String getParamReasonString(String valRef, String amount) {
        return valRef + PARAM_REASON_STRING + amount;
    }

    public static String getParamReasonString(String val, PARAMETER p) {
        return p.getName() + PARAM_REASON_STRING + val;
    }

    public static String getOrReasonStringFromContainer(PROPERTY p, String value) {
        if (value.isEmpty()) {
            return "";
        }

        String string = "";
        for (String s : StringMaster.openContainer(value)) {
            string += s + OR;
        }
        string = string.substring(0, string.length() - OR.length());
        return p.getName().replace("Or ", "").replace("Requirements ", "") + PARAM_REASON_STRING
                + string;
    }

    public static String getModifiedParamReasonString(String comparedValue, String value) {
        PARAMETER p = ContentManager.getPARAM(new Parameter(comparedValue).getValue_string());
        return getParamReasonString(value, p);

    }

    public static String getOrReasonString(String value, VALUE... p) {
        String string = "";
        for (VALUE param : p) {
            string += param.getName() + OR;
        }
        string = string.substring(0, string.length() - OR.length());
        string += PARAM_REASON_STRING + value;
        return string;
    }

    public static String getSkillRankReqString(String mastery, Entity type, MASTERY_RANK rank) {
        return mastery + SKILL_RANK_REQUIRED + rank.getName() + " "
                + StringMaster.wrapInParenthesis("" + rank.getMasteryReq());
    }

    public static String getTotalReasonString(String amount, PARAMETER... parameters) {
        String string = "";
        for (PARAMETER p : parameters) {
            string += p.getName() + ", ";
        }
        string = StringMaster.cropLast(string, 2);

        return "Total of " + string + " required: " + amount;
    }

    public static String getWorkspaceTip(Entity entity) {
        WORKSPACE_GROUP ws = new EnumMaster<WORKSPACE_GROUP>().retrieveEnumConst(
                WORKSPACE_GROUP.class, entity.getProperty(G_PROPS.WORKSPACE_GROUP));
        return getWorkspaceTip(ws);
    }

    public static String getWorkspaceTip(WORKSPACE_GROUP ws) {
        if (ws != null) {
            switch (ws) {
                case DESIGN:
                    return "(idea)";
                case FIX:
                    return "(to fix)";
                case FOCUS:
                    return "(pending)";
                case IMPLEMENT:
                    return "(unimpl.)";
                case POLISH:
                    return "(polish)";
                case TEST:
                    return "(to test)";
                default:
                    break;
            }
        }
        return "";
    }
}
