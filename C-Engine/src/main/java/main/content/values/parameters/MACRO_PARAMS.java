package main.content.values.parameters;

import main.content.Metainfo;
import main.content.OBJ_TYPE;
import main.system.auxiliary.StringMaster;

import java.awt.*;

public enum MACRO_PARAMS implements PARAMETER {
    PREFERRED_HERO_NUMBER(null, "", false, 4, "town place"),
    TAVERN_XP_PER_HOUR(null, "", false, 10, "town place"),
    HERO_POWER_POOL(null, "", false, 0, "town place"),
    C_HERO_POWER_POOL(null, "", true, 0, "town place"),
    C_FOOD_STORE(null, "", true, 25, "town place"),
    FOOD_STORE(null, "", false, 25, "town place"),
    FOOD_COST(null, "", false, 5, "town place"),
    RENT_DURATION(null, "", true, 25, "town place"),
    // AREA
    DANGER_MOD(null, "", false, 0, "area"),
    MEDITATION_MOD(null, "", false, 0, "area"),
    REST_MOD(null, "", false, 0, "area"),
    FORAGE_MOD(null, "", false, 0, "area"),
    AREA_CREEP_POWER_TOTAL(null, "", true, 0, "area"),
    ROOM_QUALITY_MOD(null, "", true, 100, "town place"),

    GOLD_SHARE("chars", null),
    C_GOLD_SHARE(null, "", true, 0, "chars"),
    SHARED_GOLD_PERCENTAGE("chars", null),
    C_SHARED_GOLD_PERCENTAGE(null, "", true, 0, "chars"),
    C_SHARED_GOLD(null, "", true, 0, "chars"),
    HIRE_COST(null, "", false, 0, "chars"),
    SHOP_INCOME(null, "", false, 15, "shop"),
    SHOP_INCOME_GROWTH(null, "", false, 1, "shop"),

    BALANCE(null, "", false, 0, "shop"),
    MIN_BALANCE(null, "", false, -300, "shop"),
    MAX_DEBT(null, "", false, 100, "shop"),
    DEBT_INTEREST(null, "", false, 120, "shop"),

    ROUTE_PROGRESS(null, "", true, 0, "party"),
    ROUTE_PROGRESS_PERCENTAGE(null, "", false, 0, "place", "route", "town"),
    MAP_POS_X(null, "", false, 0, "place", "route", "town", "party", "region"),
    MAP_POS_Y(null, "", false, 0, "place", "route", "town", "party", "region"),
    COMBAT_READINESS("Combat Readiness", "", true, 50, "chars"),
    ONENESS("Oneness", "", true, 50, "chars"),
    VIGOR("Vigor", "", true, 100, "chars"),
    HEALTH("Health", "", true, 100, "chars"),
    MOTIVATION(null, "", true, 50, "chars"),
    HUNGER(null, "", true, 0, "chars"),
    CREEP_AMBUSH_POINT(null, "", true, 0, "route"),
    FATIGUE("Fatigue", "", true, 0, "party", "chars"), // why common?
    // ORGANIZATION("party", "ORGANIZATION"),
    TRAVEL_SPEED("TRAVEL_SPEED", "", false, 4, "party", "chars", "macro units"),
    EXPLORE_SPEED("EXPLORE_SPEED", "", false, 2, "party", "chars", "macro units"),
    PROVISIONS("PROVISIONS", "", false, 0, "party"),
    WATER(null, "", false, 0, "party"),
    SOULGEMS(null, "", false, 0, "party"),
    C_WATER(null, "", true, 0, "party"),
    C_SOULGEMS(null, "", true, 0, "party"),
    C_PROVISIONS("PROVISIONS", "", true, 0, "party"),
    CONSUMPTION("CONSUMPTION", "", false, 1, "party", "chars", "macro units"),
    ROUTE_LENGTH("ROUTE_LENGTH", "", false, 4, "route"),
    ROUTE_LENGTH_REMAINING("ROUTE_LENGTH_REMAINING", "", true, 0, "route"),
    SPEED_MOD(null, "", false, 100, "route", "area"),
    EXPLORE_SPEED_MOD(null, "", false, 100, "route", "area"),
    BEND_FACTOR(null, "", false, 25, "route"),
    ERA(null, "", false, 3, "campaign", "world"),
    YEAR(null, "", false, 1500, "campaign", "world"),
    MONTH(null, "", false, 1, "campaign"),
    DAY(null, "", false, 1, "campaign"),
    DAY_OR_NIGHT(null, "", false, 1, "campaign"),
    MILE_TO_PIXEL(null, "", false, 1, "region"),
    HOURS_ELAPSED(null, "", true, 0, "campaign"),
    CREEP_POWER_BASE(null, "", false, 100, "campaign"),
    CREEP_POWER_PER_HOUR(null, "", false, 2, "campaign"),
    CREEP_POWER_MAX_FACTOR(null, "", false, 2, "campaign"),
    HOURS_INTO_TURN(null, "", false, 0, "campaign"),

