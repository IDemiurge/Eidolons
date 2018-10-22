package main.content;

import main.content.CONTENT_CONSTS.DEITY;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CONTENT_CONSTS2 {
    public static final int POWER_ATTACK_DMG_MOD = 135;
    public static final int POWER_ATTACK_ATTACK_MOD = 75;
    public static final int POWER_ATTACK_STA_COST_MOD = 150;
    public static final int POWER_ATTACK_AP_COST_MOD = 135;
    public static final int QUICK_ATTACK_DMG_MOD = 65;
    public static final int QUICK_ATTACK_ATTACK_MOD = 125;
    public static final int QUICK_ATTACK_STA_COST_MOD = 75;
    public static final int QUICK_ATTACK_AP_COST_MOD = 65;
    private static final DAMAGE_TYPE[] bludge = new DAMAGE_TYPE[]{GenericEnums.DAMAGE_TYPE.BLUDGEONING};


    static {
        INJURY.CRACKED_SKULL.dmg_types = bludge;
        INJURY.CRUSHED_KNEE.dmg_types = bludge;
        INJURY.BROKEN_RIBS.dmg_types = bludge;
        INJURY.CRACKED_SKULL.dmg_types = bludge;

    }

    public enum AI_MODIFIERS {
        TRUE_BRUTE, COWARD, MERCIFUL, CRUEL,
    }

    public enum AUTO_TEST_ASSERTION {
        POS, DEAD, VAL

    }

    public enum AUTO_TEST_MEASUREMENT {
        VALUE, DISTANCE, COUNTER, DMG
    }

    public enum AUTO_TEST_PREFS {
        SOURCE_TYPE, TARGET_TYPE, TARGETS_COUNT, RELATIVE_POS,
    }

    public enum AUTO_TEST_TYPE {
        ACTION, PASSIVE_MEASURE, ACTION_SKILL,
    }

    public enum FACTION {
        DWARVEN_CLANS("", "", ""),
        PIRATE_REPUBLIC,
        SAVAGE_TRIBES,

        CHAOS_LEGION,
        WARP_MINIONS,
        DEMONS_OF_ABYSS,

        SONS_OF_ULAMIR,
        CELESTIALS,
        THE_WILD_HUNT,
        WOOD_ELVES,

        THIEVES_GUILD,
        BARON_AVENMOR,
        BARON_GERNTOD,
        BARON_MOLFREY,
        RAVENGUARD,
        SILVER_KNIGHTS,
        INQUISITION,

        PLAGUE_EATERS,
        DEATH_CULT,
        PALE_LEGIONS,
        VAMPIRE_LORDS,

        TWILIGHT_ORDER,
        DEEP_ONES,
        NIGHT_HAUNTERS,
        THE_COVEN,
        CIRCLE_OF_MAGI,
        THE_STRIX,;

        DEITY deity;
        String units;
        String group = "Standard";
        FACTION[] allies;
        private String image = "mini\\special\\pirate.jpg";

        FACTION() {

        }

        FACTION(String units, String group, String image, FACTION... allies) {
            this.units = units;
            if (!group.isEmpty()) {
                this.group = group;
            }
            if (!image.isEmpty()) {
                this.image = image;
            }
            this.allies = allies;
        }

        @Override
        public String toString() {
            return StringMaster.getWellFormattedString(super.toString());
        }

        public String getUnits() {
            return units;
        }

        public DEITY getDeity() {
            return deity;
        }

        public void setDeity(DEITY deity) {
            this.deity = deity;
        }

        public String getAllyFactions() {
            return ContainerUtils.constructContainer(ListMaster.toStringList(allies));
        }

        public String getGroup() {
            return group;
        }

        public String getImage() {
            return image;
        }
    }

    public enum GLOBAL_ORDER_TYPE {
        EXPLORE, RALLY, SCATTER, WANDER,
    }

    public enum INJURY {
        RIPPED,
        MAIMED,
        SLASHED,
        PIERCED,
        CHOPPED,

        CRACKED_SKULL,

        TWISTED_ANKLE,
        SPRINGED_LEG(INJURY_TYPE.LIMB, "Move Ap Penalty(50);Defense Mod(-20);Stealth([mod]-25"),
        CRUSHED_KNEE, // Reaction Roll Save Bonus();

        DAMAGED_FINGERS(INJURY_TYPE.LIMB, "Attack Ap Penalty(20);Damage Mod(-20);Attack Mod(-20);"),
        SNAPPED_WRIST(INJURY_TYPE.LIMB, "Throw Attack Mod(-30);Attack Ap Penalty(35);Damage Mod|Diagonal Damage Mod|Side Damage Mod(-20)"),
        BROKEN_ARM(INJURY_TYPE.LIMB, "Attack Ap Penalty(50);Damage Mod(-20);Force Mod(-35)"),

        BROKEN_RIBS(INJURY_TYPE.BODY, "Bludgeoning Resistance(-25);Stamina Penalty(35);"),
        CRACKED_SPINE(INJURY_TYPE.BODY, "Ap Penalty(35);"),;
        public DAMAGE_TYPE[] dmg_types;
        private String modString;
        private INJURY_TYPE type;

        INJURY() {
        }

        INJURY(INJURY_TYPE type, String modString) { // int shock,
            this.modString = modString;
            this.type = type;
        }

        public String getModString() {
            return modString;
        }

        public INJURY_TYPE getType() {
            return type;
        }

    }

    public enum INJURY_TYPE {
        LIMB, HEAD, BODY, INTERNAL
    }

    //FOR AV!!! DON'T JUST REMOVE
    public enum LINK_VARIANT {

        ANGLE_TO_LEFT_2,
        ANGLE_TO_LEFT_1,
        ANGLE_TO_LEFT_0,
        ANGLE_TO_LEFT,
        ANGLE_TO_LEFT2,
        ANGLE_TO_LEFT3,
        VERTICAL(true),
        ANGLE_TO_RIGHT3,
        ANGLE_TO_RIGHT2,
        ANGLE_TO_RIGHT,
        ANGLE_TO_RIGHT_0,
        ANGLE_TO_RIGHT_1,
        ANGLE_TO_RIGHT_2,

        HORIZONTAL(false),
        VERTICAL_LONG(true),
        VERTICAL_SHORT(true),

        ANGLE_TO_LEFT_SHORT,
        ANGLE_TO_RIGHT_SHORT,
        VERTICAL_XL(true),
        VERTICAL_XXL(true),

        VERTICAL_THIN(true),
        VERTICAL_LONG_THIN(true),
        VERTICAL_XL_THIN(true),
        VERTICAL_XXL_THIN(true),

        HORIZONTAL_LONG(false),
        HORIZONTAL_SHORT(false);

        int offsetY;
        int offsetX;
        int nodeOffsetY;
        int nodeOffsetX;
        private Image image;

        LINK_VARIANT(boolean b) {

        }

        LINK_VARIANT() {

            // this.offsetY = getYOffsetForLink(this);
            // this.offsetX = getXOffsetForLink(this);
            // this.nodeOffsetY = getYOffsetForLink(this);
            // this.nodeOffsetX = getXOffsetForLink(this);
        }

        LINK_VARIANT(int offsetY, int offsetX, int nodeOffsetY, int nodeOffsetX) {
            this.offsetY = offsetY;
            this.offsetX = offsetX;
            this.nodeOffsetY = nodeOffsetY;
            this.nodeOffsetX = nodeOffsetX;
        }

        public int getWidth() {
            return getImage().getWidth(null);
        }

        public int getHeight() {
            return getImage().getHeight(null);
        }

        public Image getImage() {
            if (image == null) {
                image = ImageManager.getImage(getImageFileName());
            }
            return image;

        }

        public String getImageFileName() {
            return "UI\\components\\ht\\" + "LINK_" + name() + ".png";
        }

        public int getXOffset() {
            return (getOffsetMultiplier()) * getImage().getWidth(null);
        }

        public int getOffsetMultiplier() {
            if (isToLeft()) {
                return -1;
            }
            if (isToRight()) {
                return 1;
            }
            return 0;
        }

        public boolean isToLeft() {
            return name().contains("LEFT");
        }

        public boolean isToRight() {
            return name().contains("RIGHT");
        }

        public int getOffsetY() {
            return -getImage().getHeight(null);
            // return offsetY;
        }

        public int getOffsetX() {

            if (this == LINK_VARIANT.VERTICAL || this == LINK_VARIANT.VERTICAL_LONG) {
                return 32 - this.getImage().getWidth(null) / 2;
            }

            int rankPoolWidth = STD_IMAGES.RANK_COMP.getWidth();
            if (this.isToLeft()) {
                // right edge at rc's end
                // now left edge at comp's edge

                return -this.getWidth() + (64 - rankPoolWidth) / 2 + rankPoolWidth;
            } else {
                return (64 - rankPoolWidth) / 2;
            }
            // return offsetX;
        }

        public int getNodeOffsetY() {
            return -getImage().getHeight(null);
            // return nodeOffsetY;
        }

        public int getNodeOffsetX() {
            if (this == LINK_VARIANT.VERTICAL || this == LINK_VARIANT.VERTICAL_LONG) {
                return 0;
            }

            if (isToLeft()) {
                return -32; // -getImage().getWidth(null) / 2 + 32;
            }
            return getImage().getWidth(null) - 32;
            // getXOffsetForLink(this);
            // return nodeOffsetX;
        }

        public boolean isAutoPos() {
            // TODO Auto-generated method stub
            return false;
        }

    }

    // to use {INDEXES} ... I'd need a lot of smarts :) >|< separator for
    /*
     * Pestlego
	 * Astro 
	 * Syphron
	 * 
	 */

    public enum MACRO_STATUS {
        CAMPING, EXPLORING, TRAVELING, IN_AMBUSH, IDLE,
    }

    public enum MIST_VFX {
        WHITE_MIST,
        CYAN_MIST,
        DARK_MIST,
        CLOUDS,;

        MIST_VFX() {

        }

        public String getPath() {
            return StrPathBuilder.build("mist", name().replace("_", " "));
        }
    }


    public enum ORDER_TYPE {
        PATROL, PURSUIT, MOVE, ATTACK, KILL, SPECIAL, HEAL, SUPPORT, PROTECT, HOLD, WANDER
    }

    public enum SHOP_LEVEL { // quality and materials - filter in and Type will
        // filter out?
        POOR,
        COMMON,
        QUALITY,
        OPULENT,
    }

    public enum SHOP_MODIFIER {
        HUMAN, ELVEN, DWARVEN, WIZARDING, DARK, UNDERGROUND, HOLY,
    }

    public enum SHOP_TYPE {
        // ++ per faction?
        MERCHANT("cloth", "ranged", "ammo", "potions", "poisons", "elixirs", "concoctions", "orbs", "wands"), // if
        SPECIAL_GOODS("ranged", "ammo", "daggers", "poisons"),
        JEWELER("rings", "amulets", "empty"),

        HEAVY_WEAPONS(G_PROPS.WEAPON_SIZE, "huge", "large", "medium"),
        WEAPONS(G_PROPS.WEAPON_SIZE, "large", "medium", "small"),
        LIGHT_WEAPONS(G_PROPS.WEAPON_SIZE, "medium", "small", "tiny"),

        LIGHT_ARMOR("cloth", "leather", "bucklers"),
        ARMOR("chain", "leather", "bucklers"),
        HEAVY_ARMOR("chain", "plate", "shields"), //

        ALCHEMIST("potions", "poisons", "elixirs", "concoctions"),
        UNDERTAKER,
        BLACK_MARKET,

        MAGICAL_GOODS("orbs", "wands", "potions", "elixirs"),
        ARTIFACTS("orbs", "wands"),
        MISC,;
        String[] item_groups;
        PROPERTY filterProp;

        // ++ item type exceptions
        SHOP_TYPE(PROPERTY filterProp, String... item_groups) {
            this.item_groups = item_groups;
            this.filterProp = filterProp;
        }

        SHOP_TYPE(String... item_groups) {
            this.item_groups = item_groups;
        }

        public String[] getItemGroups() {
            return item_groups;
        }

        public PROPERTY getFilterProp() {
            return filterProp;
        }
    }

    public enum STD_ACTION_MODES {
        STANDARD,
        SWIFT("", "", "", "", "", "-35;25;-25;-35", "DAMAGE_MOD;ATTACK_MOD;STA_COST;AP_COST;", "", "", ActionEnums.ACTION_TYPE_GROUPS.ATTACK),
        MIGHTY("", "", "", "", "", "35;-25;50;35", "DAMAGE_MOD;ATTACK_MOD;STA_COST;AP_COST;", "", "", ActionEnums.ACTION_TYPE_GROUPS.ATTACK),
        QUICK(),
        SILENT(),
        // QUICK("", "", "", "", "", "-35;25;-25;-35",
        // "DAMAGE_MOD;ATTACK_MOD;STA_COST;AP_COST;", "", ""),
        // SILENT("", "", "", "", "", "35;-25;50;35",
        // "DAMAGE_MOD;ATTACK_MOD;STA_COST;AP_COST;", "", ""),

        AIMED,
        DEFENSIVE,
        ALERT,
        INTENSE;
        public List<ACTION_TYPE_GROUPS> defaultActionGroups;
        private String description;
        private Map<String, String> addPropMap;
        private Map<String, String> setPropMap;
        private Map<String, String> paramModMap;
        private Map<String, String> paramBonusMap;

        STD_ACTION_MODES() {
            this("", "", "", "", "", "", "", "", "");
        }

        STD_ACTION_MODES(String description, String addProps, String addPropValues,
                         String setProps, String setPropValues, String modParamValues, String modParams,
                         String bonusParams, String bonusParamVals, ACTION_TYPE_GROUPS... groups) {
            if (groups == null) {
                defaultActionGroups = new ArrayList<>();
            } else {
                defaultActionGroups = Arrays.asList(groups);
            }
            this.description = description;
            this.addPropMap = new MapMaster<String, String>().constructMap(ContainerUtils
             .openContainer(addProps), ContainerUtils.openContainer(addPropValues));
            this.setPropMap = new MapMaster<String, String>().constructMap(ContainerUtils
             .openContainer(setProps), ContainerUtils.openContainer(setPropValues));
            this.paramModMap = new MapMaster<String, String>().constructMap(ContainerUtils
             .openContainer(modParams), ContainerUtils.openContainer(modParamValues));
            this.paramBonusMap = new MapMaster<String, String>().constructMap(ContainerUtils
             .openContainer(bonusParams), ContainerUtils.openContainer(bonusParamVals,
             StringMaster.UPGRADE_SEPARATOR));
        }

        public String getPrefix() {
            return StringMaster.getWellFormattedString(name()) + " ";
        }

        public String getDescription() {
            return description;
        }

        public Map<String, String> getAddPropMap() {
            return addPropMap;
        }

        public Map<String, String> getSetPropMap() {
            return setPropMap;
        }

        public Map<String, String> getParamModMap() {
            return paramModMap;
        }

        public Map<String, String> getParamBonusMap() {
            return paramBonusMap;
        }

        public List<ACTION_TYPE_GROUPS> getDefaultActionGroups() {
            return defaultActionGroups;
        }
    }

}
