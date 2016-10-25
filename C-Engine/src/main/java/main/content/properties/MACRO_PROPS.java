package main.content.properties;

import main.content.ContentManager;
import main.content.Metainfo;
import main.content.OBJ_TYPE;
import main.system.auxiliary.StringMaster;

public enum MACRO_PROPS implements PROPERTY {
    FACTIONS(null, false, "town", "world"),
    REGIONS("world", null),
    STARTING_LOCATION("campaign", "Starting Location"),
    CAMPAIGN_PARTY("campaign", "Campaign Party"),
    WORLD("campaign", "World"),
    WANDERING_GROUPS(true, null, true, "area"),
    ENCOUNTER_SUBGROUPS(null, true, "area"),
    AMBUSHING_GROUPS(true, null, true, "route", "place"),
    FACTION_RELATIONS(true, null, false, "town", "party"),
    DUNGEON_LEVELS(false, null, true, "place", "missions"),
    BOSS_LEVEL_POOL(false, null, true, "place"),
    ROOT_POOL(false, null, true, "place"),
    SUBLEVEL_GROUP_POOL(false, null, true, "place"),
    SUBLEVEL_POOL(false, null, true, "place"),
    PLACE(true, null, false, "party"),
    CURRENT_EXPLORATION(true, null, false, "party"),
    ROUTE(true, null, false, "party"),
    MACRO_STATUS(true, null, false, "party"),
    MACRO_MODE(true, null, false, "chars"),
    HEROES_FOR_HIRE(true, null, true, "town place"),
    TOWN_PLACE_TYPE("town place", null),
    TOWN_PLACE_MODIFIER("town place", null),
    HERO_BACKGROUNDS(null, true, "town place"),
    HERO_GROUPS(null, true, "town place"),
    TOWN_PLACES(null, true, "town", "place"),
    PARTIES(null, true, "town", "place", "route"),
    AREA(true, null, false, "region", "party", "route", "town", "place"),
    PLACE_VISIBILITY_STATUS(true, null, false, "route", "town", "place"),
    SHOP_ITEMS("shop", null), // true,

    SHOP_LEVEL("shop", null),
    SHOP_TYPE("shop", null),
    SHOP_ITEM_GROUPS("shop", null),
    SHOP_MODIFIER("shop", null),
    SHOPS(null, true, "town", "place"),
    DUNGEONS(null, true, "town", "place"),
    TOWN_HALL(null, false, "town"),
    TAVERNS(null, false, "town"),
    LINKED_PLACES(null, false, "route", "town", "place"),
    LINKED_ROUTES(null, false, "route", "town", "place"),
    LINKED_TOWNS(null, false, "route", "town", "place"),
    FACTION(null, false, "town", "party"),
    MAP_IMAGE("region", "Map Image"),
    // WORLD
    // REGIONS("world", "Regions"),
    // MAP_IMAGE("world", "Map Image"),
    EVENTS("world", "Events"),
    // REGIONS
    PLACES("region", "Places"),
    TOWNS("region", null),
    AREAS("region", null),
    REGION("Region", false, "place", "town", "route", "party", "campaign"),

    INTERNAL_ROUTES(null, true, "region"),
    EXTERNAL_ROUTES(null, true, "region"),
    AREA_BOUNDARIES(null, true, "area"),
    ROUTE_TYPE("route", null),
    DANGER_LEVEL("route", null),
    CONCEALMENT_LEVEL("route", null),
    PRESET_ENCOUNTER("route", null),
    RANDOM_ENCOUNTERS("route", null),

    TERRAIN_TYPE("route", null),
    ORIGIN("route", null),
    DESTINATION("route", null),

    MISSION_MAP(null, false, "missions"),
    MISSION_TYPE(null, false, "missions"),
    // bools - allow_level_up ; allow_map_transform
    MISSION_TAGS(null, true, "missions"),
    COMPANION_POOL(null, true, "missions"),
    COMPANION_FILTER_CONDITIONS(null, true, "missions"),
    SHOP_DATA(null, true, "missions"),
    MODES_ALLOWED(null, true, "missions"),

