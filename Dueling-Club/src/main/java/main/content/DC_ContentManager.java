package main.content;

import main.client.cc.gui.neo.tree.view.ClassTreeView;
import main.client.cc.gui.neo.tree.view.SkillTreeView;
import main.client.dc.Launcher;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.ASPECT;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;
import main.content.enums.entity.HeroEnums.CLASS_TYPE;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.WEAPON_SIZE;
import main.content.enums.entity.SkillEnums;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.values.parameters.*;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Deity;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.Obj;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.rules.mechanics.CoatingRule;
import main.game.core.game.DC_Game;
import main.swing.generic.components.editors.EDITOR;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.math.DC_MathManager;

import java.util.*;

public class DC_ContentManager extends ContentManager {
    public static final PARAMETER[] DYNAMIC_PARAMETERS = {
     PARAMS.C_TOUGHNESS,
     PARAMS.C_ENDURANCE,
     PARAMS.C_STAMINA,
     PARAMS.C_MORALE,
     PARAMS.C_FOCUS,
     PARAMS.C_ESSENCE,
    };
    public static final PARAMETER[] MAIN_PARAMETERS = {
     PARAMS.C_ESSENCE,
    };
    public static final VALUE[] ATTRIBUTES = {PARAMS.STRENGTH, PARAMS.VITALITY, PARAMS.AGILITY,
     PARAMS.DEXTERITY, PARAMS.WILLPOWER, PARAMS.INTELLIGENCE, PARAMS.SPELLPOWER,
     PARAMS.KNOWLEDGE, PARAMS.WISDOM, PARAMS.CHARISMA,};
    public static final PARAMS[] COST_PARAMS = {PARAMS.ESS_COST, PARAMS.ENDURANCE_COST,
     PARAMS.FOC_COST, PARAMS.STA_COST, PARAMS.AP_COST,};
    public static final PARAMS[] PAY_PARAMS = {PARAMS.C_ESSENCE, PARAMS.C_ENDURANCE,
     PARAMS.C_FOCUS, PARAMS.C_STAMINA, PARAMS.C_N_OF_ACTIONS,};
    private final static PARAMETER[] ARMOR_MODIFYING_PARAMS = {PARAMS.NOISE, PARAMS.QUICK_SLOTS,
     PARAMS.ARMOR, PARAMS.DEFENSE, PARAMS.TOUGHNESS, PARAMS.ENDURANCE,
     PARAMS.SIDE_SIGHT_PENALTY, PARAMS.SIGHT_RANGE, PARAMS.STEALTH, PARAMS.DETECTION,
     PARAMS.STAMINA_PENALTY, PARAMS.FOCUS_PENALTY, PARAMS.AP_PENALTY,
     PARAMS.ESSENCE_PENALTY, PARAMS.SPELL_STA_PENALTY, PARAMS.SPELL_FOC_PENALTY,
     PARAMS.SPELL_AP_PENALTY, PARAMS.SPELL_ESS_PENALTY,
     // PARAMS.ATTACK_AP_PENALTY, PARAMS.ATTACK_STA_PENALTY, //TODO
     // OFFHAND?!
     PARAMS.MOVE_AP_PENALTY, PARAMS.MOVE_STA_PENALTY,

    };
    private static final DC_TYPE[] BF_OBJ_TYPES = {DC_TYPE.CHARS, DC_TYPE.BF_OBJ,
     DC_TYPE.UNITS};
    private static final Integer[] defaultUnitParams = null;
    // dynamic will be sorted out if need be

