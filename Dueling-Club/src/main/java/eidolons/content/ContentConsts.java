package eidolons.content;

import eidolons.entity.Deity;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
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
     PARAMS.C_STAMINA,
     PARAMS.C_MORALE,
            PARAMS.C_ESSENCE,
     PARAMS.C_FOCUS,
    };
    public static final PARAMETER[] MAIN_PARAMETERS = {
     PARAMS.ATTACK, PARAMS.DEFENSE, PARAMS.INITIATIVE, PARAMS.ARMOR, PARAMS.FORTITUDE, PARAMS.RESISTANCE, PARAMS.SPIRIT,
              PARAMS.N_OF_COUNTERS,
    };
    public static final VALUE[] ATTRIBUTES = {PARAMS.STRENGTH, PARAMS.VITALITY, PARAMS.AGILITY,
     PARAMS.DEXTERITY, PARAMS.WILLPOWER, PARAMS.INTELLIGENCE, PARAMS.SPELLPOWER,
     PARAMS.KNOWLEDGE, PARAMS.WISDOM, PARAMS.CHARISMA,};
    public static final PARAMS[] ATTRIBUTES_WRAPPED = {
     PARAMS.STRENGTH, PARAMS.INTELLIGENCE, PARAMS.VITALITY,
     PARAMS.SPELLPOWER, PARAMS.AGILITY, PARAMS.KNOWLEDGE, PARAMS.DEXTERITY,
     PARAMS.WISDOM, PARAMS.WILLPOWER, PARAMS.CHARISMA,

    };
    public static final PARAMS[] COST_PARAMS = {PARAMS.ESS_COST, PARAMS.ENDURANCE_COST,
     PARAMS.FOC_COST, PARAMS.STA_COST, PARAMS.AP_COST,};
    public static final PARAMS[] PAY_PARAMS = {PARAMS.C_ESSENCE, PARAMS.C_ENDURANCE,
     PARAMS.C_FOCUS, PARAMS.C_STAMINA ,};
    final static PARAMETER[] ARMOR_MODIFYING_PARAMS = {PARAMS.NOISE, PARAMS.QUICK_SLOTS,
      PARAMS.ARMOR, PARAMS.DEFENSE, PARAMS.TOUGHNESS, PARAMS.ENDURANCE,
      PARAMS.SIDE_SIGHT_PENALTY, PARAMS.SIGHT_RANGE, PARAMS.STEALTH, PARAMS.DETECTION,
      PARAMS.STAMINA_PENALTY, PARAMS.FOCUS_PENALTY, PARAMS.AP_PENALTY,
      PARAMS.ESSENCE_PENALTY, PARAMS.SPELL_STA_PENALTY, PARAMS.SPELL_FOC_PENALTY,
      PARAMS.SPELL_AP_PENALTY, PARAMS.SPELL_ESS_PENALTY,
      // PARAMS.ATTACK_AP_PENALTY, PARAMS.ATTACK_STA_PENALTY, //TODO
      // OFFHAND?!
      PARAMS.MOVE_AP_PENALTY, PARAMS.MOVE_STA_PENALTY,

     };
    static final DC_TYPE[] BF_OBJ_TYPES = {DC_TYPE.CHARS, DC_TYPE.BF_OBJ,
      DC_TYPE.UNITS};
    private static final Integer[] defaultUnitParams = null;
    // first page => attributes?
    private static final Integer[] defaultCharParams = null;
    public final static Integer[][] defaultParams = {defaultUnitParams, defaultCharParams,};
    // another page => C_ and PERC
    static final Class<?>[] PARAM_ENUM_CLASSES = {G_PARAMS.class, PARAMS.class};
    static final Class<?>[] PROP_ENUM_CLASSES = {G_PROPS.class, PROPS.class};
    static final String DEFAULT_DEITY = "Faithless";
    static final String DEFAULT_WEAPON = "Petty Fist";
    public static PARAMETER[] REGENERATED_PARAMS = {
     PARAMS.ENDURANCE, PARAMS.FOCUS,
     PARAMS.ESSENCE, PARAMS.STAMINA
     //   ,PARAMS.ENERGY
    };
    public static PARAMETER[] headerUnitParams2 = {PARAMS.C_N_OF_COUNTERS ,
     PARAMS.C_ATB, PARAMS.C_ENDURANCE,
     PARAMS.STEALTH, PARAMS.CONCEALMENT, PARAMS.DETECTION, PARAMS.BEHIND_SIGHT_BONUS,
     PARAMS.SIDE_SIGHT_PENALTY,
     PARAMS.ENDURANCE_REGEN, PARAMS.C_CARRYING_WEIGHT,
    };
    // next page => Masteries
    // another page => Modifiers (+sneak)
    private static PARAMETER[] headerCharParams = null;
    public static PARAMETER[] headerUnitParams = {PARAMS.BASE_DAMAGE, PARAMS.ARMOR,
     PARAMS.DEFENSE, PARAMS.ATTACK, PARAMS.C_MORALE, PARAMS.RESISTANCE, PARAMS.SPELL_ARMOR,
     PARAMS.SIGHT_RANGE,
    };
    public final static PARAMETER[][] headerParams = {headerUnitParams, headerCharParams,};
    static PARAMETER[] FEAT_MODIFYING_PARAMS = {PARAMS.XP_COST_REDUCTION,
     PARAMS.XP_COST_REDUCTION_VERBATIM_SPELLS, PARAMS.XP_COST_REDUCTION_LEARNED_SPELLS,
     PARAMS.XP_COST_REDUCTION_MASTERIES,

     PARAMS.CRITICAL_REDUCTION, PARAMS.CRITICAL_MOD, PARAMS.COUNTER_MOD,
     PARAMS.COOLDOWN_MOD, PARAMS.BLOCK_CHANCE, PARAMS.PARRY_CHANCE,

    }; // also
    private static VALUE[] excludedValuesFromAll = {G_PARAMS.POS_X, G_PARAMS.POS_Y,
     G_PROPS.BF_OBJECT_TYPE, G_PROPS.IMAGE, G_PROPS.TYPE, G_PROPS.LORE, G_PROPS.DEITY,
     G_PROPS.DESCRIPTION, G_PROPS.GROUP, G_PROPS.SOUNDSET, PARAMS.QUANTITY};
    static VALUE[][] excludedValues = {
      {PROPS.DAMAGE_TYPE,},
      {},
      {PROPS.DAMAGE_TYPE,}, // CHARS
      {}, {}};
    static String[] unknownValues = {G_PROPS.TYPE.name(), PROPS.VISIBILITY_STATUS.name(),
       PROPS.DETECTION_STATUS.name(), PROPS.FACING_DIRECTION.name(),
       PARAMS.C_ATB.name()  ,
      };
    static List<VALUE> NO_SHOW_NAME_VALUES = new ArrayList<>();
    static Map<String, Deity> deities;
    static List<PARAMS> masteries;
    static List<PARAMETER> ARMOR_MODIFYING_PARAMS_FULL;
    private static HashMap<OBJ_TYPE, List<VALUE>> defaultValues = new HashMap<>();
    static Map<String, EDITOR> editorMap;
    static List<VALUE> backgroundValues;
    static List<PARAMETER> backgroundDynamicParams;
    static Map<HeroEnums.PRINCIPLES, PARAMETER> alignmentMap = new HashMap<>();
    static Map<HeroEnums.PRINCIPLES, PARAMETER> identityMap = new HashMap<>();
    static String focusMasteries;
    static String focusClassGroups;
    static ArrayList<PARAMETER> dynamicParams = new ArrayList<>();
    static VALUE[] displayedHeroProperties= new VALUE[]{
     G_PROPS.ASPECT, G_PROPS.DEITY, G_PROPS.BACKGROUND,
     G_PROPS.RACE, G_PROPS.MODE, G_PROPS.STATUS, G_PROPS.STANDARD_PASSIVES,
     PARAMS.INTEGRITY,

    };
    static PARAMETER[] WEAPON_MODIFYING_PARAMS = {

     PARAMS.ARMOR, PARAMS.DEFENSE, PARAMS.TOUGHNESS, PARAMS.ENDURANCE, PARAMS.STAMINA_PENALTY,
     PARAMS.FOCUS_PENALTY, PARAMS.AP_PENALTY, PARAMS.ESSENCE_PENALTY,
     PARAMS.SPELL_STA_PENALTY, PARAMS.SPELL_FOC_PENALTY, PARAMS.SPELL_AP_PENALTY,
     PARAMS.SPELL_ESS_PENALTY,
     // PARAMS.ATTACK_AP_PENALTY, PARAMS.ATTACK_STA_PENALTY,
     PARAMS.MOVE_AP_PENALTY, PARAMS.MOVE_STA_PENALTY,};
    private static PROPERTY[] headerUnitProps = {G_PROPS.ASPECT, G_PROPS.DEITY, G_PROPS.STATUS,
     G_PROPS.MODE};
    private static PROPERTY[] headerCharProps = {G_PROPS.RACE, G_PROPS.RANK,};
    public final static PROPERTY[][] headerProps = {headerUnitProps, headerCharProps,};
}
