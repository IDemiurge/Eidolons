package main.content.values.properties;

import main.content.ContentManager;
import main.content.Metainfo;
import main.content.OBJ_TYPE;
import main.system.auxiliary.StringMaster;

public enum MACRO_PROPS implements PROPERTY {
    FACTIONS(null, false, "town", "world"),
    PLAYER_FACTION(null, false, "world"),
    REGIONS("world", null),
    STARTING_LOCATION("campaign", "Starting Location"),
    CAMPAIGN_PARTY("campaign", "Campaign Party"),
    WORLD("campaign", "World"),
    WANDERING_GROUPS(true, null, true, "area"),
    ENCOUNTER_SUBGROUPS(null, true, "area"),
    AMBUSHING_GROUPS(true, null, true, "route", "place"),
    FACTION_RELATIONS(true, null, false, "town", "party"),

    DUNGEON_TYPES(false, null, true, "place"),

    PLACE_TYPE(false, null, false, "place"),
    PLACE_SUBTYPE(false, null, false, "place"),

    DUNGEON_LEVELS(false, null, true, "place", "mission"),
    DUNGEONS(null, true, "town", "place"), //explicit by in-game name for generation

    MAP_ICON(false, null, false, "town", "place"),

    //advanced
    BOSS_LEVEL_POOL(false, null, true, "placeX"),
    ROOT_POOL(false, null, true, "placeX"),
    SUBLEVEL_GROUP_POOL(false, null, true, "placeX"),
    SUBLEVEL_POOL(false, null, true, "placeX"),

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
    PARTIES(null, true, "region", "town", "place", "route"),
    AREA(true, null, false, "region", "party", "route", "town", "place"),
    PLACE_VISIBILITY_STATUS(true, null, false, "route", "town", "place"),
    SHOP_ITEMS("shop", null), // true,

    SHOP_LEVEL("shop", null),
    SHOP_TYPE("shop", null),
    SHOP_ITEM_GROUPS("shop", null),
    SHOP_MODIFIER("shop", null),
    SHOPS(null, true, "town", "place"),
    TOWN_HALL(null, false, "town"),
    TAVERNS(null, false, "town"),
    LINKED_PLACES(null, false, "route", "town", "place"),
    LINKED_ROUTES(null, false, "route", "town", "place"),
    LINKED_TOWNS(null, false, "route", "town", "place"),
    FACTION(null, false, "town", "party"),
    MAP_IMAGE(null, false, "route", "region"),
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

    MISSION_MAP(null, false, "mission"),
    MISSION_TYPE(null, false, "mission"),
    // bools - allow_level_up ; allow_map_transform
    MISSION_TAGS(null, true, "mission"),
    COMPANION_POOL(null, true, "mission"),
    COMPANION_FILTER_CONDITIONS(null, true, "mission"),
    SHOP_DATA(null, true, "mission"),
    MODES_ALLOWED(null, true, "mission"),

    HERO_CREATION_CHOICE_DATA(null, true, "mission"),
    HERO_SELECTION_FILTER_CONDITIONS(null, true, "mission"),

    OBJECTIVE_DATA(null, true, "mission"),
    OBJECTIVE_TYPES(null, true, "mission"),
    SUBOBJECTIVE_TYPES(null, true, "mission"),
    SUBOBJECTIVE_DATA(null, true, "mission"),

    ROOT_LEVEL(null, false, "mission"),
    BOSS_LEVEL(null, false, "mission"),
    LEVEL_PATH(null, false, "mission"),
    RANDOM_SUBLEVELS(null, true, "mission"),
    PRESET_SUBLEVELS(null, true, "mission"),
    MISSION_PARTY(null, false, "mission"),
    MISSION_CUSTOM_PARTY(null, true, "mission"),
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
        if (StringMaster.isEmpty(shortName)) {
            this.shortName = this.fullName;
        }
        this.descr = descr;
        this.entityType = entityType;
        this.dynamic = dynamic;
        this.defaultValue = defaultValue;
        this.AV_ID = AV_ID;
    }

    @Override
    public String getName() {
        if (shortName == null) {
            shortName = StringMaster.getWellFormattedString(name());
        }
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