    // first page => attributes?
    private static final Integer[] defaultCharParams = null;
    public final static Integer[][] defaultParams = {defaultUnitParams, defaultCharParams,};
    // another page => C_ and PERC
    private static final Class<?>[] PARAM_ENUM_CLASSES = {G_PARAMS.class, PARAMS.class};
    private static final Class<?>[] PROP_ENUM_CLASSES = {G_PROPS.class, PROPS.class};
    private static final String DEFAULT_DEITY = "Faithless";
    private static final String DEFAULT_WEAPON = "Petty Fist";
    public static PARAMETER[] REGEN_PARAMS = {
     PARAMS.ENDURANCE, PARAMS.FOCUS,
     PARAMS.ESSENCE, PARAMS.STAMINA
//   ,PARAMS.ENERGY
    };
    private static PARAMETER[] WEAPON_MODIFYING_PARAMS = {

     PARAMS.ARMOR, PARAMS.DEFENSE, PARAMS.TOUGHNESS, PARAMS.ENDURANCE, PARAMS.STAMINA_PENALTY,
     PARAMS.FOCUS_PENALTY, PARAMS.AP_PENALTY, PARAMS.ESSENCE_PENALTY,
     PARAMS.SPELL_STA_PENALTY, PARAMS.SPELL_FOC_PENALTY, PARAMS.SPELL_AP_PENALTY,
     PARAMS.SPELL_ESS_PENALTY,
     // PARAMS.ATTACK_AP_PENALTY, PARAMS.ATTACK_STA_PENALTY,
     PARAMS.MOVE_AP_PENALTY, PARAMS.MOVE_STA_PENALTY,};
    private static PROPERTY[] headerUnitProps = {G_PROPS.ASPECT, G_PROPS.DEITY, G_PROPS.STATUS,
     G_PROPS.MODE};
    private static PROPERTY[] headerCharProps = {G_PROPS.RACE, G_PROPS.RANK,};
    // takes
    // weapon
    // and
    // armor
    // params
    // as
    // well
    // as
    // masteries
    public final static PROPERTY[][] headerProps = {headerUnitProps, headerCharProps,};
    private static PARAMETER[] headerUnitParams = {PARAMS.BASE_DAMAGE, PARAMS.ARMOR,
     PARAMS.DEFENSE, PARAMS.ATTACK, PARAMS.C_MORALE, PARAMS.RESISTANCE, PARAMS.SPELL_ARMOR,
     PARAMS.SIGHT_RANGE,

    };
    private static PARAMETER[] headerUnitParams2 = {PARAMS.C_N_OF_COUNTERS, PARAMS.C_N_OF_ACTIONS,
     PARAMS.C_INITIATIVE, PARAMS.C_ENDURANCE,

     PARAMS.STEALTH, PARAMS.CONCEALMENT, PARAMS.DETECTION, PARAMS.BEHIND_SIGHT_BONUS,
     PARAMS.SIDE_SIGHT_PENALTY,

     PARAMS.ENDURANCE_REGEN, PARAMS.C_CARRYING_WEIGHT, PARAMS.INITIATIVE_MODIFIER,
     PARAMS.INITIATIVE_BONUS,

    };

    // next page => Masteries
    // another page => Modifiers (+sneak)
    private static PARAMETER[] headerCharParams = null;
    public final static PARAMETER[][] headerParams = {headerUnitParams, headerCharParams,};
    private static PARAMETER[] FEAT_MODIFYING_PARAMS = {PARAMS.XP_COST_REDUCTION,
     PARAMS.XP_COST_REDUCTION_VERBATIM_SPELLS, PARAMS.XP_COST_REDUCTION_LEARNED_SPELLS,
     PARAMS.XP_COST_REDUCTION_MASTERIES,

     PARAMS.CRITICAL_REDUCTION, PARAMS.CRITICAL_MOD, PARAMS.COUNTER_MOD,
     PARAMS.COOLDOWN_MOD, PARAMS.BLOCK_CHANCE, PARAMS.PARRY_CHANCE,

    }; // also
    private static VALUE[] priorityValues = {

     PARAMS.C_N_OF_ACTIONS, PARAMS.C_ENDURANCE, PARAMS.C_TOUGHNESS, PARAMS.LEVEL,};
    private static VALUE[] excludedValuesFromAll = {G_PARAMS.POS_X, G_PARAMS.POS_Y,
     G_PROPS.BF_OBJECT_TYPE, G_PROPS.IMAGE, G_PROPS.TYPE, G_PROPS.LORE, G_PROPS.DEITY,
     G_PROPS.DESCRIPTION, G_PROPS.GROUP, G_PROPS.SOUNDSET, PARAMS.QUANTITY};
    private static VALUE[][] excludedValues = {
     //
     {PROPS.DAMAGE_TYPE,},
     //
     {},
     //
     {PROPS.DAMAGE_TYPE,}, // CHARS
     {}, {}};
    private static String[] unknownValues = {G_PROPS.TYPE.name(), PROPS.VISIBILITY_STATUS.name(),
     PROPS.DETECTION_STATUS.name(), PROPS.FACING_DIRECTION.name(),
     // size
     // type

     PARAMS.C_INITIATIVE.name(), PARAMS.C_N_OF_ACTIONS.name(),

    };
    private static List<VALUE> NO_SHOW_NAME_VALUES = new LinkedList<>();

    private static Map<String, Deity> deities;
    private static List<PARAMS> masteries;
    private static List<PARAMETER> ARMOR_MODIFYING_PARAMS_FULL;
    private static HashMap<OBJ_TYPE, List<VALUE>> defaultValues = new HashMap<>();
    private static Map<String, EDITOR> editorMap;
    private static List<VALUE> backgroundValues;
    private static List<PARAMETER> backgroundDynamicParams;
    private static Map<PRINCIPLES, PARAMETER> alignmentMap = new HashMap<>();
    private static Map<PRINCIPLES, PARAMETER> identityMap = new HashMap<>();
    private static String focusMasteries;
    private static String focusClassGroups;
    private static LinkedList<PARAMETER> dynamicParams = new LinkedList<>();

