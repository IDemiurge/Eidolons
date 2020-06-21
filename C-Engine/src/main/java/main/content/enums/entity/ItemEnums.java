package main.content.enums.entity;

import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.GenericEnums.RESIST_GRADE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.system.auxiliary.StringMaster;

import java.awt.*;
import java.util.Map;

/**
 * Created by JustMe on 2/14/2017.
 */
public class ItemEnums {
    public enum ARMOR_GROUP {
        CLOTH, LEATHER, CHAIN, PLATE

    }

    public enum ARMOR_TYPE {
        LIGHT, HEAVY,

    }

    public enum GARMENT_GROUP {
        COMMON, SPECIAL, LEGENDARY, CUSTOM
    }

    public enum GARMENT_TYPE {
        BOOTS, GLOVES, CLOAK, HELMET
    }

    public enum ITEM_GROUP {
        POTIONS, CONCOCTIONS, COATING, KEY, GOLD_PACK
        // THROWING, TRAP, GEMSTONE

    }

    public enum ITEM_MATERIAL_GROUP {
        METAL, WOOD, LEATHER, CLOTH, BONE, STONE, NATURAL, CRYSTAL
    }

    public enum ITEM_RARITY {
        //don't change the order!!!
        EXCEPTIONAL(5),
        RARE(15),
        UNCOMMON(25),
        COMMON(55),;
        int chance;

        ITEM_RARITY(int chance) {
            this.chance = chance;
        }

        public int getChance() {
            return chance;
        }
    }

    public enum ITEM_SHOP_CATEGORY {
        COMMON, RARE, SPECIAL, LEGENDARY
    }

    public enum ITEM_SLOT {
        MAIN_HAND(G_PROPS.MAIN_HAND_ITEM){
            public ITEM_SLOT getReserve() {
                return RESERVE_MAIN_HAND;
            }
        },
        ARMOR(G_PROPS.ARMOR_ITEM),
        OFF_HAND(G_PROPS.OFF_HAND_ITEM){
            public ITEM_SLOT getReserve() {
                return RESERVE_OFF_HAND;
            }
        },

        RESERVE_MAIN_HAND(G_PROPS.RESERVE_MAIN_HAND_ITEM),
        RESERVE_OFF_HAND(G_PROPS.RESERVE_OFF_HAND_ITEM),

        // NECKLACE(G_PROPS.MAIN_HAND_ITEM),
        // RING_1(G_PROPS.ARMOR_ITEM),
        // RING_2(G_PROPS.OFF_HAND_ITEM)
        ;

        private final PROPERTY prop;

        ITEM_SLOT(PROPERTY prop) {
            this.prop = prop;
        }

        public PROPERTY getProp() {
            return prop;
        }

        public ITEM_SLOT getReserve() {
            return null;
        }
    }

    public enum ITEM_TYPE {
        ALCHEMY, MAGICAL, THROWING, SPECIAL_ITEM,

    }

    public enum JEWELRY_GROUP {
        EMPTY, PASSIVE, PARAMETER, ACTIVE,ATTRIBUTE
    }

    public enum JEWELRY_TYPE {
        RING, AMULET
    }

    public enum MATERIAL {

        RED_OAK(1, 2, 0.5, 5, 45, ITEM_MATERIAL_GROUP.WOOD, -1, null),
        IRONWOOD(2, 2, 0.75, 12, 85, ITEM_MATERIAL_GROUP.WOOD, 1, null),
        BLACKWOOD(4, 4, 1.25, 25, 130, ITEM_MATERIAL_GROUP.WOOD, 2, null),
        PALEWOOD(5, 6, 1, 50, 225, ITEM_MATERIAL_GROUP.WOOD, 3, null),
        BILEWOOD(6, 3, 2.5, 150, 175, ITEM_MATERIAL_GROUP.WOOD, 4, GenericEnums.DAMAGE_TYPE.ACID),
        WAILWOOD(5, 3, 2, 190, 350, ITEM_MATERIAL_GROUP.WOOD, 4, GenericEnums.DAMAGE_TYPE.SONIC),
        FEYWOOD(5, 2, 1, 250, 450, ITEM_MATERIAL_GROUP.WOOD, 4, GenericEnums.DAMAGE_TYPE.MAGICAL),

