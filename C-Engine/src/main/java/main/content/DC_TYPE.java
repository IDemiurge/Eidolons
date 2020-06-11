package main.content;

import main.content.enums.GenericEnums;
import main.content.enums.entity.AbilityEnums;
import main.content.enums.entity.HeroEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//it's actually ENTITY_TYPE

public enum DC_TYPE implements OBJ_TYPE {
    BOSS("boss", G_PROPS.BOSS_GROUP, 21, G_PROPS.BOSS_TYPE),
    UNITS("units", G_PROPS.ASPECT, 0, G_PROPS.UNIT_GROUP),
    SPELLS("spells", G_PROPS.ASPECT, 1, G_PROPS.SPELL_GROUP),
    CHARS("chars",
//            G_PROPS.GROUP,
            G_PROPS.RACE,
            2, G_PROPS.GROUP ),

    ABILS("abils", G_PROPS.ABILITY_TYPE, 3, G_PROPS.ABILITY_GROUP) {
        public boolean isTreeEditType() {
            return true;
        }
    },

    BF_OBJ("bf obj", G_PROPS.BF_OBJECT_TYPE, 4, G_PROPS.BF_OBJECT_GROUP),
    BUFFS("buffs", G_PROPS.BUFF_TYPE, 5),
    ACTIONS("actions", G_PROPS.ACTION_TYPE, 6),
    ARMOR("armor", G_PROPS.ARMOR_TYPE, 7, G_PROPS.ARMOR_GROUP),
    WEAPONS("weapons", G_PROPS.WEAPON_TYPE, 8, G_PROPS.WEAPON_GROUP),
    SKILLS("skills", G_PROPS.SKILL_GROUP, 9, G_PROPS.MASTERY) {
        public boolean isHeroTreeType() {
            return true;
        }
    },
    // ONLY IN PROPER ORDER!!!!!!!!!!!!
    ITEMS("items", G_PROPS.ITEM_TYPE, 10, G_PROPS.ITEM_GROUP),

    GARMENT("garment", G_PROPS.GROUP, 11, G_PROPS.GARMENT_TYPE),
    JEWELRY("jewelry", G_PROPS.GROUP, 12, G_PROPS.JEWELRY_GROUP),
    CLASSES("classes", G_PROPS.CLASS_TYPE, 13, G_PROPS.CLASS_GROUP) {
        public boolean isHeroTreeType() {
            return true;
        }
    },
    PERKS("perks", G_PROPS.GROUP, 14, G_PROPS.PERK_GROUP){
        @Override
        public OBJ_TYPE getParent() {
            return SKILLS;
        }
    },

    DEITIES("deities", G_PROPS.GROUP, 14, G_PROPS.ASPECT),
//    ENCOUNTERS("encounters", G_PROPS.ENCOUNTER_GROUP, 15, G_PROPS.ENCOUNTER_SUBGROUP),
    ENCOUNTERS("encounters", G_PROPS.ENCOUNTER_GROUP, 15, G_PROPS.ENCOUNTER_TYPE),
    FLOORS("dungeons", G_PROPS.GROUP, 16),
    PARTY("party", G_PROPS.GROUP, 17),
    TERRAIN("terrain", G_PROPS.GROUP, 18, false),

    SCENARIOS("scenarios", G_PROPS.GROUP, 21),
    PLACES("places", G_PROPS.GROUP, 23),
    DIALOGUE("dialogue", G_PROPS.GROUP, 24) {
        public boolean isTreeEditType() {
            return true;
        }
    },
    ACTORS("actors", G_PROPS.GROUP, 25), LORD("lord", G_PROPS.GROUP, 26 ),
//    this is macro!!! SHOPS("shops", G_PROPS.GROUP, 26),

    //    CHARACTER("character", G_PROPS.GROUP, 32),
//    MERCHANT(DC_TYPE.CHARACTER, "merchant", G_PROPS.GROUP, 27),
//    INNKEEPER(DC_TYPE.CHARACTER, "innkeeper", G_PROPS.GROUP, 28),
//    LIBRARIAN(DC_TYPE.CHARACTER, "librarian", G_PROPS.GROUP, 29),
//    MENTOR(DC_TYPE.CHARACTER, "mentor", G_PROPS.GROUP, 30),
//    MERCENARY(DC_TYPE.CHARACTER, "mercenary", G_PROPS.GROUP, 31),
    //    TRAP("traps", G_PROPS.GROUP, 24),
    META("meta", G_PROPS.GROUP, -1, true),
    ALL("all");

    private static final Map<String, DC_TYPE> searchMap;