    TOWN_STASH_SIZE(null, "", false, 40, "town")

    // // SETTLEMENTS

    , REPUTATION(null, "", false,  0, "town", "shop"),


    // POWER,


    TREPIDATION("npc", null),
    ESTEEM("npc", null),
    AFFECTION("npc", null),
    QUEST_AMOUNT("quest", null );
    // NUMBER_OF_HEROES,
    // NUMBER_0F_unitsS,
    //
    //
    // POPULATION,
    //
    // // FACTIONS
    //
    // // ARMY
    // MORALE,
    // STRENGTH,
    //
    // // ROAD
    // LENGTH,
    // // PROVINCE
    //
    // // Influence => create Obj when in dialogue
    // AFFECTION, // ++ companion obj? contributes to Party,
    // ESTEEM,
    // TREPIDATION, // AWE?
    // HATRED

    private String shortName;
    private String fullName;
    private String descr;
    private String entityType;
    private String[] entityTypes;
    private String[] sentityTypes;
    private int AV_ID;
    private int defaultValue;
    private boolean dynamic = false;
    private Metainfo metainfo;
    private boolean lowPriority = false;
    private boolean attr = false;

    MACRO_PARAMS() {

    }

    MACRO_PARAMS(String fullName, String descr, boolean dynamic, int defaultValue,
                 String... entityTypes) {

        this(entityTypes[0], fullName, descr, dynamic, defaultValue, Integer.MAX_VALUE);
        this.entityTypes = entityTypes;
    }

    MACRO_PARAMS(String entityType, String fullName, String descr, boolean dynamic,
                 int defaultValue, Color c) {
        this(entityType, fullName, descr, dynamic, defaultValue, Integer.MAX_VALUE);
        this.metainfo = new Metainfo(c);
    }

    MACRO_PARAMS(String entityType, String fullName) {
        this(entityType, fullName, fullName, false, 0);
    }

    MACRO_PARAMS(String entityType, String fullName, String descr, boolean dynamic, int defaultValue) {
        this(entityType, fullName, descr, dynamic, defaultValue, Integer.MAX_VALUE);
    }

    MACRO_PARAMS(String entityType, String fullName, String descr, boolean dynamic,
                 int defaultValue, int AV_ID) {
        this.shortName = StringMaster.getWellFormattedString(name());
        if (fullName == null) {
            fullName = shortName;
        }
        this.fullName = fullName;
        this.descr = descr;
        this.entityType = entityType;
        this.dynamic = dynamic;
        this.defaultValue = defaultValue;
        this.AV_ID = AV_ID;
    }

    @Override
    public String getName() {
        return shortName;
    }

    /**
     * @return the entityTypes
     */

    public String[] getEntityTypes() {
        return entityTypes;
    }

    @Override
    public Metainfo getMetainfo() {
        return metainfo;
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
    public boolean isAttribute() {
        return isAttr();
    }

    public boolean isAttr() {
        return attr;
    }

    public void setAttr(boolean attr) {
        this.attr = attr;
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
        // TODO Auto-generated method stub
        return null;
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
    public boolean isMastery() {
        // TODO Auto-generated method stub
        return false;
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

    @Override
    public boolean isMod() {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public INPUT_REQ getInputReq() {
        return null;
    }

}
