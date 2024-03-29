package eidolons.content;

import eidolons.content.values.ValuePages;
import main.content.ContentValsManager;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.QUALITY_LEVEL;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.STANDARD_PASSIVES;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.system.auxiliary.StringMaster;

public class DC_CONSTS {

    public static final int MAX_INV_ITEMS = 30;
    public static final int AS_SWITCH_ATB_BASE = 5;
    public static final int AS_SWITCH_FOCUS_BASE = 3;
    public static final int CHAOS_LEVEL_STARTS_AT = 3;

    public enum CONCOCTION_LEVEL {
        CHEAP(50, 40, "", ItemEnums.QUALITY_LEVEL.INFERIOR),
        NORMAL(100, 100, "1", ItemEnums.QUALITY_LEVEL.NORMAL),
        POTENT(200, 300, "2", ItemEnums.QUALITY_LEVEL.SUPERIOR),;
        private int mod;
        private final String imgCode;
        private final int cost;
        private final QUALITY_LEVEL quality;

        CONCOCTION_LEVEL(int mod, int cost, String imgCode,
                         QUALITY_LEVEL quality) {
            this.mod = mod;
            this.imgCode = imgCode;
            this.cost = cost;
            this.quality = quality;
        }

        public QUALITY_LEVEL getQuality() {
            return quality;
        }

        public int getMod() {
            return mod;
        }

        public void setMod(int mod) {
            this.mod = mod;
        }

        public String getName() {
            return StringMaster.format(name());
        }

        public String getImgCode() {
            return imgCode;
        }

        public int getCost() {
            return cost;
        }
    }

    public enum JEWELRY_ITEM_TRAIT {
        ATTRIBUTE_BONUS(5, "", 2, 4, 8, 15, 25, "Amulet;Power Ring", ContentValsManager
         .getFinalAttributes().toArray(
          new PARAMETER[ATTRIBUTE.values().length])),
        // TODO names!
        MAGIC_RESISTANCES(7, "", 15, 25, 40, 60, 85, "Magic Ring", ValuePages.MAGIC_RESISTANCES),
        PHYSICAL_RESISTANCES(10, "", 12, 18, 25, 35, 50, "Bracer", ValuePages.PHYSICAL_RESISTANCES),
        RESISTANCE(8, "", 8, 13, 18, 24, 32, "Signet", PARAMS.RESISTANCE),
        RESISTANCE_PENETRATION(6, "", 10, 16, 24, 32, 42, "Possessed Ring", PARAMS.RESISTANCE_PENETRATION),

        // ESSENCE_REGENERATION(10, "", 5, 12, 25, 60, 150, "Magic Ring",
        // PARAMS.ESSENCE_REGEN),
        ENDURANCE_REGENERATION(12, "", 6, 20, 40, 80, 200, "Signet;Pendant", PARAMS.ENDURANCE_REGEN),
        ENDURANCE(4, "", 30, 75, 200, 500, 1250, "Bracer", PARAMS.ENDURANCE),
        TOUGHNESS(4, "", 15, 35, 80, 200, 550, "Bone Ring", PARAMS.TOUGHNESS),
        DEFENSE(4, "", 10, 18, 30, 50, 100, "Signet", PARAMS.DEFENSE),
        ATTACK(3, "", 12, 20, 35, 60, 115, "Bracer", PARAMS.ATTACK),
        SPIRIT(3, "", 2, 4, 8, 16, 35, "Noble Ring;Pendant", PARAMS.SPIRIT),

//        MEMORIZATION_CAP(6, "", 10, 24, 45, 80, 200, "Magic Ring", PARAMS.MEMORIZATION_CAP),
        CARRYING_CAPACITY(6, "", 15, 35, 80, 175, 400, "Bracer", PARAMS.CARRYING_CAPACITY),
//        INITIATIVE_MODIFIER(15, "", 4, 8, 15, 25, 40, "Noble Ring", PARAMS.INITIATIVE_MODIFIER),
        N_OF_ACTIONS(15, "", 1, 2, 4, 9, 22, "Power Ring", PARAMS.INITIATIVE),
        SIGHT_RANGE(24, "", 1, 2, 3, 4, 5, "Necklace", false, PARAMS.SIGHT_RANGE),
//        CONCEALMENT(12, "", 5, 12, 25, 50, 110, "Possessed Ring", PARAMS.CONCEALMENT),