        COTTON(1, 0.75, 8, 75, ITEM_MATERIAL_GROUP.CLOTH, -1),
        SILK(2, 0.5, 30, 250, ITEM_MATERIAL_GROUP.CLOTH, 1),

		/*
         * "Bludgeoning(nor;nor);Slashing(nor;nor);Piercing(nor;nor);" +
		 * "Acid(nor;nor);Fire(nor;nor);Cold(nor;nor);Lightning(nor;nor);" +
		 * "Sonic(nor;nor);Light(nor;nor);"),
		 */

        // WANDS, ...?
        // MAGICAL MATERIALS?
        IVORY(1, 2, 30, 150, ITEM_MATERIAL_GROUP.BONE, -1),
        BLACK_BONE(3, 3, 95, 325, ITEM_MATERIAL_GROUP.BONE, -1),
        MAN_BONE(4, 2, 185, 500, ITEM_MATERIAL_GROUP.BONE, -1),
        DRAGON_BONE(8, 3, 335, 850, ITEM_MATERIAL_GROUP.BONE, -1),
        // DEMON BONE
        // LIVING BONE

        THIN_LEATHER(1, 3, 0.5, 5, 60, ITEM_MATERIAL_GROUP.LEATHER, -1, null),
        TOUGH_LEATHER(2, 4, 0.75, 12, 60, ITEM_MATERIAL_GROUP.LEATHER, 1, null),
        THICK_LEATHER(4, 3, 1, 25, 80, ITEM_MATERIAL_GROUP.LEATHER, 2, null),
        FUR(2, 4, 2, 50, 50, ITEM_MATERIAL_GROUP.LEATHER, 3, null),
        LIZARD_SKIN(6, 7, 0.5, 75, 120, ITEM_MATERIAL_GROUP.LEATHER, 3, null),
        TROLL_SKIN(8, 10, 2, 100, 100, ITEM_MATERIAL_GROUP.LEATHER, 3, null),
        DRAGONHIDE(9, 7, 1, 250, 250, ITEM_MATERIAL_GROUP.LEATHER, 3, null),

        // TROLL_SKIN(8, 3, 100,ITEM_MATERIAL_GROUP.METAL, -1), // brown
        // BASILISK_SKIN(3, 1, 30,ITEM_MATERIAL_GROUP.METAL, -1), // dark GREEN
        // NAGA_SKIN(4, 1, 50,ITEM_MATERIAL_GROUP.METAL, -1), //teal
        // DEMON_SKIN(10, 2, 200,ITEM_MATERIAL_GROUP.METAL, -1), //reddish

        // GREEN
        GRANITE(4, 8, 30, 50, ITEM_MATERIAL_GROUP.STONE, -1),
        ONYX(2, 5, 15, 150, ITEM_MATERIAL_GROUP.STONE, -1),
        OBSIDIAN(3, 4, 50, 300, ITEM_MATERIAL_GROUP.STONE, 1),
        CRYSTAL(5, 3, 125, 600, ITEM_MATERIAL_GROUP.CRYSTAL, 2),
        SOULSTONE(8, 2, 250, 1000, ITEM_MATERIAL_GROUP.STONE, 3),
        STAR_EMBER(12, 2, 450, 1450, ITEM_MATERIAL_GROUP.CRYSTAL, 4),

        SILVER(4, 2, 50, 125, ITEM_MATERIAL_GROUP.METAL, -1),
        GOLD(8, 3, 125, 100, ITEM_MATERIAL_GROUP.METAL, -1),

