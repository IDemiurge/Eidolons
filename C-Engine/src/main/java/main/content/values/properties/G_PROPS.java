package main.content.values.properties;

import main.system.auxiliary.StringMaster;

public enum G_PROPS implements PROPERTY {
    VERSION("all", null),
    WORKSPACE_GROUP("all", null),
    DEV_NOTES("all", null),
    TIMESTAMP("all", null),


    NAME("all", "Name"),
    DISPLAYED_NAME("all", "Name"),
    ID("all", "Id"),
    IMAGE("all", "Image"),
    TYPE("all", "Type"),
    PARENT_TYPE("hidden", null),
    CLASSIFICATIONS("", true, "units", "chars", "bf obj", "terrain"),

    VARIABLES("all", "Variables"), PASSIVES("all", "Passives"),
    DESCRIPTION("all", "Description"),
    LORE("all", "Lore"),
    STD_BOOLS(null, true, "all"),
    DYNAMIC_BOOLS(null, true, "all"),

    HOTKEY("actions", "Hotkey"),
    GROUP("all", "Group"),
    UNIT_GROUP("UNIT_GROUP", false, "units"),

    BOSS_TYPE("UNIT_GROUP", false, "boss"),
    BOSS_GROUP("UNIT_GROUP", false, "boss"),

    GENDER("Gender", false, "chars"),
    ASPECT("Aspect", false, "chars", "units", "spells", "deities"),
    VARIABLE_TYPES("abils", "Variables"),
    KEYS("keys", false, "skills"), // target
    BUFF_TYPE("buffs", "Buff_type"),
    ABILITY_TYPE("abils", "Ability_Type"),

    ACTION_TYPE("actions", "Action_Type"),
    ACTION_TYPE_GROUP("actions", null),
    ACTION_TAGS("Action tags", true, "actions"),
    ABILITIES("abils", "Abilities"),
    SOUNDSET("Soundset", "units", "chars", "spells", "items"),
    IMPACT_SPRITE("Impact Sprite", "actions", "spells", "items"),

    PRINCIPLES("Principles", true, "units", "chars", "deities", "classes"),
    BF_OBJECT_TYPE("bf obj", null),
    BF_OBJECT_GROUP("bf obj", null),
    BF_OBJECT_CLASS("bf obj", null),
    BF_OBJECT_TAGS(null, true, "bf obj"),

    SPELL_GROUP("spells", "Spell group"),
    SPELL_SUBGROUP("spells", "Spell group"),
    SPELL_UPGRADE_GROUPS(null, true, "spells", "chars", "units"),
    SPELL_TYPE("spells", "Spell type"),
    SPELL_TAGS("Spell tags", true, "spells"),
    SPELL_POOL("spells", "Spell pool"),

    MASTERY("Mastery", "armor", "weapons", "skills"),
    STATUS("Status", true, "units", "chars", "buffs", "spells", "actions", "weapons", "armor", "terrain"),
    MODE("Mode", false, "units", "chars"),
    ACTIVES("Actives", true, "units", "chars", "items", "actions",
            "classes", "spells", "weapons", "bf obj", "deities", "skills"),
    CUSTOM_SOUNDSET("Custom Soundset", false, "units", "chars", "actions", "spells", "bf obj", "items"),



    DEITY("Deity", "units", "chars"),
    FLAVOR("Flavor", false, "scenarios", "weapons", "armor", "items", "jewelry", "classes", "skills", "spells"),

    // "chars"
    RANK("Rank", "chars", "classes"),
    RACE("Race", "chars", "units"),

    BACKGROUND("chars", "Background"),
    BACKGROUND_TYPE("chars", "Background Type"),
    CUSTOM_HERO_GROUP("chars", "custom hero group"),
    // ITEMS("chars", "ITEMS"),

    TARGETING_MODE("Targeting mode", "spells", "actions", "items"),
    SPECIAL_REQUIREMENTS("Spec req", "spells", "actions"),

    ARMOR_TYPE("armor"),
    ARMOR_GROUP("armor"),

    ITEM_TYPE("items"),
    ITEM_GROUP("items"),

    WEAPON_TYPE("weapons"),
    WEAPON_GROUP("weapons"),
    WEAPON_CLASS("weapons"),
    WEAPON_SIZE("weapons"),
    ITEM_MATERIAL_GROUP(null, false, "weapons", "armor", "jewelry"),
    MATERIAL("Material", false, "weapons", "armor", "jewelry"),
    ENCHANTMENT_SPELL(null, false, "weapons", "armor", "jewelry"),

    QUALITY_LEVEL("Quality", false, "weapons", "armor", "items"),