    static {
        Arrays.stream(PARAMS.values()).forEach(param -> {
            if (param.isDynamic()) {
                dynamicParams.add(param);
            }
        });
        // DEFAULT_VALUES
        // ArrayMaster.join();
        // BG_PARAMS

        LinkedList<PARAMETER> list = new LinkedList<>();

        ARMOR_MODIFYING_PARAMS_FULL = new LinkedList<>();

        ARMOR_MODIFYING_PARAMS_FULL.addAll(Arrays.asList(ARMOR_MODIFYING_PARAMS));
        ARMOR_MODIFYING_PARAMS_FULL.addAll(Arrays.asList(ValuePages.RESISTANCES));

        // list.addAll((ARMOR_MODIFYING_PARAMS_FULL));
        // list.addAll(Arrays.asList(WEAPON_MODIFYING_PARAMS));
        list.addAll(Arrays.asList(ValuePages.UNIT_PARAMETERS));
        list.addAll(Arrays.asList(ValuePages.MASTERIES));
        list.addAll(Arrays.asList(ValuePages.PRINCIPLE_IDENTITIES));
        list.addAll(Arrays.asList(ValuePages.PRINCIPLE_ALIGNMENTS));
        list.addAll(Arrays.asList(FEAT_MODIFYING_PARAMS));

        FEAT_MODIFYING_PARAMS = list.toArray(new PARAMETER[list.size()]);
    }

    public DC_ContentManager() {
        super();
    }

    public static void initTypeDynamicValues() {
        for (ObjType t : DataManager.getTypes(DC_TYPE.CHARS)) {
            if (!t.getGroup().equals(StringMaster.BACKGROUND)) {
                continue;
            }

        }

    }

    private static Collection<PARAMETER> generateDerivedParams() {
        Collection<PARAMETER> list = new LinkedList<>();
        for (PARAMS p : PARAMS.values()) {
            if (p.isMastery()) {
                Param scoreParam = new Param(p);
                scoreParam.setName(p.getName() + StringMaster.SCORE);
                list.add(scoreParam);
            }
        }

        for (COUNTER c : CoatingRule.COATING_COUNTERS) {
            list.add(generateCoatingParam(c, PARAMS.COATING_COUNTERS_APPLIED_PER_HIT_MOD));
            list.add(generateCoatingParam(c, PARAMS.COATING_COUNTERS_APPLIED_TO_ITEM_MOD));
            list.add(generateCoatingParam(c, PARAMS.COATING_COUNTERS_SPENT_MOD));

        }
        return list;

    }

    private static PARAMETER generateCoatingParam(COUNTER c, PARAMS p) {
        Param coatingParam = new Param(p);
        coatingParam.setName(p.getName().replace("Coating Counters", c.getName()));
        return coatingParam;
    }

    public static PARAMETER getCoatingMaxPerHitModParam(COUNTER c) {
        return ContentManager.getPARAM(PARAMS.COATING_COUNTERS_APPLIED_PER_HIT_MOD.getName()
         .replace("Coating Counters", c.getName()));
    }

    public static PARAMETER getCoatingAppliedModParam(COUNTER c) {
        return ContentManager.getPARAM(PARAMS.COATING_COUNTERS_APPLIED_TO_ITEM_MOD.getName()
         .replace("Coating Counters", c.getName()));
    }

    public static Collection<PROPERTY> generateDerivedProperties() {
        Collection<PROPERTY> list = new LinkedList<>();

        for (PROPS p : PROPS.values()) {

        }

        return list;

    }

    public static Deity getDeity(Obj obj) {
        String property = obj.getProperty(G_PROPS.DEITY);
        return getDeity(obj.getRef(), property);
    }

    public static Deity getDefaultDeity() {
        return getDeity(null, DEFAULT_DEITY);
    }

    public static Deity getDeity(Ref ref, String property) {
        if (StringMaster.isEmpty(property)) {
            return getDefaultDeity();
        }
        if (deities == null) {
            deities = new HashMap<>();
        }
        Deity deity = deities.get(property);
        if (deity != null) {
            return deity;
        }
        ObjType type = DataManager.getType(property, DC_TYPE.DEITIES);
        if (type == null) {
            return getDefaultDeity();
        }
        deity = new Deity(type, ref.getGame(), ref);
        deity.toBase();
        deities.put(type.getName(), deity);
        return deity;
    }

    public static List<ATTRIBUTE> getAttributeEnums() {
        return Arrays.asList(ATTRIBUTE.values());
    }

    // public static boolean isResetExcludedParam(PARAMETER portrait) {
    // return Arrays.asList(resetExcludedParam).contains(portrait);
    // }

    public static PARAMETER getAlignmentForPrinciple(PRINCIPLES principle) {
        PARAMETER param = alignmentMap.get(principle);
        if (param != null) {
            return param;
        }
        param = ContentManager.getPARAM(principle.toString() + StringMaster.ALIGNMENT);
        alignmentMap.put(principle, param);
        return param;
    }

    public static PARAMETER getIdentityParamForPrinciple(PRINCIPLES principle) {
        // OPTIMIZATION: having a param field on each Principle const!?
        // faster...
        PARAMETER param = identityMap.get(principle);
        if (param != null) {
            return param;
        }
        param = ContentManager.getPARAM(principle.toString() + StringMaster.IDENTITY);
        identityMap.put(principle, param);
        return param;
    }