        COPPER(1, 2, 1.5, 8, 50, ITEM_MATERIAL_GROUP.METAL, -1, null),
        BRASS(2, 2, 2, 14, 50, ITEM_MATERIAL_GROUP.METAL, -1, null),
        BRONZE(3, 3, 1.5, 40, 60, ITEM_MATERIAL_GROUP.METAL, 1, null),
        IRON(4, 3, 2.5, 40, 50, ITEM_MATERIAL_GROUP.METAL, 1, null),
        STEEL(6, 5, 2, 100, 75, ITEM_MATERIAL_GROUP.METAL, 2, null),
        MITHRIL(9, 7, 1.5, 200, 150, ITEM_MATERIAL_GROUP.METAL, 3, null),
        PLATINUM(12, 16, 3, 250, 50, ITEM_MATERIAL_GROUP.METAL, 2, null),
        ADAMANTIUM(16, 18, 4, 325, 100, ITEM_MATERIAL_GROUP.METAL, 1, GenericEnums.DAMAGE_TYPE.FIRE),
        METEORITE(14, 12, 1.5, 400, 150, ITEM_MATERIAL_GROUP.METAL, 3, GenericEnums.DAMAGE_TYPE.LIGHTNING),
        // AURUM

        BRIGHT_STEEL(5, 4, 2, 180, 200, ITEM_MATERIAL_GROUP.METAL, 2, GenericEnums.DAMAGE_TYPE.LIGHT),
        DEFILED_STEEL(6, 2, 3.5, 195, 125, ITEM_MATERIAL_GROUP.METAL, -1, GenericEnums.DAMAGE_TYPE.ACID),

        DARK_STEEL(5, 4, 2, 150, 150, ITEM_MATERIAL_GROUP.METAL, 2, GenericEnums.DAMAGE_TYPE.SHADOW),
        WRAITH_STEEL(4, 2, 0.5, 165, 220, ITEM_MATERIAL_GROUP.METAL, -1, GenericEnums.DAMAGE_TYPE.DEATH),
        PALE_STEEL(6, 2, 1.5, 150, 180, ITEM_MATERIAL_GROUP.METAL, 3, GenericEnums.DAMAGE_TYPE.COLD),
        WARP_STEEL(4, 3, 1.5, 190, 250, ITEM_MATERIAL_GROUP.METAL, 1, GenericEnums.DAMAGE_TYPE.PSIONIC),
        DEMON_STEEL(5, 3, 2.5, 200, 200, ITEM_MATERIAL_GROUP.METAL, 1, GenericEnums.DAMAGE_TYPE.CHAOS),
        MOON_SILVER(3, 5, 1, 175, 275, ITEM_MATERIAL_GROUP.METAL, 2, GenericEnums.DAMAGE_TYPE.HOLY),
        ELDRITCH_STEEL(5, 3, 1.5, 250, 200, ITEM_MATERIAL_GROUP.METAL, 1, GenericEnums.DAMAGE_TYPE.ARCANE),

        // MOON_SILVER(6, 2, 85, ITEM_MATERIAL_GROUP.METAL, DAMAGE_TYPE.HOLY,
        // -1),
        // DARK_STEEL(10, 3, 125, ITEM_MATERIAL_GROUP.METAL, DAMAGE_TYPE.SHADOW,
        // -1),
        // WARP_STEEL(10, 3, 125, ITEM_MATERIAL_GROUP.METAL, DAMAGE_TYPE.SHADOW,
        // -1),
        // // CRYSTAL(22, 1, 500, ITEM_MATERIAL_GROUP.CRYSTAL,
        // DAMAGE_TYPE.ARCANE, -1),
        // COLD_IRON(20, 4, 325, ITEM_MATERIAL_GROUP.METAL, DAMAGE_TYPE.WATER,
        // -1),
        // RED_IRON(22, 4, 400, ITEM_MATERIAL_GROUP.METAL, DAMAGE_TYPE.FIRE,
        // -1),

