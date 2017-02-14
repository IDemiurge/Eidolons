package main.content;

import main.content.CONTENT_CONSTS.ABILITY_TYPE;
import main.content.CONTENT_CONSTS.ASPECT;
import main.content.CONTENT_CONSTS.RACE;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.Arrays;
import java.util.List;

public enum OBJ_TYPES implements OBJ_TYPE {
    UNITS("units", G_PROPS.ASPECT, 0, G_PROPS.UNIT_GROUP),
    SPELLS("spells", G_PROPS.ASPECT, 1, G_PROPS.SPELL_GROUP),
    CHARS("chars", G_PROPS.RACE, 2, G_PROPS.GROUP),

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

    DEITIES("deities", G_PROPS.GROUP, 14, G_PROPS.ASPECT),
    ENCOUNTERS("encounters", G_PROPS.GROUP, 15, G_PROPS.ENCOUNTER_GROUP),
    DUNGEONS("dungeons", G_PROPS.ARCADE_REGION, 16),
    PARTY("party", G_PROPS.GROUP, 17),
    TERRAIN("terrain", G_PROPS.GROUP, 18, false),
    FACTIONS("factions", G_PROPS.FACTION_GROUP, 19),
    ARCADES("arcades", G_PROPS.GROUP, 20),
    TRAP("trap", G_PROPS.GROUP, 21),
    META("meta", G_PROPS.GROUP, -1, true),
    ALL("all"),;

    static {
        GARMENT.hidden = true;
        GARMENT.battlecraft = false;
        TRAP.battlecraft = false;
        ENCOUNTERS.battlecraft = false;
        ARCADES.battlecraft = false;
        FACTIONS.battlecraft = false;
    }

    boolean battlecraft = true;
    private String name;
    private PROPERTY groupingKey;
    private PROPERTY subGroupingKey;
    private PROPERTY upgradeRequirementProp;
    private int code = -1;
    private String image;
    private boolean hidden;
    private PARAMETER param;

    OBJ_TYPES(String name, PROPERTY groupingKey, int code, boolean hidden) {
        this(name, groupingKey, code);
        this.setHidden(hidden);
    }

    OBJ_TYPES(String name) {
        this.name = name;
        this.subGroupingKey = G_PROPS.GROUP;
    }

    OBJ_TYPES(String name, PROPERTY groupingKey, int code, PROPERTY subgroup) {
        this(name, groupingKey, code);
        this.subGroupingKey = subgroup;
    }

    OBJ_TYPES(String name, PROPERTY groupingKey, int code) {
        this(name, groupingKey);
        this.code = code;
        this.setImage("UI\\" + name + ".jpg");
    }

    OBJ_TYPES(String name, PROPERTY groupingKey) {
        this(name);
        this.groupingKey = groupingKey;

    }

    public static OBJ_TYPE getTypeByCode(int code) {
        for (OBJ_TYPE type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    public static OBJ_TYPES getType(String s) {
        if (s == null) {
            return null;
        }
        OBJ_TYPES type = null;
        try {
            type = valueOf(s.toUpperCase().replace(" ", "_"));
        } catch (Exception e) {
        }
        if (type == null) {
            LogMaster.log(0, "OBJ_TYPE not found: " + s);
            // throw new RuntimeException();
        }
        return type;
    }

    public static int getCode(String objType) {

        OBJ_TYPE TYPE = valueOf(objType.toUpperCase().replace(" ", "_"));
        return TYPE.getCode();
    }

    public static String getImage(String objType) {
        OBJ_TYPE TYPE = getType(objType);
        return TYPE.getImage();
    }

    public static List<OBJ_TYPES> getTypeGroups() {

        return Arrays.asList(values());
    }

    public static boolean isOBJ_TYPE(String name) {
        for (OBJ_TYPES type : OBJ_TYPES.values()) {
            if (StringMaster.compare(type.getName(), name, true)) {
                return true;
            }
        }
        return false;
    }

    public static Object[] getXmlGroups(OBJ_TYPE TYPE) {
        if (TYPE == OBJ_TYPES.ABILS) {
            return ABILITY_TYPE.values();
        }
        // if (TYPE == OBJ_TYPES.SPELLS)
        // return ASPECT.values();
        if (TYPE == OBJ_TYPES.CHARS) {
            return RACE.values();
        }
        if (TYPE == OBJ_TYPES.UNITS) {
            return ASPECT.values();
        }
        return null;
    }

    @Override
    public boolean isHeroTreeType() {
        return false;
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

}