    public static List<PARAMS> getMasteryParams() {
        if (masteries != null) {
            return masteries;
        }
        masteries = new LinkedList<>();
        for (PARAMETER m : ContentManager.getMasteries()) {
            masteries.add((PARAMS) m);
        }
        return masteries;
    }

    public static Collection<String> getLimitedInfoPanelValueList(String objType) {
        return Arrays.asList(unknownValues);
    }

    public static Collection<String> getInfoPanelValueList(String objType) {
        if (objType == null) {
            return Collections.EMPTY_LIST;
        }
        List<String> valueNames = ContentManager.getValueNamesMap().get(objType);
         if (valueNames != null)
            return valueNames;
        valueNames = ContentManager.getFullValueList(objType);

        // for (VALUE v : excludedValuesFromAll) {
        // valueNames.remove(v.getName());
        // }
        try {
            if (DC_TYPE.getCode(objType) < excludedValues.length) {
                for (VALUE v : excludedValues[DC_TYPE.getCode(objType)]) {
                    valueNames.remove(v.getName());
                }
            }
        } catch (Exception e) {

        }
        ContentManager.getValueNamesMap().put(objType, valueNames);
        return valueNames;

    }

    public static PARAMETER[] getArmorModifyingParams() {
        return ARMOR_MODIFYING_PARAMS_FULL
         .toArray(new PARAMETER[ARMOR_MODIFYING_PARAMS_FULL.size()]);
    }

    public static PARAMETER[] getWeaponModifyingParams() {
        return WEAPON_MODIFYING_PARAMS;
    }

    public static PARAMETER[] getFeatModifyingParams() {
        return FEAT_MODIFYING_PARAMS;
    }

    public static DC_TYPE[] getBF_TYPES() {
        return BF_OBJ_TYPES;
    }

    public static PARAMETER getDamageTypeResistance(DAMAGE_TYPE type) {
        return ContentManager.getPARAM(type.getResistanceName());

    }

    public static PARAMETER getBaseAttr(ATTRIBUTE attr) {
        return getBaseAttr(attr.getParameter());
    }

    public static PARAMETER getDefaultAttr(PARAMETER param) {
        return ContentManager.getPARAM(StringMaster.DEFAULT + param.toString());
    }

    public static PARAMETER getBaseAttr(PARAMETER param) {
        return ContentManager.getPARAM(StringMaster.BASE + param.toString());
    }

    public static Object getHeaderValues(OBJ_TYPE obj_TYPE_ENUM) {
        // TODO Auto-generated method stub
        return null;
    }

    // public static PARAMETER getFinalAttrFromBase(PARAMETER param) {
    // return ContentManager.getPARAM(StringMaster.BASE + param.toString());
    // }

    public static PARAMS[] getCostParams() {
        return COST_PARAMS;
    }

    public static PARAMETER getSpecialCostReductionParam(PARAMETER costParam, PROPERTY p) {
        String valueName = null;
        if (p == PROPS.VERBATIM_SPELLS) {
            valueName = costParam.getName() + StringMaster.REDUCTION + "_" + p.getName();
        }
        if (p == PROPS.SKILLS) {
            valueName = costParam.getName() + StringMaster.REDUCTION + "_" + p.getName();
        }
        if (p == PROPS.LEARNED_SPELLS) {
            valueName = costParam.getName() + StringMaster.REDUCTION + "_" + p.getName();
        }
        return ContentManager.getPARAM(valueName);
    }

    public static PARAMETER getCostReductionParam(PARAMETER costParam, PROPERTY p) {
        String valueName = costParam.getName() + StringMaster.REDUCTION;
        return ContentManager.getPARAM(valueName);
    }

    public static boolean isShowValueName(VALUE value) {
        return !NO_SHOW_NAME_VALUES.contains(value);
    }

    public static List<PARAMETER> getBackgroundDynamicParams() {

        if (backgroundDynamicParams != null) {
            return backgroundDynamicParams;
        }
        backgroundDynamicParams = new LinkedList<>();
        for (PARAMETER v : ValuePages.PRINCIPLE_IDENTITIES) {
            backgroundDynamicParams.add(v);
        }
        // TODO anything else?!
        backgroundDynamicParams.add(PARAMS.IDENTITY_POINTS_PER_LEVEL);
        return backgroundDynamicParams;
    }

    public static List<VALUE> getBackgroundStaticValues() {
        if (backgroundValues != null) {
            return backgroundValues;
        }
        backgroundValues = new LinkedList<>();
        for (VALUE[] list : ValuePages.BACKGROUND_VALUES) {
            for (VALUE v : list) {
                backgroundValues.add(v);
            }
        }
        backgroundValues.remove(PARAMS.IDENTITY_POINTS_PER_LEVEL);

        return backgroundValues;
    }

    public static PARAMETER[] getWeaponWeightPenaltyParams() {
        return new PARAMETER[]{
         // PARAMS.ATTACK_MOD,
         PARAMS.ATTACK_STA_PENALTY, PARAMS.ATTACK_AP_PENALTY, PARAMS.SPELL_FOC_PENALTY,
         PARAMS.SPELL_AP_PENALTY,};
    }