        // DRAGON_SKIN(14, 2, 300,ITEM_MATERIAL_GROUP.METAL, -1), //bluish
        /**
         * non metal LIZARD_SKIN TROLL_SKIN DRAGON_SKIN BONE WARP CLOTH VOID
         * CLOTH ELDRTICH CLOTH WOOD
         */

        //
        // COTTON,
        // SILK,
        // BLACK_SILK,
        // SPIDER_SILK,
        // WARP_CLOTH,
        // WEAVE,

        PUNY(1, 1, 25, 50, ITEM_MATERIAL_GROUP.NATURAL, 1),
        PETTY(3, 2, 45, 90, ITEM_MATERIAL_GROUP.NATURAL, 2),
        AVERAGE(5, 3, 95, 150, ITEM_MATERIAL_GROUP.NATURAL, -1),
        SIZABLE(8, 5, 165, 225, ITEM_MATERIAL_GROUP.NATURAL, 3),
        DIRE(12, 6, 225, 325, ITEM_MATERIAL_GROUP.NATURAL, 4),
        HUGE(14, 8, 275, 375, ITEM_MATERIAL_GROUP.NATURAL, 5),
        FEARSOME(18, 7, 375, 450, ITEM_MATERIAL_GROUP.NATURAL, 5),
        MONSTROUS(26, 9, 500, 650, ITEM_MATERIAL_GROUP.NATURAL, 6),
        BEHEMOTH(45, 15, 1500, 650, ITEM_MATERIAL_GROUP.NATURAL, 7), // sell
        // as
        // trophies...

        ;
public static final float GLOBAL_DURABILITY_MODIFIER = 1.75f;
        boolean magical;
        MATERIAL_TYPE type;
        private int durabilityMod;
        // "
        private ITEM_MATERIAL_GROUP group;
        private int code;
        private Color color;
        private final String name;
        private final int modifier;
        private final double weight;
        private int hardness;
        private final int enchantmentCapacity;
        private final int cost;
        private DAMAGE_TYPE dmg_type;
        private Map<DAMAGE_TYPE, RESIST_GRADE> selfDamageGradeMap;
        private Map<DAMAGE_TYPE, RESIST_GRADE> resistGradeMap;

        MATERIAL(int modifier, int durability, double weight, int cost, int magic,
                 ITEM_MATERIAL_GROUP g, int code, DAMAGE_TYPE dmg_type) {
            this(modifier, weight, cost, magic, g, dmg_type, code);
            this.durabilityMod = Math.round(durability * GLOBAL_DURABILITY_MODIFIER);
            this.group = g;
            magical = dmg_type != null;
        }

        MATERIAL(int modifier, double weight, int cost, int magic, ITEM_MATERIAL_GROUP g, int code) {
            this(modifier, weight, cost, magic, code);
            this.group = g;

        }

        MATERIAL(int modifier, double weight, int cost, int magic, ITEM_MATERIAL_GROUP g,
                 DAMAGE_TYPE dmg_type, int code) {
            this(modifier, weight, cost, magic, code);
            this.dmg_type = dmg_type;
            this.group = g;
        }

        MATERIAL(int modifier, double weight, int cost, int magic, int code) {
            this.name = StringMaster.format(name());
            this.modifier = modifier;
            this.weight = weight;
            this.cost = cost;
            this.enchantmentCapacity = magic;
            this.code = code;
        }

        public MATERIAL_TYPE getType() {
            return type;
        }

        // TODO update!
        public void setType(MATERIAL_TYPE type) {
            this.type = type;
        }

        public RESIST_GRADE getResistGrade(DAMAGE_TYPE dmg_type) {
            return resistGradeMap.get(dmg_type);
        }

        public RESIST_GRADE getSelfDamageGrade(DAMAGE_TYPE dmg_type) {
            return selfDamageGradeMap.get(dmg_type);
        }