    HERO_CREATION_CHOICE_DATA(null, true, "missions"),
    HERO_SELECTION_FILTER_CONDITIONS(null, true, "missions"),

    OBJECTIVE_DATA(null, true, "missions"),
    OBJECTIVE_TYPES(null, true, "missions"),
    SUBOBJECTIVE_TYPES(null, true, "missions"),
    SUBOBJECTIVE_DATA(null, true, "missions"),

    ROOT_LEVEL(null, false, "missions"),
    BOSS_LEVEL(null, false, "missions"),
    LEVEL_PATH(null, false, "missions"),
    RANDOM_SUBLEVELS(null, true, "missions"),
    PRESET_SUBLEVELS(null, true, "missions"),
    MISSION_PARTY(null, false, "missions"),
    MISSION_CUSTOM_PARTY(null, true, "missions"),
    MUSIC_THEME("MUSIC_THEME", true, "region", "place"), // container
    ACTOR("dialogue", "Speaker"),
    DIALOGUE_TREE("dialogue", "DialogueTree"),
    DIALOGUES("Dialogues", true, "actor"),
    HEROES,
    // BACKGROUND_IMAGE,
    // // PARTY
    // ACTIVE_UNITS,
    // RESERVE_UNITS,
    // EVENT_NAME,
    // SETTLEMENTS

    // FACTIONS

    // ARMY

    ;

    private Metainfo metainfo;
    private String shortName;
    private String fullName;
    private String descr;
    private String entityType;
    private boolean dynamic;
    private String defaultValue;
    private int AV_ID;

    private boolean lowPriority = false;
    private String[] entityTypes;
    private boolean container;

    MACRO_PROPS() {

    }

    MACRO_PROPS(boolean dynamic, String shortName, boolean container, String... entityTypes) {
        this(shortName, container, entityTypes);
        this.dynamic = dynamic;
    }

    MACRO_PROPS(String shortName, boolean container, String... entityTypes) {
        this(entityTypes[0], shortName, "", false, ContentManager.getDefaultEmptyValue(), 0);
        this.entityTypes = entityTypes;
        this.container = container;
    }

    MACRO_PROPS(String entityType, String shortName) {
        this(entityType, shortName, "", false, ContentManager.getDefaultEmptyValue(), 0);
    }

    MACRO_PROPS(String entityType, String shortName, String descr, boolean dynamic,
                String defaultValue, int AV_ID) {
        this.shortName = shortName;
        this.fullName = StringMaster.getWellFormattedString(name());
        if (StringMaster.isEmpty(shortName))
            this.shortName = this.fullName;
        this.descr = descr;
        this.entityType = entityType;
        this.dynamic = dynamic;
        this.defaultValue = defaultValue;
        this.AV_ID = AV_ID;
    }

    @Override
    public String getName() {
        if (shortName == null)
            shortName = StringMaster.getWellFormattedString(name());
        return shortName;
    }

    /**
     * @return the entityTypes
     */

    public String[] getEntityTypes() {
        return entityTypes;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getDescription() {
        return descr;
    }

    @Override
    public String getEntityType() {

        return entityType;
    }

    @Override
    public String getDefaultValue() {
        return String.valueOf(defaultValue);
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    @Override
    public boolean isContainer() {
        return container;
    }

    public Metainfo getMetainfo() {
        return metainfo;
    }

    public void setMetainfo(Metainfo metainfo) {
        this.metainfo = metainfo;
    }

    @Override
    public boolean isSuperLowPriority() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setSuperLowPriority(boolean lowPriority) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public boolean isHighPriority() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setHighPriority(boolean highPriority) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isWriteToType() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setWriteToType(boolean writeToType) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addSpecialDefault(OBJ_TYPE type, Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object getSpecialDefault(OBJ_TYPE type) {
        // TODO Auto-generated method stub
        return null;
    }

    public INPUT_REQ getInputReq() {
        return null;
    }
}