    public static PARAMETER[] getArmorWeightPenaltyParams() {
        return new PARAMETER[]{
         // PARAMS.DEFENSE_MOD,
         PARAMS.MOVE_STA_PENALTY, PARAMS.MOVE_AP_PENALTY, PARAMS.SPELL_STA_PENALTY,
         PARAMS.SPELL_ESS_PENALTY,};
    }

    public static void addDefaultValues(Entity entity, boolean dynamic) {
        addDefaultValues(entity, dynamic,

         ContentManager.getValueList()
        );
    }
        public static void addDefaultValues(Entity entity, boolean dynamic,
                                            Collection<VALUE> vals) {
        for (VALUE VAL : vals) {
            if (!ContentManager.isValueForOBJ_TYPE(entity.getOBJ_TYPE_ENUM(), VAL)) {
                continue;
            }
            String value = getDefaultValueSpecial(entity, VAL);
            if (value == null) {
                value = VAL.getDefaultValue();
            }
            if (!dynamic) {
                if (VAL.isDynamic()) {
                    continue;
                }
                if (StringMaster.isEmpty(value)) {
                    continue;
                }
                if (value.equals("0")) {
                    continue;
                }
            }
            boolean unit = C_OBJ_TYPE.UNITS_CHARS.equals(entity.getOBJ_TYPE_ENUM());

            if (getDefaultValuesToReset().contains(VAL)
             || (!unit && StringMaster.isEmpty(entity.getValue(VAL)) && !StringMaster
             .isEmpty(value))) {
                if (entity instanceof Obj) {
                    entity.getType().setValue(VAL, value);
                }
                entity.setValue(VAL, value);
                if (unit) {
                    LogMaster.log(1, entity + ":: Added Default Value " + ""
                     + VAL + "=" + value);
                }
            }

        }
        entity.setDefaultValuesInitialized(true);
    }

    private static List<VALUE> getDefaultValuesToReset() {
        return new LinkedList<>(Arrays
         .asList(
          PARAMS.COUNTER_STAMINA_PENALTY,
          PARAMS.AOO_STAMINA_PENALTY,
          PARAMS.INSTANT_STAMINA_PENALTY,
          PARAMS.COUNTER_MOD));
    }

    private static String getDefaultValueSpecial(Entity entity, VALUE v) {
        if (v.getSpecialDefault(entity.getOBJ_TYPE_ENUM()) != null) {
            return v.getSpecialDefault(entity.getOBJ_TYPE_ENUM()).toString();
        }
        return null;
    }

    public static void addDefaultValues(Entity entity) {
//this should be done in AV!!!
        for (String value : DC_ContentManager.getInfoPanelValueList(entity.getOBJ_TYPE())) {
            VALUE VAL = ContentManager.getValue(value);
            if (VAL == null) {
                continue;
            }
            if (StringMaster.isEmpty(entity.getValue(VAL))
             && !StringMaster.isEmptyOrZero(VAL.getDefaultValue())) {
                if (entity instanceof Obj) {
                    entity.getType().setValue(VAL, VAL.getDefaultValue());
                }
                entity.setValue(VAL, VAL.getDefaultValue());
            }
        }
        entity.setDefaultValuesInitialized(true);
    }

    public static PROPERTY[] getWeaponModifyingProps() {
        // TODO Auto-generated method stub
        return null;
    }

    public static DAMAGE_TYPE getDamageForAspect(ASPECT aspect) {
        if (aspect == null) {
            return null;
        }
        switch (aspect) {
            case ARCANUM:
                return GenericEnums.DAMAGE_TYPE.ARCANE;
            case CHAOS:
                return GenericEnums.DAMAGE_TYPE.CHAOS;
            case DARKNESS:
                return GenericEnums.DAMAGE_TYPE.SHADOW;
            case DEATH:
                return GenericEnums.DAMAGE_TYPE.DEATH;
            case LIFE:
                break;
            case LIGHT:
                return GenericEnums.DAMAGE_TYPE.HOLY;
            case NEUTRAL:
                break;
            default:
                break;

        }
        return null;
    }

    // public static void addDefaultValues(Entity entity) {
    // Chronos.mark("setting default values for " + entity.getName());
    // for (VALUE VAL : getDefaultValues(entity.getOBJ_TYPE_ENUM())) {
    // if (StringMaster.isEmpty(entity.getValue(VAL))) {
    // if (entity instanceof Obj)
    // entity.getType().setValue(VAL, VAL.getDefaultValue());
    // entity.setValue(VAL, VAL.getDefaultValue());
    // }
    // }
    // entity.setDefaultValuesInitialized(true);
    // Chronos.logTimeElapsedForMark("setting default values for "
    // + entity.getName());
    //
    // }

