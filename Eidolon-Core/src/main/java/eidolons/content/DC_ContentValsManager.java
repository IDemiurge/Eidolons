package eidolons.content;

import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.values.ValueInitializer;
import eidolons.content.values.ValuePageManager;
import eidolons.content.values.ValuePages;
import eidolons.content.values.ValueTypePairs;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.attach.DC_PassiveObj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.mechanics.CoatingRule;
import eidolons.game.core.game.DC_Game;
import eidolons.netherflame.eidolon.heromake.passives.SkillMaster;
import main.content.*;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.ASPECT;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.ClassEnums;
import main.content.enums.entity.HeroEnums.*;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.WEAPON_SIZE;
import main.content.enums.entity.RpgEnums;
import main.content.enums.entity.SkillEnums;
import main.content.enums.entity.EffectEnums.COUNTER;
import main.content.values.parameters.*;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.EDITOR;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.auxiliary.data.ArrayMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.*;

import static main.content.enums.entity.SkillEnums.MASTERY.*;

public class DC_ContentValsManager extends ContentValsManager {

    private static final ObjectMap<OBJ_TYPE, Set<VALUE>> overrideCache = new ObjectMap<>();

    static {
        ValueInitializer.init();
        initCaches();
        Arrays.stream(PARAMS.values()).forEach(param -> {
            if (param.isDynamic()) {
                ContentConsts.dynamicParams.add(param);
            }
        });

        ArrayList<PARAMETER> list = new ArrayList<>();

        ContentConsts.ARMOR_MODIFYING_PARAMS_FULL = new ArrayList<>();
        ContentConsts.ARMOR_MODIFYING_PARAMS_FULL.addAll(Arrays.asList(ContentConsts.ARMOR_MODIFYING_PARAMS));
        ContentConsts.ARMOR_MODIFYING_PARAMS_FULL.addAll(Arrays.asList(ValuePages.RESISTANCES));

        // list.addAll((ARMOR_MODIFYING_PARAMS_FULL));
        // list.addAll(Arrays.asList(WEAPON_MODIFYING_PARAMS));
        list.addAll(Arrays.asList(ValuePages.UNIT_PARAMETERS));
        list.addAll(Arrays.asList(ValuePages.MASTERIES));
        list.addAll(Arrays.asList(ContentConsts.FEAT_MODIFYING_PARAMS));

        ContentConsts.FEAT_MODIFYING_PARAMS = list.toArray(new PARAMETER[0]);
    }

    public DC_ContentValsManager() {
        super();
    }

    public static void initCaches(){
        for (Object[] cachePair : ValueTypePairs.cachePairs) {
           Set<VALUE> set = new ArrayMaster<VALUE>().flattenToSet((VALUE[][]) cachePair[1]);
            overrideCache.put((OBJ_TYPE) cachePair[0], set);
        }
    }

    protected Boolean getValueForTypeOverride(OBJ_TYPE type, VALUE p) {
        Set<VALUE> override = overrideCache.get(type);
        if (override != null) {
            return override.contains(p);
        }
        return null;
    }

    public static void initTypeDynamicValues() {
        for (ObjType t : DataManager.getTypes(DC_TYPE.CHARS)) {
            if (!t.getGroup().equals(Strings.BACKGROUND)) {
            }

        }

    }

    public boolean checkAllApplies(VALUE p, OBJ_TYPE type) {
        if (p instanceof G_PROPS) {
            switch (((G_PROPS) p)) {
                case TOOLTIP:
                case LORE:
                case PASSIVES:
                case UNIQUE_ID:
                case DESCRIPTION:
                case STD_BOOLS:
                case DYNAMIC_BOOLS:
                    return checkAllApplies_((G_PROPS) p,type);
            }
        }
        return true;
    }