    static {
TERRAIN.omitted=true;
ACTORS.omitted=true;
JEWELRY.omitted=true;
FLOORS.omitted=true;
DIALOGUE.omitted=true;
PLACES.omitted=true;
SCENARIOS.omitted=true;

        GARMENT.hidden = true;
        GARMENT.battlecraft = false;
//        TRAP.battlecraft = false;
        ENCOUNTERS.battlecraft = false;

        final DC_TYPE[] values = DC_TYPE.values();
        searchMap = new HashMap<>(values.length, 1f);
        for (int i = 0; i < values.length; i++) {
            final DC_TYPE value = values[i];
            searchMap.put(value.getName(), value);
        }
    }

    boolean battlecraft = true;
    private DC_TYPE parent;
    private String name;
    private PROPERTY groupingKey;
    private PROPERTY subGroupingKey;
    private PROPERTY upgradeRequirementProp;
    private int code = -1;
    private String image;
    private boolean hidden;
    private PARAMETER param;
    public boolean omitted;

    DC_TYPE(String name, PROPERTY groupingKey, int code, boolean hidden) {
        this(name, groupingKey, code);
        this.setHidden(hidden);
    }

    DC_TYPE(String name) {
        this.name = name;
        this.subGroupingKey = G_PROPS.GROUP;
    }

    DC_TYPE(String name, PROPERTY groupingKey, int code, PROPERTY subgroup) {
        this(name, groupingKey, code);
        this.subGroupingKey = subgroup;
    }

    DC_TYPE(String name, PROPERTY groupingKey, int code) {
        this(null, name, groupingKey, code);
    }

    DC_TYPE(DC_TYPE parent, String name, PROPERTY groupingKey, int code) {
        this(name, groupingKey);
        this.code = code;
        this.setImage("ui/" + name + ".jpg");
        this.parent = parent;
    }

    DC_TYPE(String name, PROPERTY groupingKey) {
        this(name);
        this.groupingKey = groupingKey;

    }


    private static DC_TYPE getFromName(String name) {
        DC_TYPE dcType = searchMap.get(name);
        if (dcType == null) {
            name = name.toLowerCase().replace("_", " ");
            dcType = searchMap.get(name);
        }

        return dcType;
    }

    public static OBJ_TYPE getTypeByCode(int code) {
        for (OBJ_TYPE type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }


    public static DC_TYPE getType(String s) {
        if (s == null) {
            return null;
        }
        DC_TYPE type = null;
       /* try {
            type = valueOf(s.toUpperCase().replace(" ", "_"));
        } catch (Exception e) {
        }*/
        type = getFromName(s);
        if (type == null) {
            LogMaster.log(0, "OBJ_TYPE not found: " + s);
            // throw new RuntimeException();
        }
        return type;
    }

    public static int getCode(String objType) {

        //OBJ_TYPE TYPE = valueOf(objType.toUpperCase().replace(" ", "_"));
        OBJ_TYPE TYPE = getFromName(objType);
        return TYPE.getCode();
    }

    public static String getImage(String objType) {
        OBJ_TYPE TYPE = getType(objType);
        return TYPE.getImage();
    }

    public static List<DC_TYPE> getTypeGroups() {

        return Arrays.asList(values());
    }

    public static boolean isOBJ_TYPE(String name) {
        for (DC_TYPE type : DC_TYPE.values()) {
            if (StringMaster.compare(type.getName(), name, true)) {
                return true;
            }
        }
        return false;
    }

    public static Object[] getXmlGroups(OBJ_TYPE TYPE) {
        if (TYPE == DC_TYPE.ABILS) {
            return AbilityEnums.ABILITY_TYPE.values();
        }
        // if (TYPE == OBJ_TYPES.SPELLS)
        // return ASPECT.values();
        if (TYPE == DC_TYPE.CHARS) {
            return HeroEnums.RACE.values();
        }
        if (TYPE == DC_TYPE.UNITS) {
            return GenericEnums.ASPECT.values();
        }
        return null;
    }

    @Override
    public boolean isHeroTreeType() {
        return false;
    }

    @Override
    public OBJ_TYPE getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the groupingKey
     */
    public PROPERTY getGroupingKey() {
        return groupingKey;
    }

    /**
     * @param groupingKey the groupingKey to set
     */
    public void setGroupingKey(PROPERTY groupingKey) {
        this.groupingKey = groupingKey;
    }

    public PROPERTY getSubGroupingKey() {
        return subGroupingKey;
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

    public boolean isNonBattlecraft() {
        return !this.battlecraft;
    }

    @Override
    public boolean isTreeEditType() {
        return false;

    }

    public PARAMETER getParam() {
        return param;
    }

    public void setParam(PARAMETER param) {
        this.param = param;
    }

    @Override
    public PROPERTY getUpgradeRequirementProp() {
        return upgradeRequirementProp;
    }

    public void setUpgradeRequirementProp(PROPERTY upgradeRequirementProp) {
        this.upgradeRequirementProp = upgradeRequirementProp;
    }


    public boolean isOmitted() {
        return omitted;
    }
}