    // private static List<VALUE> getDefaultValues(OBJ_TYPE TYPE) {
    // List<VALUE> list = defaultValues.getOrCreate(TYPE);
    // if (list == null && TYPE != null) {
    // list = new LinkedList<>();
    // for (VALUE portrait : ContentManager.getValuesForType(TYPE.getName(), false)) {
    // if (StringMaster.isEmpty(portrait.getDefaultValue()))
    // if (!portrait.getDefaultValue().equals("0"))
    // list.add(portrait);
    // }
    //
    // defaultValues.put(TYPE, list);
    // }
    // return list;
    // }

    public static PARAMETER[] getParams(String sparam) {
        PARAMETER[] parameters = getParameters(sparam);
        if (parameters == null) {
            return new PARAMETER[0];
        }
        return parameters;

    }

    private static PARAMETER[] getParameters(String sparam) {
        if (ContentManager.isParameter(sparam)) {
            return new PARAMETER[]{ContentManager.getPARAM(sparam)};
        }
        PARAMETER param = ContentManager.getMastery(sparam);

        if (param != null) {
            return new PARAMETER[]{param};
        }
        if (StringMaster.openContainer(sparam, StringMaster.AND_SEPARATOR).size() > 1) {
            return DC_Game.game.getValueManager().getParamsFromContainer(sparam);
        } else {
            return DC_Game.game.getValueManager().getValueGroupParams(sparam);
        }
    }

    public static PARAMETER getPayParamFromUpkeep(PARAMETER param) {
        if (param == PARAMS.ESS_UPKEEP) {
            return PARAMS.C_ESSENCE;
        }
        if (param == PARAMS.AP_UPKEEP) {
            return PARAMS.C_N_OF_ACTIONS;
        }
        if (param == PARAMS.END_UPKEEP) {
            return PARAMS.C_ENDURANCE;
        }
        if (param == PARAMS.FOC_UPKEEP) {
            return PARAMS.C_FOCUS;
        }
        if (param == PARAMS.STA_UPKEEP) {
            return PARAMS.C_STAMINA;
        }

        return null;
    }

    public static boolean isParamFloatDisplayed(PARAMETER param) {
        if (param.isAttribute()) {
            if (!param.getName().contains("Base")) {
                return true;
            }
        }
        return false;
    }

    public static int compareSize(WEAPON_SIZE weaponSize, WEAPON_SIZE weaponSize2) {
        // TODO Auto-generated method stub

        int i = 0;
        int j = 0;
        for (WEAPON_SIZE w : ItemEnums.WEAPON_SIZE.values()) {
            if (weaponSize == w) {
                break;
            }
            i++;
        }
        for (WEAPON_SIZE w : ItemEnums.WEAPON_SIZE.values()) {
            if (weaponSize2 == w) {
                break;
            }
            j++;
        }

        return i - j;
    }

    public static PARAMS getPairedAttribute(ATTRIBUTE attr) {
        switch (attr) {
            case STRENGTH:
                return PARAMS.BASE_VITALITY;
            case VITALITY:
                return PARAMS.BASE_STRENGTH;
            case DEXTERITY:
                return PARAMS.BASE_AGILITY;
            case AGILITY:
                return PARAMS.BASE_DEXTERITY;
            case INTELLIGENCE:
                return PARAMS.BASE_KNOWLEDGE;
            case KNOWLEDGE:
                return PARAMS.BASE_INTELLIGENCE;
            case SPELLPOWER:
                return PARAMS.BASE_WILLPOWER;
            case WILLPOWER:
                return PARAMS.BASE_SPELLPOWER;
            case CHARISMA:
                return PARAMS.BASE_WISDOM;
            case WISDOM:
                return PARAMS.BASE_CHARISMA;
        }

        return null;
    }

    public static ObjType getBaseClassType(CLASS_GROUP classGroup) {
        return DataManager.getType(getBaseClassTypeName(classGroup), DC_TYPE.CLASSES);
    }

    private static String getBaseClassTypeName(CLASS_GROUP classGroup) {
        switch (classGroup) {
            case ACOLYTE:
                break;
            case FIGHTER:
                break;
            case HERMIT:
                break;
            case MULTICLASS:
                break;
            case ROGUE:
                break;
            case RANGER:
                return "Scout";
            case SORCERER:
                return "Apostate";
            case KNIGHT:
                return "Squire";
            case TRICKSTER:
                break;
            case WIZARD:
                return "Wizard Apprentice";
            default:
                break;

        }
        return classGroup.getName();
    }

    public static PARAMETER getHighestMastery(Unit hero) {
        List<PARAMETER> masteries = DC_MathManager.getUnlockedMasteries(hero);
        sortMasteries(hero, masteries);
        return masteries.get(0);
    }

    public static void sortMasteries(final Unit hero, List<PARAMETER> params) {
        Collections.sort(params, new Comparator<PARAMETER>() {
            public int compare(PARAMETER o1, PARAMETER o2) {
                Integer v1 = hero.getIntParam(o1);
                Integer v2 = hero.getIntParam(o2);
                if (v1 > v2) {
                    return -1;
                }
                if (v1 < v2) {
                    return 1;
                }
                return 0;
            }
        });

    }