        // MAGIC_MASTERIES(12, "", 4, 8, 15, 25, 40, "Power Ring;Necklace",
        // VALUE_GROUP.MAGIC
        // .getParams()),
        // OFFENSE_MASTERIES(8, "", 5, 10, 18, 30, 50, "Bracer",
        // VALUE_GROUP.ANY_OFFENSE
        // .getParams()),
        // DEFENSE_MASTERIES(8, "", 5, 10, 18, 30, 50, "Signet",
        // VALUE_GROUP.ANY_DEFENSE
        // .getParams()),
        // WEAPON_MASTERIES(10, "", 5, 10, 18, 30, 50, "Bracer",
        // VALUE_GROUP.WEAPON
        // .getParams()),
        //
        // MISC_MASTERIES(14, "", 5, 10, 18, 30, 50, "Noble Ring",
        // ValuePages.MASTERIES_MISC),

        ;
        Integer cost_mod;        // *amount?
        private final Integer[] integers;
        private final PARAMETER[] params;
        private final String imageVariant;
        private final int costBase;
        private final String jewelryTypes;
        private boolean doubleAmulet;

        JEWELRY_ITEM_TRAIT(int costBase, String letter, Integer l1, Integer l2,
                           Integer l3, Integer l4, Integer l5, String jewelryTypes,
                           boolean doubleAmulet, PARAMETER... p) {
            this(costBase, letter, l1, l2, l3, l4, l5, jewelryTypes, p);
            this.doubleAmulet = doubleAmulet;
        }

        JEWELRY_ITEM_TRAIT(int costBase, String letter, Integer l1, Integer l2,
                           Integer l3, Integer l4, Integer l5, String jewelryTypes,
                           PARAMETER... p) {
            imageVariant = (letter);
            this.jewelryTypes = (jewelryTypes);
            integers = (new Integer[]{l1, l2, l3, l4, l5,});
            this.costBase = costBase;
            this.params = (p);
            doubleAmulet = true;
        }

        public boolean isDoubleAmulet() {
            return doubleAmulet;
        }

        public String toString() {
            return StringMaster.format(super.toString());
        }

        public PARAMETER[] getParams() {
            return params;
        }

        public Integer[] getIntegers() {
            return integers;
        }

        public String getImageSuffix() {
            return imageVariant;
        }

        public int getCostBase() {
            return costBase;
        }

        public String getJewelryTypes() {
            return jewelryTypes;
        }

    }

    public enum JEWELRY_PASSIVE_ENCHANTMENT { // for amulets only?
        GRACE(400, true, UnitEnums.STANDARD_PASSIVES.DEXTEROUS, "_b", "", "Necklace"),
        SNAKE(400, true, UnitEnums.STANDARD_PASSIVES.FIRST_STRIKE, "_c", "", "Necklace"),
        FLIGHT(400, true, UnitEnums.STANDARD_PASSIVES.FLYING, "_b", "", "Pendant"),
        SWIFTNESS(400, true, UnitEnums.STANDARD_PASSIVES.NO_RETALIATION, "_b", "", "Amulet"),
        DARKVISION(400, true, UnitEnums.STANDARD_PASSIVES.DARKVISION, "_b", "", "Amulet"),
        PHANTOMS(400, true, UnitEnums.STANDARD_PASSIVES.IMMATERIAL, "_b", "", "Pendant"),
        VIGILANCE(400, true, UnitEnums.STANDARD_PASSIVES.VIGILANCE, "_d", "", "Pendant"),
        VAMPIRISM(15, true, "Life Steal", 15, 25, 50, 75, 100, "_c", "", "Pendant"),;
        private final String iconLetter;
        private final String iconNumber;
        private boolean leveled;
        private final boolean ring;
        private Integer[] integers;
        private final int costBase;
        private PROPERTY prop;
        private String value;
        private final String itemTypes;

