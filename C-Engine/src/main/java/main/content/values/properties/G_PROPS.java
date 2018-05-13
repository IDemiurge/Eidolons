package main.content.values.properties;

import main.content.Metainfo;
import main.content.OBJ_TYPE;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.Map;

public enum G_PROPS implements PROPERTY {
    LAST_EDITOR("meta", null),
    VERSION("all", null),
    WORKSPACE_GROUP("all", null),
    DEV_NOTES("all", null),
    GAME_VERSION("meta", null),
    NAME("all", "Name"),
    DISPLAYED_NAME("all", "Name"),
    ID("all", "Id"),
    IMAGE("all", "Image"),
    TYPE("all", "Type"),
    PARENT_TYPE("hidden", null),
    CLASSIFICATIONS("", true, "units", "chars", "bf obj", "terrain"),
    // CLASSIFICATION("all", "Classifications"),

    HOTKEY("actions", "Hotkey"),
    GROUP("all", "Group"),
    UNIT_GROUP("UNIT_GROUP", false, "units"),
    GENDER("Gender", false, "chars"),
    ASPECT("Aspect", false, "chars", "units", "spells", "deities"),
    VARIABLES("all", "Variables"),
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
    STATUS("Status", true, "units", "chars", "bf", "spells", "actions", "weapons", "armor", "terrain"),
    MODE("Mode", false, "units", "chars"),
    ACTIVES("Actives", true, "units", "chars", "items", "actions", "classes", "spells", "weapons", "bf obj", "deities", "skills"),
    CUSTOM_SOUNDSET("Custom Soundset", false, "units", "chars", "actions", "spells", "bf obj", "items"),

    PASSIVES("all", "Passives"),

    DEITY("Deity", "units", "chars"),
    FLAVOR("Flavor", false, "weapons", "armor", "items", "jewelry", "classes", "skills", "spells"),
    DESCRIPTION("all", "Description"),
    LORE("all", "Lore"),
    // "chars"
    RANK("Rank", "chars", "classes", "encounters"),
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

    SKILL_GROUP("skills", "Skill Type"),
    EMBLEM("EMBLEM", false, "chars", "deities", "factions"),
    STD_BOOLS(null, true, "all"),
    DYNAMIC_BOOLS(null, true, "all"),

    STANDARD_PASSIVES("Standard Passives", true, "units", "chars", "classes", "bf obj", "skills", "weapons", "armor"),
    IMMUNITIES(null, true, "units", "chars", "classes", "bf obj", "skills", "weapons", "armor"),
    CLASS_TYPE("classes"),
    CLASS_GROUP("classes"),
    // UPGRADE
    BASE_TYPE("", false, "spells", "abils", "chars", "units", "skills", "classes", "weapons", "armor", "items", "actions"),

    CUSTOM_PROPS("all"),
    CUSTOM_PARAMS("all"),
    ABILITY_GROUP("abils"),
    ENCOUNTER_GROUP("encounters"),
    ENCOUNTER_SUBGROUP("encounters"),
    ENCOUNTER_TYPE("encounters"),

    DUNGEON_GROUP("dungeons"),
    DUNGEON_SUBFOLDER("dungeons"),
    DUNGEON_TYPE("dungeons"),
    DUNGEON_LEVEL("dungeons"),
    ARCADE_REGION("", false, "party", "dungeons"),
    ARCADE_ROUTE("dungeons"),

    DUNGEONS_COMPLETED("party"),
    DUNGEONS_PENDING("party"),
    LAST_DUNGEON("party"),
    DIFFICULTY("party"),

    EMPTY_VALUE(""),
    JEWELRY_GROUP("jewelry"),
    JEWELRY_TYPE("jewelry"),
    GARMENT_TYPE("garment"),
    TOOLTIP("all"),
    FACTION_GROUP("factions"),
    UNIQUE_ID("all"), FULLSIZE_IMAGE("scenarios"), PERK_PARAMS("Perk Params",true,"perks"), PERK_GROUP("perks");

    static {
        NAME.setInputReq(INPUT_REQ.STRING);
        // MAIN_HAND_ITEM.setDynamic(true);
        // OFF_HAND_ITEM.setDynamic(true);
        // ARMOR_ITEM.setDynamic(true);
        UNIQUE_ID.setDynamic(true);
        STATUS.setDynamic(true);
        // STATUS.setDynamic(true);
        SPELL_POOL.setDynamic(true);
        DYNAMIC_BOOLS.setDynamic(true);
        NAME.setHighPriority(true);
        IMAGE.setHighPriority(true);
        // ASPECT.setHighPriority(true);
        // DEITY.setHighPriority(true);
        MAIN_HAND_ITEM.setLowPriority(true);
        OFF_HAND_ITEM.setLowPriority(true);
        ARMOR_ITEM.setLowPriority(true);

        // CLASSIFICATION.setLowPriority(true);
        SOUNDSET.setLowPriority(true);
        CUSTOM_SOUNDSET.setLowPriority(true);
        LORE.setLowPriority(true);
        DESCRIPTION.setLowPriority(true);
        VARIABLES.setLowPriority(true);
        VARIABLES.setContainer(true);

        KEYS.setLowPriority(true);
        STD_BOOLS.setLowPriority(true);
        BASE_TYPE.setLowPriority(true);
        CUSTOM_PROPS.setLowPriority(true);
        CUSTOM_PARAMS.setLowPriority(true);
        TARGETING_MODE.setDefaultValue("SINGLE");
        // ITEMS.setLowPriority(true);
    }

    boolean writeToType;
    INPUT_REQ inputReq;
    private String name;
    private String entityType;
    private String defaultValue;
    private boolean lowPriority = false;
    private boolean container;
    private String[] entityTypes;
    private boolean dynamic;
    private boolean superLowPriority;
    private boolean highPriority;
    private String shortName;
    private String fullName;
    private Map<OBJ_TYPE, Object> defaultValuesMap;

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
            this.shortName = StringMaster.getWellFormattedString(name());
        }
        this.name = StringMaster.getWellFormattedString(name());
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

    public Map<OBJ_TYPE, Object> getDefaultValuesMap() {
        if (defaultValuesMap == null) {
            defaultValuesMap = new HashMap<>();
        }
        return defaultValuesMap;
    }

    @Override
    public void addSpecialDefault(OBJ_TYPE type, Object value) {
        getDefaultValuesMap().put(type, value);

    }

    @Override
    public Object getSpecialDefault(OBJ_TYPE type) {
        return getDefaultValuesMap().get(type);

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
    public Metainfo getMetainfo() {
        return null;
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

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @Override
    public boolean isSuperLowPriority() {
        return superLowPriority;
    }

    @Override
    public void setSuperLowPriority(boolean superLowPriority) {
        this.superLowPriority = superLowPriority;
    }

    @Override
    public boolean isHighPriority() {
        return highPriority;
    }

    @Override
    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
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
}
