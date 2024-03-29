package eidolons.content;

import main.content.C_OBJ_TYPE;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.RpgEnums.PRINCIPLES;
import main.content.enums.entity.RpgEnums;
import main.content.values.properties.PROPERTY;
import main.system.auxiliary.StringMaster;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// Qualitative properties ..*what?*
public enum PROPS implements PROPERTY { // SPECIAL_ATTACKS, MOVES, ACTIONS
    // INSTEAD OF 'ACTIVES'!
    // AGE(null, "chars"),
    DIALOGUE_DATA(null, false, "dialogue"),

    //TODO cleanup - old scenario logic
    SCENARIO_TYPE(null, false, "scenarios"),
    SCENARIO_MISSIONS(null, true, "scenarios"),
    SCENARIO_PARTY(null, false, "scenarios"),
    DIFFICULTY(null, false, "scenarios", "dungeons", "chars"),

    BRIEFING_DATA(null, false, "dungeons"),
    LOADING_SCREEN(null, false, "dungeons"),
    FLOOR_TEMPLATE_PATH(null, false, "dungeons"),
    FLOOR_FILE_PATH(null, false, "dungeons"),
    FLOOR_SCRIPTS(null, true, "dungeons"),

    DUNGEON_TAGS(null, true, "dungeons"),
    MAP_BACKGROUND(null, false, "dungeons", "area"),

    // TODO remove useless props... (just disabled for now)
    COLOR_THEME(null, false, "bf obj", "dungeons"),
    ALT_COLOR_THEME(null, false, "dungeons"),

    SUBDUNGEON_TYPE(null, false, "dungeons"),

    MASTERY_GROUPS_MAGIC(null, true, "units", "chars"),
    MASTERY_GROUPS_WEAPONS(null, true, "units", "chars"),
    MASTERY_GROUPS_MISC(null, true, "units", "chars"),


    ACTION_PRIORITY_MODS(null, true, "units", "chars"),
    ACTION_PRIORITY_BONUSES(null, true, "units", "chars"),


    //TODO LC 2.0 - Unit Progression
    LVL_PLAN(null, true, "units"),
    GOLD_PLAN(null, true, "units"),
    ATTRIBUTE_PROGRESSION(null, true, "units", "chars"),
    MASTERY_PROGRESSION(null, true, "units", "chars"),
    VERBATIM_PRIORITY(null, true, "units", "chars"),
    MEMORIZATION_PRIORITY(null, true, "units", "chars"),

    MAIN_HAND_REPERTOIRE(null, true, "units", "chars"),
    OFF_HAND_REPERTOIRE(null, true, "units", "chars"),
    ARMOR_REPERTOIRE(null, true, "units", "chars"),
    QUICK_ITEM_REPERTOIRE(null, true, "units", "chars"),
    JEWELRY_ITEM_TRAIT_REPERTOIRE(null, true, "units", "chars"),
    JEWELRY_PASSIVE_ENCHANTMENT_REPERTOIRE(null, true, "units", "chars"),

    QUALITY_LEVEL_RANGE(null, true, "units", "chars"),
    ALLOWED_MATERIAL(null, true, "units", "chars"),

    // quick items/jewelry?

    AI_LOGIC(null, false, "actions", "spells"),
    AI_TYPE(null, false, "units", "chars"),
    AI_MODIFIERS(null, true, "units", "chars"),
    AI_PRIORITY_FORMULA(null, false, "actions", "spells"),
    BEHAVIOR_MODE(null, false, "units", "chars"),
    STANDARD_ACTION_PASSIVES("STD_ACTION_PASSIVES", true, "actions"),
    STANDARD_SPELL_PASSIVES("STD_SPELL_PASSIVES", true, "spells"),

    LAST_SEEN(null, true, "units", "chars", "bf obj"),
    HINTS(null, true, "units", "chars", "bf obj"),
    FACING_DIRECTION(null, false, "units", "chars", "bf obj"),
    DIRECTION(null, false, "units", "chars", "bf obj"),

    VISIBILITY_STATUS(null, false, "units", "chars", "bf obj", "terrain"),
    DETECTION_STATUS(null, false, "units", "chars", "bf obj", "terrain"),
    DAMAGE_TYPE(null, false, "units", "chars", "weapons", "actions", "items", "spells"),
    VISION_MODE(null, false, "units", "chars", "bf obj"),

    OBJECT_ARMOR_TYPE(null, false, "units", "chars", "bf obj"),
    BF_OBJECT_SIZE(null, false, "bf obj"),
    BF_OBJ_MATERIAL(null, false, "bf obj"),
    BF_OBJ_QUALITY(null, true, "bf obj"),
    CONTAINER_CONTENTS(null, true, "bf obj"),
    CONTAINER_CONTENT_VALUE(null, true, "bf obj"),