    public boolean checkAllApplies_(G_PROPS p, OBJ_TYPE type) {
        if (type instanceof DC_TYPE) {
            switch (((DC_TYPE) type)) {
                case SKILLS:
                    switch (p) {
//TODO
                    }
                    break;
                case SPELLS:
            }
        }
        return true;
    }
    private static Collection<PARAMETER> generateDerivedParams() {
        Collection<PARAMETER> list = new ArrayList<>();
        for (PARAMS p : PARAMS.values()) {
            if (p.isMastery()) {
                Param scoreParam = new Param(p);
                scoreParam.setName(p.getName() + Strings.SCORE);
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
        return ContentValsManager.getPARAM(PARAMS.COATING_COUNTERS_APPLIED_PER_HIT_MOD.getName()
         .replace("Coating Counters", c.getName()));
    }

    public static PARAMETER getCoatingAppliedModParam(COUNTER c) {
        return ContentValsManager.getPARAM(PARAMS.COATING_COUNTERS_APPLIED_TO_ITEM_MOD.getName()
         .replace("Coating Counters", c.getName()));
    }

    public static Collection<PROPERTY> generateDerivedProperties() {
        Collection<PROPERTY> list = new ArrayList<>();

        for (PROPS p : PROPS.values()) {

        }

        return list;

    }

    public static List<ATTRIBUTE> getAttributeEnums() {
        return Arrays.asList(ATTRIBUTE.values());
    }


    public static PARAMETER getAlignmentForPrinciple(RpgEnums.PRINCIPLES principle) {
        PARAMETER param = ContentConsts.alignmentMap.get(principle);
        if (param != null) {
            return param;
        }
        param = ContentValsManager.getPARAM(principle.toString() + Strings.ALIGNMENT);
        ContentConsts.alignmentMap.put(principle, param);
        return param;
    }

    public static PARAMETER getIdentityParamForPrinciple(RpgEnums.PRINCIPLES principle) {
        // OPTIMIZATION: having a param field on each Principle const!?
        // faster...
        PARAMETER param = ContentConsts.identityMap.get(principle);
        if (param != null) {
            return param;
        }
        param = ContentValsManager.getPARAM(principle.toString() + Strings.IDENTITY);
        ContentConsts.identityMap.put(principle, param);
        return param;
    }

    public static List<PARAMS> getMasteryParams() {
        if (ContentConsts.masteries != null) {
            return ContentConsts.masteries;
        }
        ContentConsts.masteries = new ArrayList<>();
        for (PARAMETER m : ContentValsManager.getMasteries()) {
            ContentConsts.masteries.add((PARAMS) m);
        }
        return ContentConsts.masteries;
    }

    public static Collection<String> getLimitedInfoPanelValueList(String objType) {
        return Arrays.asList(ContentConsts.unknownValues);
    }

    public static Collection<String> getInfoPanelValueList(String objType) {
        if (objType == null) {
            return Collections.EMPTY_LIST;
        }
        List<String> valueNames = ContentValsManager.getValueNamesMap().get(objType);
        if (valueNames != null)
            return valueNames;
        valueNames = ContentValsManager.getFullValueList(objType);

        // for (VALUE v : excludedValuesFromAll) {
        // valueNames.remove(v.getName());
        // }
        try {
            if (DC_TYPE.getCode(objType) < ContentConsts.excludedValues.length) {
                for (VALUE v : ContentConsts.excludedValues[DC_TYPE.getCode(objType)]) {
                    valueNames.remove(v.getName());
                }
            }
        } catch (Exception e) {

        }
        ContentValsManager.getValueNamesMap().put(objType, valueNames);
        return valueNames;

    }

    public static PARAMETER[] getArmorModifyingParams() {
        return ContentConsts.ARMOR_MODIFYING_PARAMS_FULL
         .toArray(new PARAMETER[0]);
    }

    public static PARAMETER[] getWeaponModifyingParams() {
        return ContentConsts.WEAPON_MODIFYING_PARAMS;
    }

    public static PARAMETER[] getFeatModifyingParams() {
        return ContentConsts.FEAT_MODIFYING_PARAMS;
    }

    public static DC_TYPE[] getBF_TYPES() {
        return ContentConsts.BF_OBJ_TYPES;
    }

    public static PARAMETER getDamageTypeResistance(DAMAGE_TYPE type) {
        return ContentValsManager.getPARAM(type.getResistanceName());

    }

    public static PARAMETER getBaseAttr(ATTRIBUTE attr) {
        return getBaseAttr(attr.getParameter());
    }

    public static PARAMETER getDefaultAttr(PARAMETER param) {
        return ContentValsManager.getPARAM(Strings.DEFAULT + param.toString());
    }

    public static PARAMETER getBaseAttr(PARAMETER param) {
        PARAMETER base = ContentValsManager.getPARAM(Strings.BASE + param.toString());
        if (base!=null )
            return base;
        return param;
    }

    public static Object getHeaderValues(OBJ_TYPE obj_TYPE_ENUM) {
        // TODO Auto-generated method stub
        return null;
    }

    // public static PARAMETER getFinalAttrFromBase(PARAMETER param) {
    // return ContentManager.getPARAM(StringMaster.BASE + param.toString());
    // }

    public static PARAMS[] getCostParams() {
        return ContentConsts.COST_PARAMS;
    }

    @Deprecated
    public static PARAMETER getSpecialCostReductionParam(PARAMETER costParam, PROPERTY p) {
        String valueName = null;
        if (p == PROPS.LEARNED_SPELLS) {
            valueName = costParam.getName() + Strings.REDUCTION + "_" + p.getName();
        }
        if (p == PROPS.SKILLS) {
            valueName = costParam.getName() + Strings.REDUCTION + "_" + p.getName();
        }
        return ContentValsManager.getPARAM(valueName);
    }

    public static PARAMETER getCostReductionParam(PARAMETER costParam, PROPERTY p) {
        String valueName = costParam.getName() + Strings.REDUCTION;
        return ContentValsManager.getPARAM(valueName);
    }

    public static boolean isShowValueName(VALUE value) {
        return !ContentConsts.NO_SHOW_NAME_VALUES.contains(value);
    }

    public static PARAMETER[] getWeaponWeightPenaltyParams() {
        return new PARAMETER[]{
         // PARAMS.ATTACK_MOD,
         PARAMS.ATTACK_TOUGHNESS_COST_MOD, PARAMS.ATTACK_ATB_COST_MOD, PARAMS.SPELL_FOC_COST_MOD,
         PARAMS.SPELL_ATB_COST_MOD,};
    }

    public static PARAMETER[] getArmorWeightPenaltyParams() {
        return new PARAMETER[]{
         // PARAMS.DEFENSE_MOD,
         PARAMS.MOVE_TOU_COST_MOD, PARAMS.MOVE_ATB_COST_MOD,
         PARAMS.SPELL_ESS_COST_MOD,};
    }

    public static void addDefaultValues(Entity entity, boolean dynamic) {
        addDefaultValues(entity, dynamic, ContentValsManager.getValueList());
    }

    public static void addDefaultValues(Entity entity, boolean dynamic,
                                        Collection<VALUE> vals) {
        for (VALUE VAL : vals) {
            if (!ContentValsManager.isValueForOBJ_TYPE(entity.getOBJ_TYPE_ENUM(), VAL)) {
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

            if ((!unit && StringMaster.isEmpty(entity.getValue(VAL)) && !StringMaster
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


    private static String getDefaultValueSpecial(Entity entity, VALUE v) {
        // if (v.getSpecialDefault(entity.getOBJ_TYPE_ENUM()) != null) {
        //     return v.getSpecialDefault(entity.getOBJ_TYPE_ENUM()).toString();
        // }
        // Core Review - this is too heavy and brutish, do we even need it really?
        return null;
    }

    public static void addDefaultValues(Entity entity) {
        //this should be done in AV!!!
        for (String value : DC_ContentValsManager.getInfoPanelValueList(entity.getOBJ_TYPE())) {
            VALUE VAL = ContentValsManager.getValue(value);
            if (VAL == null) {
                continue;
            }
            String defaultValue = VAL.getDefaultValue();

            if (StringMaster.isEmpty(entity.getValue(VAL))
             && !StringMaster.isEmptyOrZero(defaultValue)) {
                if (entity instanceof Obj) {
                    entity.getType().setValue(VAL, defaultValue);
                }
                entity.setValue(VAL, defaultValue);
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
            case LIGHT:
                return GenericEnums.DAMAGE_TYPE.HOLY;
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
    // list = new ArrayList<>();
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
        if (ContentValsManager.isParameter(sparam)) {
            return new PARAMETER[]{ContentValsManager.getPARAM(sparam)};
        }
        PARAMETER param = ContentValsManager.getMastery(sparam);

        if (param != null) {
            return new PARAMETER[]{param};
        }
        if (ContainerUtils.openContainer(sparam, Strings.VERTICAL_BAR).size() > 1) {
            return DC_Game.game.getValueManager().getParamsFromContainer(sparam);
        } else {
            return DC_Game.game.getValueManager().getValueGroupParams(sparam);
        }
    }

    public static boolean isParamFloatDisplayed(PARAMETER param) {
        if (param.isAttribute()) {
            return !param.getName().contains("Base");
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

    public static ObjType getBaseClassType(ClassEnums.CLASS_GROUP classGroup) {
        return DataManager.getType(getBaseClassTypeName(classGroup), DC_TYPE.CLASSES);
    }

    private static String getBaseClassTypeName(ClassEnums.CLASS_GROUP classGroup) {
        switch (classGroup) {
            case RANGER:
                return "Scout";
            case SORCERER:
                return "Apostate";
            case KNIGHT:
                return "Squire";
            case WIZARD:
                return "Wizard Apprentice";
            default:
                break;

        }
        return classGroup.getName();
    }

    public static PARAMETER getHighestMastery(Unit hero) {
        List<PARAMETER> masteries = SkillMaster.getUnlockedMasteries(hero);
        sortMasteries(hero, masteries);
        return masteries.get(0);
    }

    public static void sortMasteries(final Unit hero, List<PARAMETER> params) {
        params.sort(new Comparator<PARAMETER>() {
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
        ContentValsManager.getParamsForType("chars", false);
        List<String> list = new ArrayList<>();
        for (PARAMETER p : ContentValsManager.getParamsForType("chars", false)) {
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

    public static ClassEnums.CLASS_GROUP getMainClassGroup(Unit hero) {
        return new EnumMaster<ClassEnums.CLASS_GROUP>().retrieveEnumConst(ClassEnums.CLASS_GROUP.class, hero
         .getProperty(PROPS.FIRST_CLASS));

    }

    public static List<ObjType> getMulticlassTypes() {
        return DataManager.getTypesSubGroup(DC_TYPE.CLASSES, "Multiclass");
    }

    public static boolean isMulticlass(ObjType type) {
        return type.getProperty(G_PROPS.CLASS_GROUP).equalsIgnoreCase("Multiclass");
    }

    public static Map<String, EDITOR> getEditorMap() {
        return ContentConsts.editorMap;
    }

    public static void setEditorMap(Map<String, EDITOR> editorMap2) {
        ContentConsts.editorMap = editorMap2;
    }

    public static ClassEnums.CLASS_GROUP getClassGroupsFromClassType(ClassEnums.CLASS_TYPE type) {
        // CLASS_GROUP group;
        // new ListMaster<>()
        return null;
    }

    public static RpgEnums.PRINCIPLES getPrinciple(PARAMETER param) {
        return new EnumMaster<RpgEnums.PRINCIPLES>().retrieveEnumConst(RpgEnums.PRINCIPLES.class, ContainerUtils
         .openContainer(param.getName(), " ").get(0));
    }

    public static List<String> getStandardDeities() {
        // Collections.sort(list,new
        // EnumMaster().getEnumSorter(STD_DEITY_TYPE_NAMES ))
        return DataManager.getTypesGroupNames(DC_TYPE.DEITIES, "Playable");
    }

    public static String getStandardDeitiesString(String separator) {
        return ContainerUtils.constructStringContainer(getStandardDeities(), separator);
    }

    public static String getMainAttributeForClass(DC_PassiveObj classObj) {

        ClassEnums.CLASS_GROUP group = new EnumMaster<ClassEnums.CLASS_GROUP>().retrieveEnumConst(ClassEnums.CLASS_GROUP.class,
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

    public static WeaponItem getDefaultWeapon(Unit heroObj) {
        return new WeaponItem(DataManager.getType(ContentConsts.DEFAULT_WEAPON, DC_TYPE.WEAPONS), heroObj);
    }


    public static List<VALUE> getArmorGradeMultiParams() {
        List<VALUE> list = new ArrayList<>();
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
        return ContentValsManager.getPROP("RESIST_GRADE_" + dmg_type.name());
    }

    public static PROPERTY getSelfDamageGradeForDmgType(DAMAGE_TYPE dmg_type) {
        return ContentValsManager.getPROP("DURABILITY_GRADE_" + dmg_type.name());
    }

    public static PARAMETER getResistForDmgType(DAMAGE_TYPE dmg_type) {
        return ContentValsManager.getPARAM(dmg_type.name() + "_RESISTANCE");
    }

    public static PARAMETER getArmorSelfDamageParamForDmgType(DAMAGE_TYPE dmg_type) {
        // cache!
        return ContentValsManager.getPARAM(dmg_type.name() + "_DURABILITY_MOD");
    }

    public static PARAMETER getArmorParamForDmgType(DAMAGE_TYPE dmg_type) {
        // cache!
        return ContentValsManager.getPARAM(dmg_type.name() + "_ARMOR");
    }

    public static List<PARAMETER> getDynamicParams() {
        return ContentConsts.dynamicParams;
    }

    public static PARAMS getPayParameterForCost(PARAMS costParam) {
        switch (costParam) {
            case ESS_COST:
                return PARAMS.C_ESSENCE;
            case TOU_COST:
                return PARAMS.C_TOUGHNESS;
            case FOC_COST:
                return PARAMS.C_FOCUS;
            case ENDURANCE_COST:
                return PARAMS.C_ENDURANCE;
        }
        return costParam;
    }

    public static OBJ_TYPE getTypeForProperty(PROPERTY prop) {
        if (prop instanceof PROPS)
            return getTypeForProp((PROPS) prop);
        if (prop instanceof G_PROPS)
            switch ((G_PROPS) prop) {
                case MAIN_HAND_ITEM:
                case OFF_HAND_ITEM:
                    return DC_TYPE.WEAPONS;
                case ARMOR_ITEM:
                    return DC_TYPE.ARMOR;
            }
        return C_OBJ_TYPE.ITEMS;
    }

    public static OBJ_TYPE getTypeForProp(PROPS prop) {
        switch (prop) {
            case QUICK_ITEMS:
                return C_OBJ_TYPE.QUICK_ITEMS;
            case JEWELRY:
                return DC_TYPE.JEWELRY;
            case INVENTORY:
                return C_OBJ_TYPE.ITEMS;
        }
        return C_OBJ_TYPE.ITEMS;
    }

    public static SUBRACE[] getSubraces(RACE race) {
        switch (race) {
            case HUMAN:
                return new SUBRACE[]{
                 SUBRACE.MAN_OF_KINGS_REALM,
                 SUBRACE.MAN_OF_EAGLE_REALM,
                 SUBRACE.MAN_OF_GRIFF_REALM,
                 SUBRACE.MAN_OF_RAVEN_REALM,
                 SUBRACE.MAN_OF_WOLF_REALM,
                };
            case ELF:
                return new SUBRACE[]{
                 SUBRACE.FEY_ELF,
                 SUBRACE.GREY_ELF,
                 SUBRACE.WOOD_ELF,
                 SUBRACE.HIGH_ELF,
                };
            case DWARF:
                return new SUBRACE[]{
                 SUBRACE.MOONSILVER_DWARF,
                 SUBRACE.STONESHIELD_DWARF,
                 SUBRACE.IRONHELM_DWARF,
                 SUBRACE.WOLFSBANE_DWARF,
                 SUBRACE.RUNESMITH_DWARF,
                 SUBRACE.GRIMBART_DWARF,

                 //                 BACKGROUND.FROSTBEARD_DWARF,
                 //                 BACKGROUND.WILDAXE_DWARF,
                 //                 BACKGROUND.REDBLAZE_DWARF,
                };
        }
        return new SUBRACE[0];
    }

    public static VALUE[] getDisplayedHeroProperties() {
        return ContentConsts.displayedHeroProperties;
    }

    public static void setDisplayedHeroProperties(VALUE[] displayedHeroProperties) {
        ContentConsts.displayedHeroProperties = displayedHeroProperties;
    }

    public void init() {
        ContentConsts.NO_SHOW_NAME_VALUES.addAll(Arrays.asList(ValuePages.GENERIC_DC_HEADER));
        ContentConsts.NO_SHOW_NAME_VALUES.addAll(Arrays.asList(ValuePages.CHARS_HEADER));
        ContentConsts.NO_SHOW_NAME_VALUES.addAll(Arrays.asList(ValuePages.BF_OBJ_HEADER));

        ArrayList<PARAMETER> params = new ArrayList<>();
        params.addAll(Arrays.asList(G_PARAMS.values()));
        params.addAll(Arrays.asList(PARAMS.values()));
        params.addAll(Arrays.asList(MACRO_PARAMS.values()));

        ArrayList<PROPERTY> props = new ArrayList<>();
        props.addAll(Arrays.asList(G_PROPS.values()));
        props.addAll(Arrays.asList(PROPS.values()));
        props.addAll(Arrays.asList(MACRO_PROPS.values()));

        params.addAll(generateDerivedParams());

        ContentValsManager.init(props, params);
        ContentValsManager.setParamEnumClasses(ContentConsts.PARAM_ENUM_CLASSES);
        ContentValsManager.setPropEnumClasses(ContentConsts.PROP_ENUM_CLASSES);

        ValuePageManager.init();

        EnumMaster.setALT_CONSTS_CLASS(DC_CONSTS.class);
        // initTypeDynamicValues(); TODO !
    }


    public static SkillEnums.MASTERY[] getMasteriesFromSkillGroup(SkillEnums.SKILL_GROUP group) {
        switch (group) {
            case BODY_MIND:
                return new SkillEnums.MASTERY[]{
                        ATHLETICS_MASTERY,
                        MOBILITY_MASTERY,
                        MEDITATION_MASTERY,
                        DISCIPLINE_MASTERY,

                };
            case SPELLCASTING:
                return new SkillEnums.MASTERY[]{
                        SPELLCRAFT_MASTERY,
                        WIZARDRY_MASTERY,
                        DIVINATION_MASTERY,

                };
            case WEAPONS:
                return new SkillEnums.MASTERY[]{
                        BLADE_MASTERY,
                        AXE_MASTERY,
                        BLUNT_MASTERY,
                        POLEARM_MASTERY,
                };
            case OFFENSE:
                return new SkillEnums.MASTERY[]{
                        TWO_HANDED_MASTERY,
                        DUAL_WIELDING_MASTERY,
                        MARKSMANSHIP_MASTERY,
                        UNARMED_MASTERY,
                };
            case DEFENSE:
                return new SkillEnums.MASTERY[]{
                        DEFENSE_MASTERY,
                        SHIELD_MASTERY,
                        STEALTH_MASTERY,
                        DETECTION_MASTERY,

                };
            case COMMAND:
                return new SkillEnums.MASTERY[]{
                        LEADERSHIP_MASTERY,
                        TACTICS_MASTERY,
                };
            case CRAFT:
                return new SkillEnums.MASTERY[]{
                        ARMORER_MASTERY,
                        ITEM_MASTERY,

                };
            case PRIME_ARTS:
                return new SkillEnums.MASTERY[]{
                        FIRE_MASTERY,
                        AIR_MASTERY,
                        WATER_MASTERY,
                };
            case ARCANE_ARTS:
                return new SkillEnums.MASTERY[]{
                        CONJURATION_MASTERY,
                        SORCERY_MASTERY,
                        ENCHANTMENT_MASTERY,

                };
            case LIFE_ARTS:
                return new SkillEnums.MASTERY[]{
                        EARTH_MASTERY,
                        SAVAGE_MASTERY,
                        SYLVAN_MASTERY,
                };
            case DARK_ARTS:
                return new SkillEnums.MASTERY[]{
                        PSYCHIC_MASTERY,
                        SHADOW_MASTERY,
                        WITCHERY_MASTERY,

                };
            case CHAOS_ARTS:
                return new SkillEnums.MASTERY[]{
                        WARP_MASTERY,
                        DESTRUCTION_MASTERY,
                        DEMONOLOGY_MASTERY,

                };
            case HOLY_ARTS:
                return new SkillEnums.MASTERY[]{
                        REDEMPTION_MASTERY,
                        BENEDICTION_MASTERY,
                        CELESTIAL_MASTERY,
                };
            case DEATH_ARTS:
                return new SkillEnums.MASTERY[]{
                        AFFLICTION_MASTERY,
                        BLOOD_MAGIC_MASTERY,
                        NECROMANCY_MASTERY,
                };
        }
        return null;
    }
}