    public static List<String> getBonusParamList() {
        ContentManager.getParamsForType("chars", false);
        List<String> list = new LinkedList<>();
        for (PARAMETER p : ContentManager.getParamsForType("chars", false)) {
            if (p.isAttribute()) {
                continue;
            }
            if (p.isMastery()) {
                continue;
            }
            if (p.getName().contains("Default")) {
                continue;
            }
            if (p.getName().contains(" Resistance")) {
                continue;
            }
            list.add(p.getName());
        }

        for (PARAMETER m : ValuePages.RESISTANCES) {
            list.add(m.getName());
        }
        for (PARAMS m : getMasteryParams()) {
            list.add(m.getName());
        }

        return list;
    }

    public static CLASS_GROUP getMainClassGroup(Unit hero) {
        return new EnumMaster<CLASS_GROUP>().retrieveEnumConst(CLASS_GROUP.class, hero
         .getProperty(PROPS.FIRST_CLASS));

    }

    public static List<ObjType> getMulticlassTypes() {
        return DataManager.getTypesSubGroup(DC_TYPE.CLASSES, "Multiclass");
    }

    public static boolean isMulticlass(ObjType type) {
        return type.getProperty(G_PROPS.CLASS_GROUP).equalsIgnoreCase("Multiclass");
    }

    public static Map<String, EDITOR> getEditorMap() {
        return editorMap;
    }

    public static void setEditorMap(Map<String, EDITOR> editorMap2) {
        editorMap = editorMap2;
    }

    public static CLASS_GROUP getClassGroupsFromClassType(CLASS_TYPE type) {
        // CLASS_GROUP group;
        // new ListMaster<>()
        return null;
    }

    public static PRINCIPLES getPrinciple(PARAMETER param) {
        return new EnumMaster<PRINCIPLES>().retrieveEnumConst(PRINCIPLES.class, StringMaster
         .openContainer(param.getName(), " ").get(0));
    }

    public static List<String> getStandardDeities() {
        // Collections.sort(list,new
        // EnumMaster().getEnumSorter(STD_DEITY_TYPE_NAMES ))
        return DataManager.getTypesGroupNames(DC_TYPE.DEITIES, "Playable");
    }

    public static String getStandardDeitiesString(String separator) {
        return StringMaster.constructStringContainer(getStandardDeities(), separator);
    }

    public static String getMainAttributeForClass(DC_FeatObj classObj) {

        CLASS_GROUP group = new EnumMaster<CLASS_GROUP>().retrieveEnumConst(CLASS_GROUP.class,
         classObj.getProperty(G_PROPS.CLASS_GROUP));
        if (group != null) {
            switch (group) {
                case ACOLYTE:
                    return "Wisdom;Charisma";
                case FIGHTER:
                    return "Strength;Vitality";
                case HERMIT:
                    return "Wisdom;Willpower";
                case KNIGHT:
                    return "Willpower;Charisma";
                case MULTICLASS:
                    // from base class? 1st and 2nd together
                    break;
                case RANGER:
                    return "Agility";
                case ROGUE:
                    return "Agility";
                case SORCERER:
                    return "Spellpower;Intelligence";
                case TRICKSTER:
                    return "Dexterity";
                case WIZARD:
                    return "Knowledge";
                default:
                    break;

            }
        }
        return null;

    }

    public static DC_WeaponObj getDefaultWeapon(Unit heroObj) {
        return new DC_WeaponObj(DataManager.getType(DEFAULT_WEAPON, DC_TYPE.WEAPONS), heroObj);
    }

    public static String getFocusMasteries() {
        if (focusMasteries != null) {
            return focusMasteries;
        }
        focusMasteries = "";
        for (SkillEnums.MASTERY m : SkillTreeView.FOCUS_WORKSPACE) {
            focusMasteries += StringMaster.getWellFormattedString(m.toString()) + ";";
        }
        return focusMasteries;
    }

    public static String getFocusClassGroups() {
        if (focusClassGroups != null) {
            return focusClassGroups;
        }
        focusClassGroups = "";
        for (CLASS_GROUP m : ClassTreeView.FOCUS_WORKSPACE) {
            focusClassGroups += StringMaster.getWellFormattedString(m.toString()) + ";";
        }
        return focusClassGroups;
    }

    public static List<VALUE> getArmorGradeMultiParams() {
        List<VALUE> list = new LinkedList<>();
        list.add(PARAMS.ARMOR);
        for (DAMAGE_TYPE dmg_type : GenericEnums.DAMAGE_TYPE.values()) {
            if (dmg_type != GenericEnums.DAMAGE_TYPE.POISON && dmg_type != GenericEnums.DAMAGE_TYPE.PHYSICAL) {
                if (dmg_type.isNatural() || !dmg_type.isMagical()) {
                    list.add(new MultiParameter(" / ", getArmorParamForDmgType(dmg_type),
                     getArmorSelfDamageParamForDmgType(dmg_type)));
                }
            }
        }
        return list;
    }