    CONTAINER_GROUP_FILTER(null, false, "bf obj"),
    CONTAINER_GROUP_SINGLE(null, false, "bf obj"),
    DIMENSION(null, false, "bf obj", "units", "chars"),

    //into feats?
    SKILLS("Skills", true, "chars", "units"),
    SKILLS_TIER_1("Skills", true, "chars", "units"),
    SKILLS_TIER_2("Skills", true, "chars"),
    SKILLS_TIER_3("Skills", true, "chars"),
    SKILLS_TIER_4("Skills", true, "chars"),
    SKILLS_TIER_5("Skills", true, "chars"),

    SPECIAL_ABILITIES("SPECIAL_ABILITIES", true, "chars", "units"),
    SKILL_REQUIREMENTS("Skill REQ", true, "spells", "skills", "classes"),
    SKILL_OR_REQUIREMENTS("Skill OR REQ", true, "spells", "skills", "classes"),
    //    TREE_NODE_GROUP(null, false, "spells", "skills", "classes"),
    //    LINK_VARIANT(null, false, "spells", "skills", "classes"),
    //    TREE_NODE_PRIORITY(null, false, "spells", "skills", "classes"),

    CLASSES("Classes", true, "chars"),
    CLASSES_TIER_1("Classes", true, "chars"),
    CLASSES_TIER_2("Classes", true, "chars"),
    CLASSES_TIER_3("Classes", true, "chars"),
    CLASSES_TIER_4("Classes", true, "chars"),
    CLASSES_TIER_5("Classes", true, "chars"),

    REQUIREMENTS("Requirements", true, "spells", "skills", "classes"),
    //TODO Cleanup
    INVENTORY(null, true, "units", "chars"),
    FIRST_CLASS("chars", "First Class"),
    SECOND_CLASS("chars", "Second Class"),
    THIRD_CLASS("chars", "Third Class"),

    QUICK_ITEMS(null, true, "units", "chars"),
    JEWELRY(null, true, "chars"),
    OFFHAND_NATURAL_WEAPON(null, false, "units", "chars"),
    NATURAL_WEAPON(null, false, "units", "chars"),

    // all spells
    LEARNED_SPELLS(null, true, "units", "chars"),
    LEARNED_ACTIONS(null, true, "units", "chars"),
    LEARNED_PASSIVES(null, true, "units", "chars"),
    TOKENS(null, true, "units", "chars"),


    BUFF_NAME(null, true, "spells", "actions", "items"),
    RESISTANCE_TYPE("spells", "Resistance type"),
    TARGETING_MODIFIERS(null, true, "spells", "actions", "items"),
    // TARGETING_CONDITIONS(null, true, "spells", "actions", "items"), via spec
    // req "custom"
    EFFECTS_WRAP("EFFECTS WRAP", false, "spells", "actions", "items"),
    RESISTANCE_MODIFIERS("", true, "spells", "actions"),

    ON_BEING_HIT("", true, "units", "chars", "armor"),
    ON_HIT("ON_HIT ", true, "spells", "actions", "units", "chars", "weapons"),
    ON_ACTIVATE("", true, "spells", "actions"),
    ON_KILL("", true, "spells", "actions", "units", "chars", "weapons"),
    WEAPON_ATTACKS("", true, "weapons"),

    JEWELRY_PASSIVE_ENCHANTMENT("jewelry", null),
    MAGICAL_ITEM_LEVEL("jewelry", null),
    MAGICAL_ITEM_TRAIT("jewelry", null),


    FILLER_TYPES("encounters", null),
    GROWTH_PRIORITIES(null, true, "encounters"),
    EXTENDED_PRESET_GROUP(null, true, "encounters"),
    PRESET_GROUP("PRESET_GROUP", true, "encounters"),
    SHRUNK_PRESET_GROUP(null, true, "encounters"),

    REINFORCEMENT_TYPE("encounters", null),
    REINFORCEMENT_CUSTOM("encounters", null),
    REINFORCEMENT_CHANCE("encounters", null),
    REINFORCEMENT_STRENGTH("encounters", null),

    UNIT_GROUP_TYPE("encounters", null),
    LOOT_TYPE("encounters", null),
    AI_BEHAVIOR_MODE("encounters", null),
    PRE_BATTLE_EVENT("encounters", null),
    AFTER_BATTLE_EVENT("encounters", null),

    @Deprecated
    DROPPED_ITEMS(null, true, "terrain"),
    BASE_CLASSES_ONE(null, true, "classes"),
    BASE_CLASSES_TWO(null, true, "classes"),
    MULTICLASSES(null, true, "chars"),