        public String toString() {
            return name;
        }

        public String getName() {
            return name;
        }

        public int getModifier() {
            return modifier;
        }

        public double getWeight() {
            return weight;
        }

        public int getCost() {
            return cost;
        }

        public DAMAGE_TYPE getDmg_type() {
            return dmg_type;
        }

        public synchronized Color getColor() {
            return color;
        }

        public synchronized boolean isMagical() {
            return magical;
        }

        public synchronized void setMagical(boolean magical) {
            this.magical = magical;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getEnchantmentCapacity() {
            return enchantmentCapacity;
        }

        public int getDurabilityMod() {
            return durabilityMod;
        }

        public ITEM_MATERIAL_GROUP getGroup() {
            return group;
        }

        public int getHardness() {
            return hardness;
        }

        public void setHardness(int hardness) {
            this.hardness = hardness;
        }

        public void setSelfDamageGradeMap(Map<DAMAGE_TYPE, RESIST_GRADE> map) {
            selfDamageGradeMap = map;
        }

        public void setResistGradeMap(Map<DAMAGE_TYPE, RESIST_GRADE> map) {
            resistGradeMap = map;
        }

    }

    public enum MATERIAL_TYPE {
        METAL, STONE, WOOD, BONE, CRYSTAL, CLOTH, FLESH,
    }

    public enum QUALITY_LEVEL {
        ANCIENT(60), OLD(80),

        DAMAGED(50), INFERIOR(75), NORMAL(100), SUPERIOR(125), SUPERB(150), MASTERPIECE(200);
        private int durabilityMod;
        private int costMod = 100;

        QUALITY_LEVEL(int durability) {
            this.setDurability(durability);
            if (durability != 100) {
                costMod = 100 - (100 - durability) * 2 / 3;
            }
        }

        public String toString() {
            return StringMaster.format(name());
        }

        public int getDurabilityMod() {
            return durabilityMod;
        }

        public void setDurability(int durability) {
            this.durabilityMod = durability;
        }

        public int getCostMod() {
            return costMod;
        }

        public void setCostMod(int costMod) {
            this.costMod = costMod;
        }

        public String getName() {
            return StringMaster.format(name());
        }
    }

    public enum WEAPON_CLASS {
        DOUBLE, TWO_HANDED, MAIN_HAND_ONLY, OFF_HAND, OFF_HAND_ONLY, QUICK_ITEM

    }

    public enum WEAPON_GROUP {
        AXES, POLLAXES, DAGGERS, SHORT_SWORDS, LONG_SWORDS, GREAT_SWORDS,
        // THROWING_KNIVES

        SCYTHES,
        STAVES("Staff"),
        SPEARS,
        CLUBS,
        MACES,
        HAMMERS("Battle Hammer"),
        FLAILS,
        WANDS,
        ORBS,
        TOWER_SHIELDS,
        SHIELDS,
        BUCKLERS,
        BOWS,
        CROSSBOWS,
        ARROWS,
        BOLTS,

        FEET,
        FISTS,
        CLAWS,
        FANGS,
        TAILS,
        HORNS,
        INSECTOID,
        HOOVES,
        BEAKS,
        EYES,
        FORCE,
        MAWS,
        NATURAL,
        FIREARMS,
        ;
        private String type;

        WEAPON_GROUP(String type) {
            this.type = type;
        }

        WEAPON_GROUP() {

        }

        public String getDefaultType() {
            if (type != null) {
                return type;
            }
            return name().substring(0, name().length() - 1);
        }
    }

    public enum WEAPON_SIZE {
        TINY, SMALL, MEDIUM, LARGE, HUGE
    }

    public enum WEAPON_TYPE {
        BLADE, AXE, BLUNT, POLE_ARM, MAGICAL, SHIELD, RANGED, AMMO, NATURAL

    }
}
