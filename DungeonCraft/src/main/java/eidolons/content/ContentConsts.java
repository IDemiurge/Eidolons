package eidolons.content;

import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.enums.entity.HeroEnums;
import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.swing.generic.components.editors.EDITOR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentConsts {
    public static final PARAMETER[] DYNAMIC_PARAMETERS = {
            PARAMS.C_TOUGHNESS,
            PARAMS.C_ENDURANCE,
            PARAMS.C_ESSENCE,
            PARAMS.C_FOCUS,
    };
    public static final PARAMETER[] MAIN_PARAMETERS = {
            PARAMS.ATTACK, PARAMS.DEFENSE, PARAMS.INITIATIVE, PARAMS.ARMOR, PARAMS.MIGHT,
            PARAMS.RESISTANCE, PARAMS.SPIRIT,
    };
    public static final PARAMETER[] ROLL_PARAMETERS = {
            PARAMS.MIGHT, PARAMS.REFLEX, PARAMS.GRIT, PARAMS.WIT, PARAMS.SPIRIT,
    };
    public static final PARAMS[] ATTRIBUTES_WRAPPED = {
            PARAMS.STRENGTH, PARAMS.INTELLIGENCE, PARAMS.VITALITY,
            PARAMS.SPELLPOWER, PARAMS.AGILITY, PARAMS.KNOWLEDGE, PARAMS.DEXTERITY,
            PARAMS.WISDOM, PARAMS.WILLPOWER, PARAMS.CHARISMA,
    };
    public static final PARAMS[] COST_PARAMS = {PARAMS.ESS_COST, PARAMS.ENDURANCE_COST,
            PARAMS.FOC_COST, PARAMS.TOU_COST, PARAMS.AP_COST,};
    public static final PARAMS[] PAY_PARAMS = {PARAMS.C_ESSENCE, PARAMS.C_ENDURANCE,
            PARAMS.C_FOCUS,};
    final static PARAMETER[] ARMOR_MODIFYING_PARAMS = {
             PARAMS.QUICK_SLOTS,
            PARAMS.ARMOR, PARAMS.DEFENSE, PARAMS.TOUGHNESS, PARAMS.ENDURANCE,
            PARAMS.SIGHT_RANGE, PARAMS.STEALTH, PARAMS.DETECTION,
            PARAMS.TOUGHNESS_COST_MOD, PARAMS.FOCUS_COST_MOD, PARAMS.ATB_COST_MOD,
            PARAMS.ESSENCE_COST_MOD, PARAMS.SPELL_FOC_COST_MOD,
            PARAMS.SPELL_ATB_COST_MOD, PARAMS.SPELL_ESS_COST_MOD,
            // PARAMS.ATTACK_AP_PENALTY, PARAMS.ATTACK_STA_PENALTY, //TODO
            // OFFHAND?!
            PARAMS.MOVE_ATB_COST_MOD, PARAMS.MOVE_TOU_COST_MOD,

    };
    static final DC_TYPE[] BF_OBJ_TYPES = {DC_TYPE.CHARS, DC_TYPE.BF_OBJ,            DC_TYPE.UNITS};
    private static final Integer[] defaultUnitParams = null;
    // first page => attributes?
    private static final Integer[] defaultCharParams = null;
    public final static Integer[][] defaultParams = {defaultUnitParams, defaultCharParams,};
    // another page => C_ and PERC
    static final Class<?>[] PARAM_ENUM_CLASSES = {G_PARAMS.class, PARAMS.class};
    static final Class<?>[] PROP_ENUM_CLASSES = {G_PROPS.class, PROPS.class};
    static final String DEFAULT_WEAPON = "Petty Fist";
    public static PARAMETER[] REGENERATED_PARAMS = {
            PARAMS.ENDURANCE, PARAMS.FOCUS,            PARAMS.ESSENCE
    };
    // next page => Masteries
    // another page => Modifiers (+sneak)
    private static final PARAMETER[] headerCharParams = null;
    public static PARAMETER[] headerUnitParams = {PARAMS.BASE_DAMAGE, PARAMS.ARMOR,
            PARAMS.DEFENSE, PARAMS.ATTACK, PARAMS.RESISTANCE,
            PARAMS.SIGHT_RANGE,
    };
    static PARAMETER[] FEAT_MODIFYING_PARAMS = {
            PARAMS.CRITICAL_REDUCTION, PARAMS.CRITICAL_MOD, PARAMS.COUNTER_MOD,
            PARAMS.BLOCK_CHANCE,
    };
    static VALUE[][] excludedValues = {
            {PROPS.DAMAGE_TYPE,},
            {},
            {PROPS.DAMAGE_TYPE,}, // CHARS
            {}, {}};
    static String[] unknownValues = {G_PROPS.TYPE.name(), PROPS.VISIBILITY_STATUS.name(),
            PROPS.DETECTION_STATUS.name(), PROPS.FACING_DIRECTION.name(),
            PARAMS.C_ATB.name(),
    };
    static List<VALUE> NO_SHOW_NAME_VALUES = new ArrayList<>();
    static List<PARAMS> masteries;
    static List<PARAMETER> ARMOR_MODIFYING_PARAMS_FULL;
    static Map<String, EDITOR> editorMap;
    static List<VALUE> backgroundValues;
    static List<PARAMETER> backgroundDynamicParams;
    static Map<HeroEnums.PRINCIPLES, PARAMETER> alignmentMap = new HashMap<>();
    static Map<HeroEnums.PRINCIPLES, PARAMETER> identityMap = new HashMap<>();
    static ArrayList<PARAMETER> dynamicParams = new ArrayList<>();
    static VALUE[] displayedHeroProperties = new VALUE[]{
            G_PROPS.ASPECT, G_PROPS.DEITY, G_PROPS.BACKGROUND,
            G_PROPS.RACE, G_PROPS.MODE, G_PROPS.STATUS, G_PROPS.STANDARD_PASSIVES,
            PARAMS.INTEGRITY,

    };
    static PARAMETER[] WEAPON_MODIFYING_PARAMS = {
            PARAMS.ARMOR, PARAMS.DEFENSE, PARAMS.TOUGHNESS, PARAMS.ENDURANCE, PARAMS.TOUGHNESS_COST_MOD,
            PARAMS.FOCUS_COST_MOD, PARAMS.ATB_COST_MOD, PARAMS.ESSENCE_COST_MOD,
            PARAMS.SPELL_FOC_COST_MOD, PARAMS.SPELL_ATB_COST_MOD,
            PARAMS.SPELL_ESS_COST_MOD,
            // PARAMS.ATTACK_AP_PENALTY, PARAMS.ATTACK_STA_PENALTY,
            PARAMS.MOVE_ATB_COST_MOD, PARAMS.MOVE_TOU_COST_MOD,};
    private static final PROPERTY[] headerUnitProps = {G_PROPS.ASPECT, G_PROPS.DEITY, G_PROPS.STATUS,
            G_PROPS.MODE};
    private static final PROPERTY[] headerCharProps = {G_PROPS.RACE, G_PROPS.RANK,};
}
