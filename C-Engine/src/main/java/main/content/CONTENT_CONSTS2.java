package main.content;

import main.content.CONTENT_CONSTS.ACTION_TYPE_GROUPS;
import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.content.CONTENT_CONSTS.DEITY;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.filesys.PathFinder;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.MapMaster;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
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
    private static final DAMAGE_TYPE[] bludge = new DAMAGE_TYPE[]{DAMAGE_TYPE.BLUDGEONING};
    private static final String SEPARATOR = ">|<";
    private static final String STD_CONSTS = "({Mastery}+{Spellpower}+{spell_spell_difficulty})";
    private static final String MSTR_SP = "({Mastery}+{Spellpower})";
    private static final String SPELL_DIFFICULTY = "{spell_spell_difficulty}";
    private static final String MSTR = "{Mastery}";
    private static final String SP = "{Spellpower}";

    static {
        INJURY.CRACKED_SKULL.dmg_types = bludge;
        INJURY.CRUSHED_KNEE.dmg_types = bludge;
        INJURY.BROKEN_RIBS.dmg_types = bludge;
        INJURY.CRACKED_SKULL.dmg_types = bludge;

    }

    public enum INJURY_TYPE {
        LIMB, HEAD, BODY, INTERNAL
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

    public enum AUTO_TEST_TYPE {
        ACTION, PASSIVE_MEASURE, ACTION_SKILL,
    }

    public enum AUTO_TEST_PREFS {
        SOURCE_TYPE, TARGET_TYPE, TARGETS_COUNT, RELATIVE_POS,
    }

    public enum AUTO_TEST_MEASUREMENT {
        VALUE, DISTANCE, COUNTER, DMG
    }

    public enum AUTO_TEST_ASSERTION {
        POS, DEAD, VAL

    }

    // PERHAPS ENCOUNTERS COULD HAVE DEITY PROPERTY AS WELL;
    public enum UNIT_GROUP {

        HUMANS("Militia,Scum,Guards,Army,Melee"),
        GREENSKINS("goblins,orcs"),
        BANDITS("Pirates,Thieving Crew,Robbers,"),
        KNIGHTS("ravenguard,holy,"),
        DWARVES("woads,"),
        NORTH("norse,woads,brutes,north"),
        UNDEAD("Plague,"),
        DEMONS("chaos,demons,demon worshippers"),
        ANIMALS("Animals,Wolves,Wargs,wild,"),
        MAGI("constructs,apostates,magi,,"),
        CRITTERS("critters,spiders,"),
        DUNGEON("chaos,demons,worshippers,"),
        FOREST("greenies,creatures"),;
        private String groups;

        UNIT_GROUP(String groups) {
            this.groups = groups;
        }

        public String getGroups() {
            return groups;
        }
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
            if (!group.isEmpty())
                this.group = group;
            if (!image.isEmpty())
                this.image = image;
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
            return StringMaster.constructContainer(ListMaster.toStringList(allies));
        }

        public String getGroup() {
            return group;
        }

        public String getImage() {
            return image;
        }
    }

    // to use {INDEXES} ... I'd need a lot of smarts :) >|< separator for
    /*
     * Pestlego
	 * Astro 
	 * Syphron
	 * 
	 */

    public enum ORDER_TYPE {
        PATROL, PURSUIT, MOVE, ATTACK, KILL, SPECIAL, HEAL, SUPPORT, PROTECT, HOLD, WANDER
    }

    public enum GLOBAL_ORDER_TYPE {
        EXPLORE, RALLY, SCATTER, WANDER,
    }

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
            if (image == null)
                image = ImageManager.getImage(getImageFileName());
            return image;

        }

        public String getImageFileName() {
            return "UI\\components\\ht\\" + "LINK_" + name() + ".png";
        }

        public int getXOffset() {
            return (getOffsetMultiplier()) * getImage().getWidth(null);
        }

        public int getOffsetMultiplier() {
            if (isToLeft())
                return -1;
            if (isToRight())
                return 1;
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

            if (this == LINK_VARIANT.VERTICAL || this == LINK_VARIANT.VERTICAL_LONG)
                return 32 - this.getImage().getWidth(null) / 2;

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
            if (this == LINK_VARIANT.VERTICAL || this == LINK_VARIANT.VERTICAL_LONG)
                return 0;

            if (isToLeft())
                return -32; // -getImage().getWidth(null) / 2 + 32;
            return getImage().getWidth(null) - 32;
            // getXOffsetForLink(this);
            // return nodeOffsetX;
        }

        public boolean isAutoPos() {
            // TODO Auto-generated method stub
            return false;
        }

    }

    public enum MACRO_STATUS {
        CAMPING, EXPLORING, TRAVELING, IN_AMBUSH,
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

        public String[] getItem_groups() {
            return item_groups;
        }

        public PROPERTY getFilterProp() {
            return filterProp;
        }
    }

    public enum AI_MODIFIERS {
        TRUE_BRUTE, COWARD, MERCIFUL, CRUEL,
    }

    public enum STD_ACTION_MODES {
        STANDARD,
        SWIFT("", "", "", "", "", "-35;25;-25;-35", "DAMAGE_MOD;ATTACK_MOD;STA_COST;AP_COST;", "", "", ACTION_TYPE_GROUPS.ATTACK),
        MIGHTY("", "", "", "", "", "35;-25;50;35", "DAMAGE_MOD;ATTACK_MOD;STA_COST;AP_COST;", "", "", ACTION_TYPE_GROUPS.ATTACK),
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
            if (groups == null)
                defaultActionGroups = new LinkedList<>();
            else
                defaultActionGroups = Arrays.asList(groups);
            this.description = description;
            this.addPropMap = new MapMaster<String, String>().constructMap(StringMaster
                    .openContainer(addProps), StringMaster.openContainer(addPropValues));
            this.setPropMap = new MapMaster<String, String>().constructMap(StringMaster
                    .openContainer(setProps), StringMaster.openContainer(setPropValues));
            this.paramModMap = new MapMaster<String, String>().constructMap(StringMaster
                    .openContainer(modParams), StringMaster.openContainer(modParamValues));
            this.paramBonusMap = new MapMaster<String, String>().constructMap(StringMaster
                    .openContainer(bonusParams), StringMaster.openContainer(bonusParamVals,
                    SEPARATOR));
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

    public enum SPELL_UPGRADE {
        ELEMENTAL(" Deals additional 15% as Lightning, Cold and Fire damage each.", 25, 25, " el", "passives;", getDamagePercentSpecialEffect(
                false, "15,Lightning", "15,Cold", "15,Fire")),
        ASTRAL(" Deals additional 10% as Holy, Shadow, " + "Chaos and Death damage each.", 25, 25, " as", "passives;", getDamagePercentSpecialEffect(
                false, "10,Holy", "10,Shadow", "10,Chaos", "10,Death")),

        PSIONIC(" Deals additional Psionic damage based on the spell's Difficulty.", 25, 25, " el", "passives;", getDamageSpecialEffect("({Mastery}+{Spellpower}+{spell_spell_difficulty}) +({Mastery}+{Spellpower}+{spell_spell_difficulty}) *({Mastery}+{Spellpower}+{spell_spell_difficulty}) /5,Psionic")),

        IRIDESCENT(" Deals additional 15% as Lightning, Fire and Holy damage each.", 25, 25, " ir", "passives;", getDamagePercentSpecialEffect(
                false, "15,Light", "15,Holy", "15,Fire"), "", "", "", "", "resistance mod;range", "-25>|<1"),

        BLAZE("Deals additional 25% Fire damage and applies an amount of Blaze counters equal to 10% of damage dealt.", 25, 25, " ir", "passives;", getDamagePercentSpecialEffect(
                false, "25,Fire")
                + ">|<" + getAddCounterSpecialEffect("Blaze,{active_damage_last_dealt}/10")),

        MESMERIC(" Reduces target's Focus by " + STD_CONSTS + "/2.", 25, 25, " me", "passives;", getValueModSpecialEffect("C_Focus,-"
                + STD_CONSTS + "/2")),

        FEARSOME(" Reduces target's Morale by " + STD_CONSTS + ".", 25, 25, " fe", "passives;", getValueModSpecialEffect("C_Morale,-"
                + STD_CONSTS + "")),

        DARK_FIRE(" Deals additional 10% as Fire and Shadow damage.", 25, 25, " as", "passives;", getDamagePercentSpecialEffect(
                false, "25,Shadow", "25,Fire")),

        CORROSION(" Adds " + STD_CONSTS + "/10 Corrosion counters.", 25, 25, " as", "passives;", getAddCounterSpecialEffect("Corrosion,"
                + STD_CONSTS + "/10")),

        BLIGHT(" Adds " + STD_CONSTS + "/8 Blight counters.", 25, 25, " as", "passives;", getAddCounterSpecialEffect("Blight,"
                + STD_CONSTS + "/8")),

        DRAINING(" Reduces target's Stamina by " + STD_CONSTS + "/4.", 25, 25, " dr", "passives;", getValueModSpecialEffect("C_Stamina,-"
                + STD_CONSTS + "/4")),

        HELLFIRE(" Deals additional 10% as " + "Chaos and Fire damage.", 25, 25, " as", "passives;", getDamagePercentSpecialEffect(
                false, "25,Chaos", "25,Fire"), "", "", "", "", "", ""),

        PUTRID(" Adds " + STD_CONSTS + "/10 Disease counters.", 20, 15, " as", "passives;", getAddCounterSpecialEffect("Disease,"
                + STD_CONSTS + "/10")),

        TOXIC(" Adds " + "" + "/5 Poison counters.", 15, 10, " as", "passives;", getAddCounterSpecialEffect("Poison,"
                + "" + "/5")),

        APHOTIC,

        VAMPIRIC(" Drains (10+{Mastery}/2)% of damage dealt into caster's Endurance.",
                35, 25, " de", "passives;", "LifeStealSpell(10+{Mastery}/2)"),
        DEATH_CHILL(" Adds " + STD_CONSTS + "/10 Freeze counters.", 20, 15, " de", "passives;", getAddCounterSpecialEffect("Freeze,"
                + STD_CONSTS + "/5")),
        SHOCK,

        ELDRITCH,
        CHRONO,
        GRAVITY,
        VOID,

        ADAMANTIUM, // spellpower is increased by X?
        METEORITE,
        DARK_STEEL,
        MOON_SILVER,
        GHOSTLY,
        SOUL,
        // resistance penetration? special roll? add Intelligence to the Beat
        WICKED,
        VOODOO(" Random curse/impossible to dispel unless caster is dead/.", 20, 15, " de", "passives;", getAddCounterSpecialEffect("Freeze,"
                + STD_CONSTS + "/5")),
        ENTROPY, // % of max toughness

        DESPAIR,
        HATRED,
        LUST,
        TERROR,

        CRIMSON, // % of missing endurance

        MUTAGENIC,
        ACIDIC,
        DIRE(" Increases Spellpower by " + SPELL_DIFFICULTY + " and an additional {Mastery}/10%.", 35, 25, " de", "passives;", "AddParam(Spellpower Bonus 10+{Mastery}/2)", "", "", "", "", "Spellpower Bonus;Spellpower Mod", ""
                + SPELL_DIFFICULTY + SEPARATOR + "{Mastery}/10"),
        RAGE,

        LIFEBLOOD, // append {endurance}*(min(10+{Mastery},{1})/100 to formula
        // so there is %
        ELDER,

        FROSTY,
        FIERY,
        PLASMA,

        UNDIMMED,
        ABSOLUTION,
        HERESY,
        VINDICATION,
        REST_IN_PEACE, // remove undying counters

        MOONLIGHT,
        PALE, //
        TWILIGHT, // ALT ENUM WITH SAME DISPLAYED NAME - FOR BENEDICTION

        STUN,
        // Roll(Mass,{active_damage_dealt},-, Knockback)
        KNOCKBACK,
        KNOCKDOWN,

        EMPOWERED,
        QUICKENED,
        SIMPLIFIED,
        EXTENDED,
        MULTIPLIED,
        ECHO,
        RECURRING,

        RAY,
        NOVA,
        BLAST,
        GLOBAL
		/*
		 * META MAGIC IN WIZARDRY!
		 */;
        private Map<String, String> addPropMap;
        private Map<String, String> setPropMap;
        private Map<String, String> paramModMap;
        private Map<String, String> paramBonusMap;
        private int spellDifficultyMod;
        private int costMod;
        private String imgSuffix;
        private String description;
        private Image glyphImageActive;
        private Image glyphSmallImage;
        private Image glyphImage;
        private Image glyphImageSelected;

        SPELL_UPGRADE() {

        }

        SPELL_UPGRADE(String description, int spellDifficultyMod, int costMod, String imgSuffix,
                      String addProps, String addPropValues) {
            this(description, spellDifficultyMod, costMod, imgSuffix, addProps, addPropValues, "",
                    "", "", "", "", "");
        }

        SPELL_UPGRADE(String description, int spellDifficultyMod, int costMod, String imgSuffix,
                      String addProps, String addPropValues, String setProps, String setPropValues,
                      String modParams, String modParamValues, String bonusParams, String bonusParamVals) {
            this.description = description;
            this.imgSuffix = imgSuffix;
            this.costMod = costMod;
            this.spellDifficultyMod = spellDifficultyMod;
            this.addPropMap = new MapMaster<String, String>().constructMap(StringMaster
                    .openContainer(addProps), StringMaster.openContainer(addPropValues));
            this.setPropMap = new MapMaster<String, String>().constructMap(StringMaster
                    .openContainer(setProps), StringMaster.openContainer(setPropValues));
            this.paramModMap = new MapMaster<String, String>().constructMap(StringMaster
                    .openContainer(modParams), StringMaster.openContainer(modParamValues));
            this.paramBonusMap = new MapMaster<String, String>().constructMap(StringMaster
                    .openContainer(bonusParams), StringMaster.openContainer(bonusParamVals,
                    SEPARATOR));
        }

        private static String getAddCounterSpecialEffect(String... amount_comma_dmg_type) {
            String effectCase = "ON_SPELL_IMPACT";
            String abilName = "AddCounter";
            return getSpecialEffect(effectCase, abilName, amount_comma_dmg_type);

        }

        private static String getValueModSpecialEffect(String... param_comma_amount) {
            String effectCase = "ON_SPELL_IMPACT";
            String abilName = "AddParam";
            return getSpecialEffect(effectCase, abilName, param_comma_amount);
        }

        private static String getDamageSpecialEffect(String... amount_comma_dmg_type) {
            String effectCase = "ON_SPELL_IMPACT";
            String abilName = "SpellDamage";
            return getSpecialEffect(effectCase, abilName, amount_comma_dmg_type);
        }

        private static String getDamagePercentSpecialEffect(boolean toDealt,
                                                            String... amount_comma_dmg_type) {
            String effectCase = "ON_SPELL_IMPACT";
            String abilName = toDealt ? "DmgAddToDealt" : "DmgAdd";
            return getSpecialEffect(effectCase, abilName, amount_comma_dmg_type);

        }

        private static String getSpecialEffect(String effectCase, String abilName, String... args) {
            String string = "SpecEffect(" + effectCase + ",";
            for (String s : args) {
                string += abilName + StringMaster.wrapInParenthesis(s);
                string += StringMaster.AND_SEPARATOR;
            }
            string = StringMaster.cropLast(string, 1);
            string = string + ")";
            return string;
        }

        public int getSpellDifficultyMod() {
            return spellDifficultyMod;
        }

        public int getCostMod() {
            return costMod;
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

        public String getImgSuffix() {
            return imgSuffix;
        }

        public void setImgSuffix(String imgSuffix) {
            this.imgSuffix = imgSuffix;
        }

        public String getName() {
            return StringMaster.getWellFormattedString(toString());
        }

        public String getDescription() {
            return description;
        }

        public Image getGlyphImageSelected() {
            if (glyphImageSelected == null) {
                glyphImageSelected = ImageManager.getImage(PathFinder.getSpellUpgradeGlyphsFolder()
                        + StringMaster.getWellFormattedString(name()) + " s.png");
            }
            if (!ImageManager.isValidImage(glyphImageSelected))
                return glyphImage;
            return glyphImageSelected;
        }

        public Image getGlyphImageActive() {
            if (glyphImageActive == null) {
                glyphImageActive = ImageManager.getImage(PathFinder.getSpellUpgradeGlyphsFolder()
                        + StringMaster.getWellFormattedString(name()) + " a.png");
            }
            if (!ImageManager.isValidImage(glyphImageSelected))
                return glyphImage;
            return glyphImageActive;
        }

        public Image getGlyphImage() {
            if (glyphImage == null) {
                glyphImage = ImageManager.getImage(PathFinder.getSpellUpgradeGlyphsFolder()
                        + StringMaster.getWellFormattedString(name()) + ".png");
            }
            return glyphImage;
        }

        public Image getGlyphSmallImage() {
            if (glyphSmallImage == null) {
                Image img = getGlyphImage(); // active?
                glyphSmallImage = ImageManager.getSizedVersion(img, 33);
            }
            return glyphSmallImage;
        }

    }

}