    MAIN_HAND_ITEM(null, false, "units", "chars"),
    OFF_HAND_ITEM(null, false, "units", "chars"),
    ARMOR_ITEM(null, false, "units", "chars"),
    RESERVE_MAIN_HAND_ITEM(null, false, "units", "chars"),
    RESERVE_OFF_HAND_ITEM(null, false, "units", "chars"),

    SKILL_GROUP("skills", "Skill Type"),
    EMBLEM("EMBLEM", false, "chars", "deities", "factions"),

    STANDARD_PASSIVES("Passives", true,"buffs", "units", "chars", "classes", "bf obj", "skills", "weapons", "armor"),
    IMMUNITIES(null, true, "units", "chars", "classes", "bf obj", "skills", "weapons", "armor"),
    CLASS_TYPE("classes"),
    CLASS_GROUP("classes"),
    // UPGRADE
    BASE_TYPE("", false, "spells", "abils", "chars", "units", "skills", "classes", "weapons", "armor", "items", "actions"),

    CUSTOM_PROPS(null ,true, "bf obj", "units", "chars", "spells", "abils", "actions"),
    ABILITY_GROUP("abils"),
    ENCOUNTER_GROUP("", false, "encounters", "units"),
    ENCOUNTER_SUBGROUP("", false, "encounters", "units"),
    ENCOUNTER_TYPE("encounters"),

    DUNGEON_GROUP("dungeons"),
    DUNGEON_SUBFOLDER("dungeons"),

    EMPTY_VALUE(""), //visual separator in AV
    JEWELRY_GROUP("jewelry"),
    JEWELRY_TYPE("jewelry"),
    GARMENT_TYPE("garment"),
    TOOLTIP("all"),
    FACTION_GROUP("factions"),
    UNIQUE_ID("all"), FULLSIZE_IMAGE("scenarios"),
    PERK_PARAMS("Perk Params", true, "perks"), PERK_GROUP("perks"),
    PERK_CLASS_REQUIREMENTS("perks");


    boolean writeToType;
    INPUT_REQ inputReq;
    private final String name;
    private String entityType;
    private String defaultValue;
    private boolean container;
    private String[] entityTypes;
    private boolean dynamic;
    private String shortName;
    private final String fullName;
    private String iconPath;
    private boolean devOnly;

    G_PROPS(String shortName, boolean container, String... entityTypes) {
        this(entityTypes[0], shortName);
        this.setContainer(container);
        this.entityTypes = entityTypes;
    }

    G_PROPS(String shortName, String entityType1, String entityType2, String entityType3,
            String entityType4) {
        this(entityType1, shortName);
        this.entityTypes = new String[4];
        this.entityTypes[0] = entityType1;
        this.entityTypes[1] = entityType2;
        this.entityTypes[2] = entityType3;
        this.entityTypes[3] = entityType4;
    }

    G_PROPS(String shortName, String entityType1, String entityType2, String entityType3) {
        this(entityType1, shortName);
        this.entityTypes = new String[3];
        this.entityTypes[0] = entityType1;
        this.entityTypes[1] = entityType2;
        this.entityTypes[2] = entityType3;
    }

    G_PROPS(String shortName, String entityType1, String entityType2) {
        this(entityType1, shortName);
        this.entityTypes = new String[2];
        this.entityTypes[0] = entityType1;
        this.entityTypes[1] = entityType2;
    }

    G_PROPS(String entityType) {
        this(entityType, null);
    }

    G_PROPS(String entityType, String shortName) {
        this.entityType = entityType;
        this.shortName = shortName;
        this.fullName = name();
        if (StringMaster.isEmpty(shortName)) {
            this.shortName = StringMaster.format(name());
        }
        this.name = StringMaster.format(name());
        this.container = false;
        defaultValue = "";
    }

    @Override
    public INPUT_REQ getInputReq() {
        return inputReq;
    }

    public void setInputReq(INPUT_REQ inputReq) {
        this.inputReq = inputReq;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getDefaultValue() {
        return String.valueOf(defaultValue);
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean isContainer() {
        return container;
    }

    public void setContainer(boolean container) {
        this.container = container;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getDescription() {
        return fullName;
    }

    @Override
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Override
    public String[] getEntityTypes() {
        return entityTypes;
    }

    public void setEntityTypes(String[] entityTypes) {
        this.entityTypes = entityTypes;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean isWriteToType() {
        return writeToType;
    }

    public void setWriteToType(boolean writeToType) {
        this.writeToType = writeToType;
    }

    @Override
    public String getIconPath() {
        return iconPath;
    }

    @Override
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    @Override
    public void setDevOnly(boolean devOnly) {
        this.devOnly = devOnly;
    }

    @Override
    public boolean isDevOnly() {
        return devOnly;
    }
}