    public static PROPERTY getResistGradeForDmgType(DAMAGE_TYPE dmg_type) {
        return ContentManager.getPROP("RESIST_GRADE_" + dmg_type.name());
    }

    public static PROPERTY getSelfDamageGradeForDmgType(DAMAGE_TYPE dmg_type) {
        return ContentManager.getPROP("DURABILITY_GRADE_" + dmg_type.name());
    }

    public static PARAMETER getResistForDmgType(DAMAGE_TYPE dmg_type) {
        return ContentManager.getPARAM(dmg_type.name() + "_RESISTANCE");
    }

    public static PARAMETER getArmorSelfDamageParamForDmgType(DAMAGE_TYPE dmg_type) {
        // cache!
        return ContentManager.getPARAM(dmg_type.name() + "_DURABILITY_MOD");
    }

    public static PARAMETER getArmorParamForDmgType(DAMAGE_TYPE dmg_type) {
        // cache!
        return ContentManager.getPARAM(dmg_type.name() + "_ARMOR");
    }

    public static LinkedList<PARAMETER> getDynamicParams() {
        return dynamicParams;
    }

    public static PARAMS getPayParameterForCost(PARAMS costParam) {
        switch (costParam) {
            case AP_COST:
                return PARAMS.C_N_OF_ACTIONS;
            case ESS_COST:
                return PARAMS.C_ESSENCE;
            case STA_COST:
                return PARAMS.C_STAMINA;
            case FOC_COST:
                return PARAMS.C_FOCUS;
            case ENDURANCE_COST:
                return PARAMS.C_ENDURANCE;
            case CP_COST:
                return PARAMS.C_N_OF_COUNTERS;
        }
        return costParam;
    }

    public void init() {
        NO_SHOW_NAME_VALUES.addAll(Arrays.asList(ValuePages.GENERIC_DC_HEADER));
        NO_SHOW_NAME_VALUES.addAll(Arrays.asList(ValuePages.CHARS_HEADER));
        NO_SHOW_NAME_VALUES.addAll(Arrays.asList(ValuePages.BF_OBJ_HEADER));

        ArrayList<PARAMETER> params = new ArrayList<>();
        params.addAll(Arrays.asList(G_PARAMS.values()));
        params.addAll(Arrays.asList(PARAMS.values()));
        if (Launcher.isMacroMode() || CoreEngine.isArcaneVault()) {
            params.addAll(Arrays.asList(MACRO_PARAMS.values()));
        }

        ArrayList<PROPERTY> props = new ArrayList<>();
        props.addAll(Arrays.asList(G_PROPS.values()));
        props.addAll(Arrays.asList(PROPS.values()));
        if (Launcher.isMacroMode() || CoreEngine.isArcaneVault()) {
            props.addAll(Arrays.asList(MACRO_PROPS.values()));
        }

        params.addAll(generateDerivedParams());

        ContentManager.init(props, params);
        ContentManager.setParamEnumClasses(PARAM_ENUM_CLASSES);
        ContentManager.setPropEnumClasses(PROP_ENUM_CLASSES);

        ValuePageManager.init();

        EnumMaster.setALT_CONSTS_CLASS(DC_CONSTS.class);
        // initTypeDynamicValues(); TODO !
    }
    public static OBJ_TYPE getTypeForProperty(PROPS prop) {
        switch (prop){
            case QUICK_ITEMS:
                return C_OBJ_TYPE.QUICK_ITEMS;
            case JEWELRY:
                return DC_TYPE.JEWELRY;
            case INVENTORY:
                return C_OBJ_TYPE.ITEMS;
        }
        return C_OBJ_TYPE.ITEMS;
    }

    public enum ATTRIBUTE {
        STRENGTH(PARAMS.STRENGTH, true, PARAMS.TOUGHNESS, PARAMS.CARRYING_CAPACITY),
        VITALITY(PARAMS.VITALITY, true, PARAMS.TOUGHNESS, PARAMS.ENDURANCE, PARAMS.REST_BONUS, PARAMS.ENDURANCE_REGEN),
        AGILITY(PARAMS.AGILITY, true),
        DEXTERITY(PARAMS.DEXTERITY, true),
        WILLPOWER(PARAMS.WILLPOWER, false),
        INTELLIGENCE(PARAMS.INTELLIGENCE, false),
        WISDOM(PARAMS.WISDOM, false),
        KNOWLEDGE(PARAMS.KNOWLEDGE, false),
        SPELLPOWER(PARAMS.SPELLPOWER, false),
        CHARISMA(PARAMS.CHARISMA, false),;
        private PARAMS parameter;
        private PARAMS[] params;

        ATTRIBUTE(PARAMS attr_param, boolean physical, PARAMS... params) {
            this.setParameter(attr_param);
            this.setParams(params);
        }

        public PARAMS[] getParams() {
            return params;
        }

        public void setParams(PARAMS[] params) {
            this.params = params;
        }

        public PARAMS getParameter() {
            return parameter;
        }

        public void setParameter(PARAMS parameter) {
            this.parameter = parameter;
        }
    }

}