    ROLL_TYPES_TO_SAVE(null, true, "spells", "actions"),
    ROLL_TYPES_TO_DISPEL_EACH_TURN(null, true, "spells", "actions"),
    CHANNELING_SOUND(null, false, "spells", "actions"),
    RETAIN_CONDITIONS(null, false, "spells", "actions"),
    ITEM_SHOP_CATEGORY(null, false, C_OBJ_TYPE.ITEMS),
    ITEM_RARITY(null, false, C_OBJ_TYPE.ITEMS),

    ALT_BASE_TYPES(null, true, "skills", "classes"),
    PARAMETER_BONUSES(null, true, "skills", "classes", "perks"),
    ATTRIBUTE_BONUSES(null, true, "skills", "classes", "perks"),
    APPLY_REQS(null, true, "skills", "classes"),


    TERRAIN_TYPE(null, false, "terrain"),


    RESIST_GRADE_PIERCING(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_PIERCING", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_BLUDGEONING(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_BLUDGEONING", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_SLASHING(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_SLASHING", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_POISON(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_POISON", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_FIRE(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_FIRE", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_COLD(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_COLD", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_LIGHTNING(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_LIGHTNING", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_ACID(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_ACID", "_")), false, "units", "chars", "weapons", "armor"),

    RESIST_GRADE_SONIC(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_SONIC", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_LIGHT(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_LIGHT", "_")), false, "units", "chars", "weapons", "armor"),

    RESIST_GRADE_ARCANE(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_ARCANE", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_CHAOS(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_CHAOS", "_")), false, "units", "chars", "weapons", "armor"),

    RESIST_GRADE_SHADOW(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_SHADOW", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_HOLY(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_HOLY", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_DEATH(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_DEATH", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_PSIONIC(StringMaster.format(StringMaster.getLastPart(
            "RESIST_GRADE_PSIONIC", "_")), false, "units", "chars", "weapons", "armor"),
    // common,
    // rare,
    // special,
    // legendary
    // etc
    // unit in the middle of each GROUP


    ANIM_SPRITE_PRECAST
            ("", true, "spells", "actions"),
    ANIM_SPRITE_CAST
            ("", true, "spells", "actions"),
    ANIM_SPRITE_RESOLVE
            ("", true, "spells", "actions"),
    ANIM_SPRITE_MAIN
            ("", true, "spells", "actions"),
    ANIM_SPRITE_IMPACT
            ("", true, "spells", "actions"),
    ANIM_SPRITE_AFTEREFFECT
            ("", true, "spells", "actions"),
    ANIM_MISSILE_SPRITE
            ("", true, "spells", "actions"),
    ANIM_MODS_SPRITE("", true, "spells", "actions"),

    ANIM_MISSILE_VFX("", true, "spells", "actions"),
    ANIM_VFX_PRECAST("", true, "spells", "actions"),
    ANIM_VFX_CAST("", true, "spells", "actions"),
    ANIM_VFX_RESOLVE("", true, "spells", "actions"),
    ANIM_VFX_MAIN("", true, "spells", "actions"),
    ANIM_VFX_IMPACT("", true, "spells", "actions"),
    ANIM_VFX_AFTEREFFECT("", true, "spells", "actions"),


    ANIM_SOUND_MISSILE("", true, "spells", "actions"),
    ANIM_SOUND_PRECAST("", true, "spells", "actions"),
    ANIM_SOUND_CAST("", true, "spells", "actions"),
    ANIM_SOUND_RESOLVE("", true, "spells", "actions"),
    ANIM_SOUND_MAIN("", true, "spells", "actions"),
    ANIM_SOUND_IMPACT("", true, "spells", "actions"),
    ANIM_SOUND_AFTEREFFECT("", true, "spells", "actions"),

    ANIM_MODS_VFX("", true, "spells", "actions"),

    SPRITE_PATH("weapons", null), UNLOCKED_MASTERIES(null, true, "chars", "units"),
    MASTERY_RANKS_1(null, true, "chars"),
    MASTERY_RANKS_2(null, true, "chars"),
    MASTERY_RANKS_3(null, true, "chars"),
    MASTERY_RANKS_4(null, true, "chars"),
    MASTERY_RANKS_5(null, true, "chars"),

    PERKS(null, true, "deities", "chars"),
    CLASS_PERK_GROUP(null, false, "classes"),
    PERK_FOR_CLASSES(null, false, "perks"),
    PERK_PARAM(null, false, "perks"),
    KEY_TYPE(null, false, "bf obj"),
    ACTOR_TYPE(null, false, "actors"),
    ACTOR_UNIT_NAMES(null, true, "actors"),

    COATING_MOD(null, false, "chars", "units", "skills"),
    KEY_DOOR_PAIRS(null, false, "dungeons"),

    ITEM_TRAITS(null, true, "weapons", "armor", "jewelry"),

    OVERLAY_SPRITES(null, false, "units", "chars", "bf obj"),
    UNDERLAY_SPRITES(null, false, "units", "chars", "bf obj"),
    TEXTURES_OVERLAY(null, false, "units", "chars", "bf obj"),
    TEXTURES_UNDERLAY(null, false, "units", "chars", "bf obj"),
    VFX(null, false, "units", "chars", "bf obj"),
    CUSTOM_OBJECT(null, false, "units", "chars", "bf obj"),

    PLACEHOLDER_DATA(null, false, "units", "bf obj"),
    PLACEHOLDER_SYMBOL(null, false, "units", "bf obj"),
    LINKED_UNIT(null, false, "units", "bf obj"),

    //NF Rules

    SPELL_SPACES(null, true, "units", "chars"),
    COMBAT_SPACES(null, true, "units", "chars"),
    QUICK_ITEMS_SPACES(null, true, "units", "chars"),
    // DIVINED_SPACES(null, true, "units", "chars"),

    BASE_CLASS(null, false, "units", "chars"),

    FEAT_SPACES_COMBAT(null, true, "units", "chars"),
    FEAT_SPACES_ITEMS(null, true, "units", "chars"),
    FEAT_SPACES_SPELLS(null, true, "units", "chars"),

    ;

    boolean writeToType;
    INPUT_REQ inputReq;
    private final String name;
    private String descr;
    private String entityType;
    private boolean dynamic;
    private String defaultValue;
    private String[] entityTypes;
    private boolean container;
    private String fullName;
    private String shortName;
    private Map<OBJ_TYPE, Object> defaultValuesMap;
    private String iconPath;
    private boolean devOnly;

    PROPS(String shortName, boolean container, C_OBJ_TYPE entityType) {
        this(shortName, container, Arrays.stream(entityType.getTypes())
                .map(DC_TYPE::getName).toArray(String[]::new));
    }

    PROPS(String shortName, boolean container, String... entityTypes) {
        this(null, shortName, "", false,
                ContentValsManager.getDefaultEmptyValue(), 0);
        this.entityTypes = entityTypes;
        this.container = container;
        if (entityTypes.length > 0)
            entityType = entityTypes[0];
        else
            entityType = "meta";
    }

    PROPS(String entityType, String shortName) {
        this(entityType, shortName, "", false, ContentValsManager.getDefaultEmptyValue(), 0);
    }

    PROPS(String entityType, String shortName, String descr, boolean dynamic, String defaultValue,
          int AV_ID) {
        this.name = StringMaster.format(name());
        this.shortName = shortName;
        if (StringMaster.isEmpty(shortName)) {
            this.shortName = name;
        }
        // this.shortName = StringMaster.capitalizeFirstLetter(name()
        // .toLowerCase());
        this.fullName = name();
        this.descr = descr;
        this.entityType = entityType;
        this.dynamic = dynamic;
        this.defaultValue = defaultValue;
    }

    @Override
    public INPUT_REQ getInputReq() {
        return inputReq;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @return the shortName
     */
    public String getName() {
        return name;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the descr
     */
    public String getDescr() {
        return descr;
    }

    /**
     * @param descr the descr to set
     */
    public void setDescr(String descr) {
        this.descr = descr;
    }

    /**
     * @return the entityType
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * @param entityType the entityType to set
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * @return the dynamic
     */
    public boolean isDynamic() {
        return dynamic;
    }

    /**
     * @param dynamic the dynamic to set
     */
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @Override
    public String getDefaultValue() {
        return String.valueOf(defaultValue);
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getEntityTypes() {
        return entityTypes;
    }


    @Override
    public boolean isContainer() {
        // TODO Auto-generated method stub
        return container;
    }

    public void setContainer(boolean container) {
        this.container = container;
    }


    public boolean isPrinciple() {
        for (PRINCIPLES p : RpgEnums.PRINCIPLES.values()) {
            if (p.toString().equalsIgnoreCase(getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isWriteToType() {
        return writeToType;
    }

    public void setWriteToType(boolean writeToType) {
        this.writeToType = writeToType;
    }

    public synchronized String getShortName() {
        return shortName;
    }

    /**
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Map<OBJ_TYPE, Object> getDefaultValuesMap() {
        if (defaultValuesMap == null) {
            defaultValuesMap = new HashMap<>();
        }
        return defaultValuesMap;
    }

    @Override
    public void setDevOnly(boolean devOnly) {
        this.devOnly = devOnly;
    }

    @Override
    public boolean isDevOnly() {
        return devOnly;
    }

    @Override
    public String getIconPath() {
        return iconPath;
    }

    @Override
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
}
