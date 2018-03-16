package main.content.enums.macro;

import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.content.values.properties.PROPERTY;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum

MACRO_OBJ_TYPES implements OBJ_TYPE {
    WORLD("world", G_PROPS.GROUP, 1),
    REGION("region", MACRO_PROPS.AREA, 2),
    PLACE(("place"), MACRO_PROPS.PLACE_TYPE, 3, MACRO_PROPS.PLACE_SUBTYPE),
    SHOP("shop", MACRO_PROPS.SHOP_LEVEL, 4, MACRO_PROPS.SHOP_TYPE),
    TOWN("town", MACRO_PROPS.AREA, 5, MACRO_PROPS.REGION){
        public OBJ_TYPE getParent() {
            return PLACE;
        }
    },
    ROUTE("route", MACRO_PROPS.AREA, 6, MACRO_PROPS.REGION),
    // DIALOGUE(("dialogue"), G_PROPS.GROUP, 8, MACRO_PROPS.ACTOR, true),
     FACTIONS("factions", G_PROPS.FACTION_GROUP, 7),
    CAMPAIGN("campaign", G_PROPS.GROUP, 8),
    AREA("area", MACRO_PROPS.REGION, 9),
    TOWN_PLACE("town place", MACRO_PROPS.REGION, 10),

    MISSION("missions", G_PROPS.GROUP, 11, MACRO_PROPS.MISSION_TYPE),

    DIALOGUE("dialogue", G_PROPS.GROUP,12 ),
    TAVERN("tavern", G_PROPS.GROUP,13 ),;
//    MAP_ACTIONS("map actions", G_PROPS.GROUP,14 );
    // MACRO_CHAR(OBJ_TYPES.CHARS, "MACRO_CHAR", 7),
    // MACRO_UNIT(OBJ_TYPES.UNITS, "MACRO_UNIT", 8),

    // MACRO_ACTION(OBJ_TYPES.ACTIONS),

    // ACTOR default dialogues,
    // for
    // dungeons, text quests and events? what about ACTORs?

    // SETTLEMENT(("SETTLEMENT"), PROPS.GROUP, 7),
    // BUILDING(("BUILDING"), PROPS.GROUP, 8),

    // CIVILIZATION(("CIVILIZATION"), PROPS.GROUP, 3),
    // ARMY( ("ARMY"), PROPS.GROUP, 4),
    // ARMY_UNIT(("ARMY_UNIT"), PROPS.GROUP, 5),

    //

    //

    // COMPANION(
    // ("CIVILIZATION"),
    // PROPS.GROUP,
    // 3),
    // DUNGEON(
    // ("CIVILIZATION"),
    // PROPS.GROUP,
    // 3),
    // INFLUENCE("DIALOGUE_ACTOR", PROPS.ASPECT, 11, true),
    // DIALOGUE_ACTOR("DIALOGUE_ACTOR", PROPS.ASPECT, 11, true)

    private static Map<String, MACRO_OBJ_TYPES> searchMap;

    static {
        final MACRO_OBJ_TYPES[] values = values();
        searchMap = new HashMap<>(values.length, 1f);

        for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
            MACRO_OBJ_TYPES value = values[i];
            searchMap.put(value.getName(), value);
        }
    }

    private String name;
    private PROPERTY groupingKey;
    private PROPERTY subGroupingKey;
    private int code = -1;
    private String image;
    private boolean hidden;
    private boolean treeEditType;
    private DC_TYPE microType;

    MACRO_OBJ_TYPES(DC_TYPE TYPE, String name, Integer code) {
        this(name, TYPE.getGroupingKey(), code, TYPE.getSubGroupingKey());
        this.setMicroType(TYPE);
    }

    MACRO_OBJ_TYPES(String name, PROPERTY v, int code, boolean hidden) {
        this(name, v, code);
        this.setHidden(hidden);
    }

    MACRO_OBJ_TYPES(String name) {
        this.name = StringMaster.getObjTypeName(name);
        this.subGroupingKey = G_PROPS.GROUP;
    }

    MACRO_OBJ_TYPES(String name, PROPERTY v, int code, PROPERTY subgroup, boolean treeEditingType) {
        this(name, v, code, subgroup);
        this.setTreeEditType(treeEditingType);
    }

    MACRO_OBJ_TYPES(String name, PROPERTY v, int code, PROPERTY subgroup) {
        this(name, v, code);
        this.subGroupingKey = subgroup;
    }

    MACRO_OBJ_TYPES(String name, PROPERTY v, int code) {
        this(name, v);
        this.code = code;
        this.setImage("UI\\" + name + ".jpg");
    }

    MACRO_OBJ_TYPES(String name, PROPERTY v) {
        this(name);
        this.groupingKey = v;

    }

    private static MACRO_OBJ_TYPES getFromName(String name) {
        MACRO_OBJ_TYPES objTypes = searchMap.get(name);
        if (objTypes == null) {
            name = name.toLowerCase();
            objTypes = searchMap.get(name);
        }

        return objTypes;
    }

    public static OBJ_TYPE getTypeByCode(int code) {
        for (OBJ_TYPE type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    public static MACRO_OBJ_TYPES getType(String s) {
        MACRO_OBJ_TYPES type = null;

        type = getFromName(s);

        if (type == null) {
            type = new EnumMaster<MACRO_OBJ_TYPES>().retrieveEnumConst(MACRO_OBJ_TYPES.class, s);
        }
        // if (type == null) {
        // type = new EnumMaster<MACRO_OBJ_TYPES>().retrieveEnumConst(
        // MACRO_OBJ_TYPES.class, s, true);
        // }
        // if (type == null) {
        // main.system.auxiliary.LogMaster.log(1, s
        // + " is not a MACRO_OBJ_TYPE!");
        // }
        return type;

    }

    public static List<MACRO_OBJ_TYPES> getTypeGroups() {

        return Arrays.asList(values());
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PROPERTY getGroupingKey() {
        return groupingKey;
    }

    public void setGroupingKey(PROPERTY groupingKey) {
        this.groupingKey = groupingKey;
    }

    public PROPERTY getSubGroupingKey() {
        return subGroupingKey;
    }

    public void setSubGroupingKey(PROPERTY subGroupingKey) {
        this.subGroupingKey = subGroupingKey;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isTreeEditType() {
        return treeEditType;
    }

    public void setTreeEditType(boolean treeEditingType) {
        this.treeEditType = treeEditingType;
    }

    public DC_TYPE getMicroType() {
        return microType;
    }

    public void setMicroType(DC_TYPE microType) {
        this.microType = microType;
    }

    @Override
    public PARAMETER getParam() {
        return null;
    }

    @Override
    public PROPERTY getUpgradeRequirementProp() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isHeroTreeType() {
        return false;
    }

}