        JEWELRY_PASSIVE_ENCHANTMENT(int costBase, boolean ring,
                                    STANDARD_PASSIVES value, String iconLetter, String iconNumber,
                                    String itemTypes) {
            this.itemTypes = itemTypes;
            this.ring = ring;
            this.costBase = costBase;
            this.iconLetter = iconLetter;
            this.iconNumber = iconNumber;
            this.leveled = false;
            if (value != null) {
                this.value = value.getName();
            }
            this.prop = G_PROPS.STANDARD_PASSIVES;

        }

        JEWELRY_PASSIVE_ENCHANTMENT(int costBase, boolean ring, String value,
                                    Integer l1, Integer l2, Integer l3, Integer l4, Integer l5,
                                    String iconLetter, String iconNumber, String itemTypes) {
            this(costBase, ring, null, iconLetter, iconNumber, itemTypes);
            this.leveled = true;
            this.integers = (new Integer[]{l1, l2, l3, l4, l5,});
            this.value = value;
            this.prop = G_PROPS.PASSIVES;
        }

        public String toString() {
            return StringMaster.format(super.toString());
        }

        public String getIconVersionNumber() {
            return iconNumber;
        }

        public String getItemTypes() {
            return itemTypes;
        }

        public String getIconVariantLetter() {
            return iconLetter;
        }

        public boolean isRing() {
            return ring;
        }

        public boolean isLeveled() {
            return leveled;
        }

        public String getValue(ITEM_LEVEL level) {
            if (!leveled) {
                return value;
            }
            return value
             + StringMaster.wrapInParenthesis(integers[level.getLevel()]
             + "");
        }

        public PROPERTY getProp() {
            return prop;
        }

        public int getCostBase() {
            return costBase;
        }
    }

    public enum ITEM_LEVEL {
        MINOR(100, 0, 10, ""),
        LESSER(150,1, 25, ""),
        COMMON(225,2, 50, ""),
        GREATER(333,3, 125, ""),
        LEGENDARY(500,4, 300, "");

        private final int power;
        private final int level;
        private final int costFactor;

        ITEM_LEVEL(int power, int n, int costFactor, String iconSuffix) {
            this.costFactor = costFactor;
            this.level = n;
            this.power = power;
        }

        public int getPower() {
            return power;
        }

        public int getLevel() {
            return level;
        }

        public int getCostFactor() {
            return costFactor;
        }

        public String toString() {
            return StringMaster.format(super.toString());
        }
    }

    public enum MAGIC_ITEM_PASSIVE_TRAIT {
        WARP, VOID, AETHER,

    }

    public enum POTION_LEVEL {
        MINOR(1, 100, "", ItemEnums.QUALITY_LEVEL.INFERIOR),
        AVERAGE(2, 225, "1", ItemEnums.QUALITY_LEVEL.NORMAL),
        GREATER(3, 400, "2", ItemEnums.QUALITY_LEVEL.SUPERIOR);

        private final int charges;
        private final String imgCode;
        private final int cost;
        private final QUALITY_LEVEL quality;

        POTION_LEVEL(int charges, int cost, String imgCode,
                     QUALITY_LEVEL quality) {
            this.charges = charges;
            this.imgCode = imgCode;
            this.cost = cost;
            this.quality = quality;
        }

        public QUALITY_LEVEL getQuality() {
            return quality;
        }

        public int getCharges() {
            return charges;
        }

        public String getName() {
            // if (this == AVERAGE)
            // return "";
            return StringMaster.format(name());

        }

        public String getImgCode() {
            return imgCode;
        }

        public int getCost() {
            return cost;
        }
    }


}
