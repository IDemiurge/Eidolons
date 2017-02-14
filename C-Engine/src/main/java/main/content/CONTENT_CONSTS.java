package main.content;

import main.content.enums.STD_MODES;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.ability.construct.VarHolder;
import main.data.ability.construct.VariableManager;
import main.entity.Ref.KEYS;
import main.game.battlefield.Coordinates.UNIT_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.awt.*;
import java.util.Map;

public class CONTENT_CONSTS {

    public enum MASTERY_RANK {
        NONE(0),
        NOVICE(1),
        APPRENTICE(5),
        ADEPT(10),
        ADVANCED(15),
        SPECIALIST(20),
        EXPERT(30),
        MASTER(40),
        GRAND_MASTER(50),;
        private int masteryReq;

        MASTERY_RANK(int masteryReq) {
            this.masteryReq = masteryReq;
        }

        public int getMasteryReq() {
            return masteryReq;
        }

        public String getName() {
            return StringMaster.getWellFormattedString(toString());
        }
    }

    public enum STANDARD_WOUNDS {
        MISSING_RIGHT_HAND, MISSING_LEFT_HAND, MISSING_RIGHT_EYE, MISSING_LEFT_EYE,
    }

    public enum STANDARD_HERO_TRAITS {
        VAGABOND, OUTLAW, DISCOMMUNICATED, DISGRACED, FUGITIVE, MERCENARY, ARTIST, MINSTREL,

        // AFFILIATION?

    }

    public enum SUBDUNGEON_TYPE {
        CAVE, HIVE, DUNGEON, CASTLE, SEWER, HELL, ASTRAL, ARCANE, CRYPT, DEN, BARROW, RUIN, HOUSE,
    }

    public enum SUBLEVEL_TYPE {
        COMMON, PRE_BOSS, BOSS, SECRET, TRANSIT, FALSE_LEVEL
    }

    public enum GARMENT_TYPE {
        BOOTS, GLOVES, CLOAK, HELMET
    }

    public enum GARMENT_GROUP {
        COMMON, SPECIAL, LEGENDARY, CUSTOM
    }

    public enum RETAIN_CONDITIONS {
        CASTER_ALIVE, CASTER_FOCUS_REQ, CASTER_CONSCIOUS, TARGET_MATCHES_TARGETING_FILTER, // DISTANCE/FACING/VISION?

    }

    public enum COLOR_THEME {
        BLUE, GREEN, RED, DARK, LIGHT, YELLOW, PURPLE {
            public String getSuffix() {
                return " v";
            }
        };

        COLOR_THEME() {
            // path suffixes
        }

        public String getSuffix() {
            return (" " + name().charAt(0)).toLowerCase();
        }
    }

    public enum BUFF_TYPE {
        RULES, SPELL, PASSIVE, STANDARD,
        // buff-debuff into SPELL by default...
    }

    public enum WORKSPACE_GROUP {
        FOCUS, FIX, TEST, IMPLEMENT, DESIGN, POLISH, COMPLETE,
    }

    public enum ITEM_SHOP_CATEGORY {
        COMMON, RARE, SPECIAL, LEGENDARY
    }

    public enum MAP_FILL_TEMPLATE {
        FOREST("Tree Sapling(2);Oak(2);Shrub(3);", "Forest Crags(1);Mossy Rocks(1);Fallen Tree(1);Mossy Boulder(1);Sleek Rock(1);"),
        SWAMP("Dead Tree(3);Forest Crags(1);Mossy Rocks(1);Tree Sapling(1); Shrub(1);", "Forest Crags(1);Mossy Rocks(1);Fallen Tree(1);Mossy Boulder(1);Sleek Rock(1);"),
        DARK_FOREST("Dead Tree(2);Tree Sapling(1);Oak(2);Shrub(3);", "Forest Crags(2);Mossy Rocks(1);Fallen Tree(2);Mossy Boulder(1);Sleek Rock(2);"),
        DEAD_FOREST("Dead Tree(3);Tree Sapling(1);Oak(1);Shrub(1);", "Forest Crags(1);Mossy Rocks(1);Tree Stump(2);Fallen Tree(2);Mossy Boulder(1);Sleek Rock(3);"),
        // RUINS("Tree Sapling(2);Oak(2);Shrub(3);",
        // "Fallen Tree(1);Mossy Boulder(1);Sleek Rock(1);"),
        // ROCKS("Tree Sapling(2);Oak(2);Shrub(3);",
        // "Fallen Tree(1);Mossy Boulder(1);Sleek Rock(1);"),
        ;
        private String centerObjects;
        private String peripheryObjects;

        MAP_FILL_TEMPLATE(String centerObjects, String peripheryObjects) {
            this.centerObjects = centerObjects;
            this.peripheryObjects = peripheryObjects;
        }

        public String getCenterObjects() {
            return centerObjects;
        }

        public String getPeripheryObjects() {
            return peripheryObjects;
        }
    }

    public enum DUNGEON_MAP_TEMPLATE {
        COAST("Water(4);", MAP_BACKGROUND.PIRATE_BAY), // one side cut off water
        // halfway thru?
        PLAINS("Shrub(1);Mossy Boulder(2);Oak(2);", MAP_BACKGROUND.CAVE),
        SWAMP("Fallen Tree(1);Sleek Rock(2);Dead Tree(3);", MAP_BACKGROUND.CAVE),

        FOREST("Tree Sapling(2);Oak(2);Fallen Tree(1);Shrub(3);Mossy Boulder(1);Sleek Rock(1);", MAP_BACKGROUND.CAVE),
        DEAD_FOREST("Fallen Tree(1);Shrub(2);Mossy Boulder(1);Sleek Rock(1);Dead Tree(4);", MAP_BACKGROUND.DARK_FOREST),
        DARK_FOREST("Fallen Tree(1);Shrub(2);Mossy Boulder(1);Sleek Rock(1);Oak(2);Dead Tree(3);", MAP_BACKGROUND.DARK_FOREST),
        CEMETARY("Gravestone(4);", MAP_BACKGROUND.CEMETARY),
        CEMETARY_WOODS("Gravestone(2);Sleek Rock(1);Mossy Boulder(1);Dead Tree(2);", MAP_BACKGROUND.CEMETARY),
        DUNGEON_HALL("Marble Column(4);", MAP_BACKGROUND.FORGOTTEN_CITY),

        DUNGEON("Stalactite(3);Stalagmite(4);", MAP_BACKGROUND.CAVE),

        HALL_MAGE("Marble Column(6);", MAP_BACKGROUND.FORGOTTEN_CITY),
        HALL_DARK("Ruined Column(3);Ruined Gateway(2);Ruined Structure(2);", MAP_BACKGROUND.DARK_CASTLE),
        CAMP("Fallen Tree(2);Barricade(3);", MAP_BACKGROUND.PIRATE_BAY),;
        private String objects;
        private MAP_BACKGROUND bg;

        DUNGEON_MAP_TEMPLATE(String objects, MAP_BACKGROUND bg) {
            this.objects = objects; // (x) - minimum x/2, max x.
            this.bg = bg;
        }

        public String getObjects() {
            return objects;
        }

        public String getBackground() {
            return bg.getBackgroundFilePath();
        }

        public MAP_BACKGROUND getBg() {
            return bg;
        }

    }

    public enum DUNGEON_MAP_MODIFIER {
        CULTIST_HIDEOUT("Altar(2)", MAP_BACKGROUND.CAVE),
        HALL("Marble Column(3);", MAP_BACKGROUND.DARK_CASTLE),
        GRAVEYARD("Gravestone(4);"
                // "Desecrated Grave(2);Fresh Grave(2);Gravestone(2);Statue(2);"
                , MAP_BACKGROUND.CEMETARY),
        RUINS("Ruined Structure(2);Ruined Column(2);Ruined Gateway(2)", MAP_BACKGROUND.RUINS),
        DEEP_WOOD("Shrub(2);Ancient Oak(3);", MAP_BACKGROUND.RUINS),
        ROCKY("Mossy Boulder(2);Sleek Rock(2);", MAP_BACKGROUND.SHEOTH),
        CAVERN("Stalactite(2);Stalagmite(3);", MAP_BACKGROUND.CAVE),;
        private String objects;
        private MAP_BACKGROUND bg;
        private String presetObjects;

        DUNGEON_MAP_MODIFIER(String objects, MAP_BACKGROUND bg, String presetObjects) {
            this.objects = objects;
            this.bg = bg;
            this.presetObjects = presetObjects;
        }

        DUNGEON_MAP_MODIFIER(String objects, MAP_BACKGROUND bg) {
            this(objects, bg, null);
        }

        public String getObjects() {
            return objects;
        }

        public String getBackground() {
            return bg.getBackgroundFilePath();
        }

        public MAP_BACKGROUND getBg() {
            return bg;
        }

        public String getPresetObjects() {
            return presetObjects;
        }
    }

    public enum BF_OBJECT_TAGS {
        INDESTRUCTIBLE,
        PASSABLE,
        SUMMONED,
        COLLAPSABLE,
        ASSYMETRICAL,
        LANDSCAPE,
        OVERLAYING,
        WATER,
        HUGE,
        LARGE
    }

    public enum BF_OBJECT_TYPE {
        NATURAL, STRUCTURE, PROP, SPECIAL
    }

    public enum BF_OBJECT_GROUP {
        WALL, COLUMNS, RUINS, CONSTRUCT, GATEWAY, GRAVES,

        WINDOWS, MAGICAL, HANGING, INTERIOR, STATUES,

        ENTRANCE, TRAP, DOOR, LIGHT_EMITTER, CONTAINER, TREASURE,

        DUNGEON, WATER, TREES, ROCKS, VEGETATION, REMAINS, CRYSTAL,;
    }

    public enum BF_OBJECT_SIZE {

        TINY, SMALL, MEDIUM, LARGE, HUGE
    }

    public enum OBJECT_ARMOR_TYPE {
        STONE, WOOD, METAL, FLESH, BONE, CRYSTAL, AETHER, ETHEREAL,;

        public ITEM_MATERIAL_GROUP getGroup() {
            switch (this) {
                case STONE:
                    return ITEM_MATERIAL_GROUP.STONE;
                case BONE:
                    return ITEM_MATERIAL_GROUP.BONE;
                case FLESH:
                    return ITEM_MATERIAL_GROUP.NATURAL;
                case METAL:
                    return ITEM_MATERIAL_GROUP.METAL;
                case WOOD:
                    return ITEM_MATERIAL_GROUP.WOOD;
                case CRYSTAL:
                    return ITEM_MATERIAL_GROUP.CRYSTAL;
                case AETHER:
                    return ITEM_MATERIAL_GROUP.CRYSTAL;
                case ETHEREAL:
                    break;
            }
            return null;
        }

    }

    public enum SUBPALETTE {
        LIGHT_EMITTERS, CONTAINERS, DOORS, TRAPS, GRAVES, TREES, ROCKS,

    }

    public enum PALETTE {
        INTERIOR, DUNGEON, NATURE, DARK, DEATH, CHAOS, ARCANE,
    }

    public enum DIMENSION {
        T1_2, T1_3, T1_4, T1_5, T1_7, T1_10, T2_3, T3_4,

        W2_1, W3_1, W4_1, W5_1, W7_1, W10_1, W3_2, W4_3,
    }

    public enum BF_OBJ_WEIGHT {
        TINY,

        COLOSSAL,
    }

    public enum BF_OBJ_QUALITY {
        TOUGH,
        CRUMBLING,
        BRITTLE,
        DURABLE,
        RESISTANT,
        ARMORED,
        THICK,
        TOUGH_II,
        CRUMBLING_II,
        BRITTLE_II,
        DURABLE_II,
        RESISTANT_II,
        ARMORED_II,
        THICK_II,
        TOUGH_III,
        CRUMBLING_III,
        BRITTLE_III,
        DURABLE_III,
        RESISTANT_III,
        ARMORED_III,
        THICK_III,
    }

    public enum BF_OBJ_MATERIAL {
        RED_OAK,
        IRONWOOD,
        BLACKWOOD,
        PALEWOOD,
        BILEWOOD,
        WAILWOOD,
        FEYWOOD,
        COTTON,
        SILK,
        IVORY,
        BLACK_BONE,
        MAN_BONE,
        DRAGON_BONE,
        THIN_LEATHER,
        TOUGH_LEATHER,
        THICK_LEATHER,
        FUR,
        LIZARD_SKIN,
        TROLL_SKIN,
        DRAGONHIDE,
        GRANITE,
        MARBLE,
        ONYX,
        OBSIDIAN,
        CRYSTAL,
        SOULSTONE,
        STAR_EMBER,
        SILVER,
        GOLD,
        COPPER,
        BRASS,
        BRONZE,
        IRON,
        STEEL,
        MITHRIL,
        PLATINUM,
        ADAMANTIUM,
        METEORITE,
        BRIGHT_STEEL,
        DEFILED_STEEL,
        DARK_STEEL,
        WRAITH_STEEL,
        PALE_STEEL,
        WARP_STEEL,
        DEMON_STEEL,
        MOON_SILVER,
        ELDRITCH_STEEL
    }

    /*
     * 24th of April, Hour of Magic
     */
    public enum ROLL_TYPES implements VarHolder {
        MIND_AFFECTING("Willpower"),
        FAITH("Faith"),
        REFLEX("Reflex"),
        ACCURACY("Accuracy"),
        REACTION("Reaction", true),
        BODY_STRENGTH("Body Strength"),
        QUICK_WIT("Quick Wit"),
        FORTITUDE("Fortitude"),
        DISARM("Disarm"),
        MASS("Mass"),
        DETECTION("Detection"),
        STEALTH("Stealth"),
        DEFENSE("Defensive"),
        IMMATERIAL("Immaterial"),
        DISPEL("Dispel"),
        UNLOCK("Unlock"),
        DISARM_TRAP("Disarm Trap"),
        FORCE("Force"),;
        boolean logToTop;
        private String name;

        ROLL_TYPES(String s, boolean logToTop) {
            this(s);
            this.logToTop = logToTop;
        }

        ROLL_TYPES(String s) {
            if (StringMaster.isEmpty(s)) {
                s = StringMaster.getWellFormattedString(name());
            }
            this.name = s;
        }

        public String getName() {
            return name;
        }

        @Override
        public Object[] getVarClasses() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getVariableNames() {
            // TODO Auto-generated method stub
            return null;
        }

        public Image getImage() {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isLogToTop() {
            return logToTop;
        }

    }

    public enum PLAYER_AI_TYPE {
        BRUTE, SNEAK, DEFENSIVE, MAD, SMART, NORMAL
    }

    public enum BEHAVIOR_MODE
            // implements MODE
    {
        PANIC, CONFUSED, BERSERK {
        public boolean isDisableCounters() {
            return false;
        }
    };

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }

        public boolean isDisableCounters() {
            return true;
        }
    }

    public enum AI_TYPE {
        NORMAL, BRUTE, // attack
        SNEAK, // hide, flee, sneak mod
        CASTER,
        CASTER_MELEE,
        CASTER_SUPPORT,
        CASTER_SUMMONER,
        CASTER_OFFENSE, // spells
        // +
        // flee
        ARCHER, // flee
        TANK // def goals
    }

    public enum AI_LOGIC { // targeting prioritizing? should be of various
        // types...
        DAMAGE,
        DAMAGE_ZONE,
        RESTORE,
        DEBILITATE,
        BUFF_POSITIVE,
        BUFF_NEGATIVE,
        SUMMON,
        CUSTOM_HOSTILE,
        CUSTOM_SUPPORT,
        MOVE,
        AUTO_DAMAGE,
        SELF,
        BUFF_POSITIVE_ZONE,
        BUFF_NEGATIVE_ZONE,
        DEBILITATE_ZONE,
        RESTORE_ZONE,
        COATING,
        OTHER

    }

    public enum MAP_BACKGROUND {
        // CITY_OF_GODS("big\\death combat\\city of gods.jpg"),
        SHEOTH("big\\death combat\\sheoth.jpg"),
        PIRATE_BAY("big\\Pirate Bay3.jpg"),
        DUNGEON("big\\dungeon.jpg"),
        DARK_FOREST("big\\dark forest.jpg"),
        LABYRINTH("big\\dungeons\\labyrinth.jpg"),
        CAVE("big\\dungeons\\cave.jpg"),
        UNDERCITY("big\\dungeons\\undercity.jpg"),
        FORGOTTEN_CITY("big\\dungeons\\forgotten city.jpg"),
        MISTY_MOUNTAINS("big\\dungeons\\Misty Mountains.jpg"),
        CEMETARY("big\\dungeons\\cemetary.jpg"),
        DEAD_CITY("big\\dungeons\\dead city.jpg"),
        MANSION("big\\dungeons\\mansion.jpg"),
        RUINS("big\\dungeon\\ruins.jpg"),
        CASTLE("big\\dungeons\\castle.jpg"),
        DARK_CASTLE("big\\dungeons\\dark castle.jpg"),

        // ARENA("big\\Arena.jpg")
        // SANCTUARY("big\\death combat\\forgotten city.jpg"),
        // HIDDEN_CITY("big\\death combat\\Hidden City.jpg"),
        // PIRATE_COAST("big\\death combat\\Pirate Coast.jpg"),
        // SECRET_ENCLAVE("big\\death combat\\secret enclave.jpg"),
        // KYNTHOS("big\\new\\kynthos.jpg"),
        // FELMARSH("big\\new\\felmarsh.jpg"),
        // RAVENWOOD("big\\new\\ravenwood.jpg"),
        // NORDHEIM("big\\new\\nordheim.jpg"),
        // MISTY_MOUNTAINS("big\\death combat\\Misty Mountains Onyx Spire.jpg"),

        ;
        private String backgroundFilePath;

        MAP_BACKGROUND(String backgroundFilePath) {
            this.backgroundFilePath = backgroundFilePath;
        }

        public String getBackgroundFilePath() {
            return backgroundFilePath;
        }

    }

    public enum GENDER {
        MALE, FEMALE, NONE
    }

    public enum BACKGROUND {
        MAN_OF_KINGS_REALM(RACE.HUMAN, SOUNDSET.ANGEL),
        MAN_OF_EAGLE_REALM(RACE.HUMAN, SOUNDSET.ANGEL),
        MAN_OF_GRIFF_REALM(RACE.HUMAN, SOUNDSET.ANGEL),
        MAN_OF_RAVEN_REALM(RACE.HUMAN, SOUNDSET.WIZARD, SOUNDSET.ROGUE),
        MAN_OF_WOLF_REALM(RACE.HUMAN, SOUNDSET.BLACKGUARD),
        STRANGER(RACE.HUMAN, SOUNDSET.ROGUE, SOUNDSET.FEMALE),
        DWARF(RACE.DWARF, SOUNDSET.WIZARD),
        ELF(RACE.ELF, SOUNDSET.ROGUE),

        HIGH_ELF(RACE.ELF),
        FEY_ELF(RACE.ELF),
        GREY_ELF(RACE.ELF),
        DARK_ELF(RACE.ELF),
        WOOD_ELF(RACE.ELF),

        STONESHIELD_DWARF(RACE.DWARF),
        IRONHELM_DWARF(RACE.DWARF),
        MOONSILVER_DWARF(RACE.DWARF),
        WILDAXE_DWARF(RACE.DWARF),
        FROSTBEARD_DWARF(RACE.DWARF),
        REDBLAZE_DWARF(RACE.DWARF),
        GRIMBART_DWARF(RACE.DWARF),
        WOLFSBANE_DWARF(RACE.DWARF),
        RUNESMITH_DWARF(RACE.DWARF),

        NORDHEIMER(RACE.HUMAN, SOUNDSET.BLACKGUARD),
        EASTERLING(RACE.HUMAN, SOUNDSET.BLACKGUARD),

        VAMPIRE(RACE.VAMPIRE, SOUNDSET.BLACKGUARD),

        INFERI_CHAOSBORN(RACE.DEMON),
        INFERI_HELLSPAWN(RACE.DEMON),
        INFERI_WARPBORN(RACE.DEMON),

        WOMAN_OF_KINGS_REALM(RACE.HUMAN, SOUNDSET.FEMALE),
        WOMAN_OF_EAGLE_REALM(RACE.HUMAN, SOUNDSET.FEMALE),
        WOMAN_OF_GRIFF_REALM(RACE.HUMAN, SOUNDSET.FEMALE),
        WOMAN_OF_RAVEN_REALM(RACE.HUMAN, SOUNDSET.FEMALE),
        WOMAN_OF_WOLF_REALM(RACE.HUMAN, SOUNDSET.FEMALE),

        RED_ORC(RACE.GOBLINOID),
        BLACK_ORC(RACE.GOBLINOID),
        PALE_ORC(RACE.GOBLINOID),
        GREEN_ORC(RACE.GOBLINOID),;
        private RACE race;
        private SOUNDSET[] soundsets;

        BACKGROUND(RACE race, SOUNDSET... soundsets) {
            this.setRace(race);
            this.setSoundsets(soundsets);
        }

        public BACKGROUND getMale() {
            if (getRace() == RACE.HUMAN) {
                BACKGROUND male = new EnumMaster<BACKGROUND>().retrieveEnumConst(BACKGROUND.class,
                        name().replace("WO", ""));
                if (male != null) {
                    return male;
                }
            }
            return this;
        }

        @Override
        public String toString() {
            return StringMaster.getWellFormattedString(super.toString());
        }

        public BACKGROUND getFemale() {
            if (isFemale() || getRace() != RACE.HUMAN || this == STRANGER) {
                return this;
            }
            return new EnumMaster<BACKGROUND>().retrieveEnumConst(BACKGROUND.class, "WO" + name());

        }

        private boolean isFemale() {
            if (getRace() != RACE.HUMAN) {
                return false;
            }
            return name().substring(0, 5).equalsIgnoreCase(StringMaster.WOMAN);
        }

        public SOUNDSET[] getSoundsets() {
            return soundsets;
        }

        public void setSoundsets(SOUNDSET[] soundsets) {
            this.soundsets = soundsets;
        }

        public RACE getRace() {
            return race;
        }

        public void setRace(RACE race) {
            this.race = race;
        }

    }

    public enum DEITY {
        NIGHT_LORD, UNDERKING, ARACHNIA, KEEPER_OF_TWILIGHT, DARK_GODS,

        FAITHLESS, FIDE_ARCANUM, LORD_OF_MAGIC, WITCH_KING, IRON_GOD, VOID_LORD,

        ANNIHILATOR, CONSUMER_OF_WORLDS, WARMASTER, QUEEN_OF_LUST,

        SKY_QUEEN, REDEEMER, ALLFATHER, THREE_DIVINES,

        CRIMSON_QUEEN, WRAITH_GOD, DEATH_GORGER, LORD_OF_DECAY,

        FEY_QUEEN, WORLD_TREE, WILD_GOD, WORLD_SERPENT, OLD_WAY
    }

    public enum RACE {
        HUMAN(),
        // raven, wolf, eagle, king, gryphon, bear ++ easterling/nordheimer
        ELF(),
        // eledari (elder elf), (dark elf), (wood elf), half elf
        DWARF(),
        // stoneshield, ironhelm, frostbeard, runesmith, moonsilver, redblaze
        GOBLINOID,
        DEMON,
        VAMPIRE,;

        RACE() {

        }
    }

    public enum DUNGEON_TAGS {
        INTERIOR, UNDERGROUND, SURFACE,
    }

    public enum DUNGEON_TYPE {
        GLORY, MINOR, LOOT, AVERAGE, BOSS, UNDERGROUND
    }

    public enum DUNGEON_GROUP {
        UNDERWORLD, ARCANE, UNDEAD, HUMAN, MISC
    }

    public enum DUNGEON_LEVEL {
        ONE, TWO, THREE, FOUR, FIVE
    }

    public enum DUNGEON_DIFFICULTY {

    }

    public enum ATTRIBUTE {
        STRENGTH,
        VITALITY,
        AGILITY,
        DEXTERITY,
        WILLPOWER,
        INTELLIGENCE,
        WISDOM,
        KNOWLEDGE,
        SPELLPOWER,
        CHARISMA,;
    }

    public enum STD_COUNTERS {
        Bleeding_Counter,
        Blaze_Counter,
        Poison_Counter,
        Freeze_Counter,
        Disease_Counter,
        Ensnared_Counter,
        Moist_Counter,

        Rage_Counter,
        Madness_Counter,
        Despair_Counter,
        Lust_Counter,
        Hatred_Counter,

        Soul_Counter,
        Undying_Counter,
        Blight_Counter,
        Corrosion_Counter,;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }

        public String getName() {
            return toString();
        }

        public Image getImage() {
            return ImageManager.getCounterImage(this);
        }

        public void isNegative() {
            // TODO Auto-generated method stub

        }

    }

    public enum STD_UNIT_TYPES {
        GHOST,
        GHOUL,
        LICH,
        SKELETON,
        VAMPIRE,
        WRAITH_LORD,
        ZOMBIE,
        WRAITH_BEAST,
        ZOMBIE_BEAST,
        UNDEAD_BEAST,
        SKELETAL_BEAST,
        VAMPIRE_BEAST,
        WRAITH_MONSTROCITY,;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }

    }

    public enum STD_BUFF_NAMES {
        Encumbered,
        Overburdened,
        Immobilized,
        Dizzy,
        Discombobulated,
        Razorsharp,
        Exhausted,
        Fatigued,
        Energized,
        Inspired,
        Fearful,
        Terrified,
        Treason,
        Main_Hand_Cadence,
        Off_Hand_Cadence,
        Critically_Wounded,
        Wounded,
        Poison,
        Bleeding,
        Faithless,
        Panic,
        Enraged,
        On_Alert,
        Frost,
        Ablaze,
        Contaminated,
        Spotted,
        Soaked,
        Enchantment,
        Ensnared,
        Asleep,
        Hallucinogetic_Poison,
        Weakening_Poison,
        Paralyzing_Poison,
        Entangled,
        Channeling;

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum FLIP {
        HORIZONTAL, VERTICAL, BOTH, CCW90, CW90, CCW180, CW180
    }

    public enum DYNAMIC_BOOLS {

        BACKWARDS_ROUTE_TRAVEL, AI_CONTROLLED, PLAYER_CONTROLLED, FLIPPED,
    }

    public enum STD_BOOLS {
        DISPELABLE,
        STACKING,
        NON_REPLACING,
        NO_FRIENDLY_FIRE,
        NO_ENEMY_FIRE,
        PERMANENT_ITEM,
        C_VALUE_OVER_MAXIMUM,
        NO_SELF_FIRE,
        SHORTEN_DIAGONALS,
        SOURCE_DEPENDENT,
        MULTI_TARGETING,
        APPLY_THRU,
        CANCELLABLE,
        ARMOR_CHANGE,
        BLOCKED,
        RANDOM,
        INVISIBLE_BUFF,
        SPECIAL_ITEM,
        SELF_DAMAGE,
        INDESTRUCTIBLE,
        INVERT_ON_ENEMY,
        BROAD_REACH,
        LEAVES_NO_CORPSE,
        PASSABLE,
        NON_DISPELABLE,
        DIVINATION_SPELL_GROUPS_INVERTED,
        WRAPPED_ITEM,
        STEALTHY_AOOS,
        DURATION_ADDED,
        UPWARD_Z,
        SPECTRUM_LIGHT,
        CANCEL_FOR_FALSE,
        BUCKLER_THROWER,
        LEFT_RIGHT_REACH,

        // TODO performance would be enhanced of course if I had real booleans
        // instead of a container to be checked.
    }

    public enum SPECIAL_REQUIREMENTS implements VarHolder {
        // boolean
        CUSTOM("condition string", "[AV(condition)]", String.class),
        PARAM("param;amount", VariableManager.getVarIndex(0) + " required: "
                + VariableManager.getVarIndex(1), PARAMETER.class, String.class),
        // PARAM_NOT("param;amount", VariableManager.getVarIndex(0)
        // + " required: " + VariableManager.getVarIndex(1), PARAMETER.class,
        // String.class),
        COUNTER("COUNTER;amount", VariableManager.getVarIndex(0) + " needed: "
                + VariableManager.getVarIndex(1), String.class, String.class),
        // COUNTER_NOT("COUNTER;amount", VariableManager.getVarIndex(0)
        // + " needed: " + VariableManager.getVarIndex(1), String.class,
        // String.class),

        HAS_ITEM("Item slot", "Must have an item in " + VariableManager.getVarIndex(0), ITEM_SLOT.class),
        ITEM("Item slot;property;value", "Must have an item in " + VariableManager.getVarIndex(0)
                + " with " + VariableManager.getVarIndex(1) + " matching "
                + VariableManager.getVarIndex(2), ITEM_SLOT.class, VariableManager.PROP_VAR_CLASS, VariableManager.PROP_VALUE_VAR_CLASS),
        NOT_ITEM("Item slot;property;value", "Must not have an item in "
                + VariableManager.getVarIndex(0), ITEM_SLOT.class, VariableManager.PROP_VAR_CLASS, VariableManager.PROP_VALUE_VAR_CLASS),
        FREE_CELL("Relative position of the cell", "The cell " + VariableManager.getVarIndex(0)
                + " must be free ", UNIT_DIRECTION.class),

        FREE_CELL_RANGE("Relative position of the cell", "The cell "
                + VariableManager.getVarIndex(1) + " spaces " + VariableManager.getVarIndex(0)
                + " must be free ", UNIT_DIRECTION.class, VariableManager.NUMBER_VAR_CLASS),

        NOT_FREE_CELL("Relative position of the cell", "The cell " + VariableManager.getVarIndex(0)
                + " must be occupied", UNIT_DIRECTION.class),

        REF_NOT_EMPTY("obj to check;ref to check", VariableManager.getVarIndex(1) + " is missing!", String.class, KEYS.class),;

        private String text;
        private Object[] vars;
        private String variableNames;

        SPECIAL_REQUIREMENTS(String variableNames, String text, Object... vars) {
            this.vars = (vars);
            this.text = text;
            this.setVariableNames(variableNames);
        }

        public String getText(Object... variables) {
            if (variables.length < 1) {
                return text;
            }
            return VariableManager.getVarText(text, variables);

        }

        @Override
        public Object[] getVarClasses() {
            return vars;
        }

        @Override
        public String getVariableNames() {
            return variableNames;
        }

        public void setVariableNames(String variableNames) {
            this.variableNames = variableNames;
        }

    }

    public enum ACTION_TYPE_GROUPS {
        MOVE, TURN, ATTACK, SPELL, SPECIAL, HIDDEN, MODE, ITEM

    }

    public enum CLASS_TYPE {
        MELEE, SPECIALIST, SPELLCASTER, GODLY
    }

    public enum CLASS_GROUP {
        SORCERER, WIZARD, // APOSTATE, APPRENTICE
        FIGHTER,
        ROGUE,
        KNIGHT,
        TRICKSTER,
        HERMIT,
        ACOLYTE,
        RANGER,
        MULTICLASS,;

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum SKILL_TYPE {
        MASTERY, SPECIAL, TRAIT, PRINCIPLE
    }

    public enum SKILL_GROUP {
        BODY_MIND, SPELLCASTING, WEAPONS, OFFENSE, DEFENSE, MISC,

        CRAFT,

        ARCANE_ARTS, LIFE_ARTS, DARK_ARTS, CHAOS_ARTS, HOLY_ARTS, DEATH_ARTS,

        // STEALTH, DETECTION, ++ BOW/CROSSBOW
    }

    public enum ACTION_TAGS {
        FLYING,
        DUAL,
        OFF_HAND,
        MAIN_HAND,
        TWO_HANDED,
        RANGED_TOUCH,
        ATTACK_OF_OPPORTUNITY,
        ATTACK_OF_OPPORTUNITY_ACTION,
        INSTANT,
        RANGED,
        THROW,
        MISSILE,
        INSTANT_ATTACK,
        RESTORATION,;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }

    }

    public enum ACTION_TYPE {
        STANDARD,
        MODE,
        SPECIAL_MOVE,
        STANDARD_ATTACK,
        SPECIAL_ATTACK,
        SPECIAL_ACTION,
        HIDDEN,
        ADDITIONAL_MOVE,
    }

    public enum STANDARD_ACTION_PASSIVES {
        AGILE, DEXTEROUS,
    }

    public enum IMMUNITIES {
        MIND_AFFECTION, POISON, WOUNDS, ENSNARE, MORALE_REDUCTION, MORALE_REDUCTION_KILL,;
    }

    public enum STANDARD_PASSIVES {
        /**
         *
         */
        BERSERKER("Wounds will only enrage this one!"), // berserk mode + apts!
        BLOODTHIRSTY("Fallen comrades will only spur this one into action!"),
        COLD_BLOODED("Fallen comrades will not discourage this unit and Lust, Hatred or Despair have no effect on it"),
        DISPASSIONATE("This unit is unaffected by any Morale effects"),
        FEARLESS("This unit is unaffected by negative Morale effects"),

        RELENTLESS("This unit is unaffected by Stamina effects"),
        ZOMBIFIED("This unit is unaffected by Focus effects"),
        SOULLESS("Will not provide Soul counters"), //
        FLESHLESS("Poison, disease and bleeding have no effect on this unit"),

        BLIND_FIGHTER("Concealed and Invisible units do not receive bonuses when fighting this unit"),
        TRANSPARENT("Transparent"),
        NON_OBSTRUCTING("Non obstructing"),

        DARKVISION("Darkvision"),
        EYES_OF_LIGHT("Can see through Blinding Light"),
        AUTOMATA("Automata - does not restore action points between rounds"),
        TRAMPLE("Trample"),
        IMMATERIAL("Immaterial - material attackers must win a roll of Willpower against Spellpower to hit this unit"), // TODO
        // prevent
        // from
        // Wounds/Bleeding!
        VIGILANCE("Vigilance"),
        CHARGE("Charge"),
        DEXTEROUS("Dexterous units do not trigger Attacks of Opportunities when moving"),
        AGILE("Agile units retain clear shot through non-Huge obstacles"),
        // DE("Agile"),
        BLUDGEONING("Bludgeoning - Endurance damage this unit deals in melee is not reduced by armor"),
        FLYING("Flying - may pass over battlefield objects and receives 25% bonus against non-Flyers"),
        DOUBLE_RETALIATION("Double Retaliation"),
        DOUBLE_STRIKE("Double Strike"),
        FIRST_STRIKE("First Strike"),
        NO_RETALIATION("No Retaliation"),
        NO_MELEE_PENALTY("No Melee Penalty"),
        CLEAVE("CLEAVE"),
        TRUE_STRIKE("True Strike"),
        INDESTRUCTIBLE("Indestructible"),
        INVULNERABLE(StringMaster.getWellFormattedString("INVULNERABLE")),

        // NEW
        UNLIMITED_RETALIATION("Unlimited Retaliation"),
        OPPORTUNIST("Opportunist "),
        BROAD_REACH("Can attack melee targets to the sides"),
        HIND_REACH("Can attack melee targets behind"),
        CRITICAL_IMMUNE("Immune to Critical Hits"),
        SNEAK_IMMUNE("Immune to Sneak Attacks"),
        MIND_AFFECTING_IMMUNE("Immune to Mind-Affecting"),

        CLEAVING_COUNTERS("Can cleave with counter attacks"),

        CLEAVING_CRITICALS("Critical attacks are always Slashing and may Cleave"),

        WEAKENING_POISON("Will lose stamina per turn for every Poison counter"),
        HALLUCINOGETIC_POISON("Critical attacks are always Slashing and may Cleave"),
        FAVORED("This unit receives full bonus of its deity"),

        SHORT("Non-Short units do not get their vision or missile attacks obstructed by this unit"),
        TALL("Does not get its vision or missile attacks obstructed by non-Tall units"),
        HUGE(""),
        SMALL("Will not block vision and missiles for non-Small units"),
        CLUMSY("This unit has difficulty with diagonal leaps"),
        DRUIDIC_VISIONS("Instead of adding Favor buff, will restore Essence when receiving already known spells thru Divination"),
        HOLY_PRAYER("Will restore Morale when receiving already known spells thru Divination")
        // CORVIDAE("Immune to sneak attacks"),

        ;

        static {
            DARKVISION.setToolTip("Unit is unaffected by Concealment penalties");
            NON_OBSTRUCTING
                    .setToolTip("Non obstructing units do not block other units' vision or missile targeting");
            NO_RETALIATION.setToolTip("Cannot be counter-attacked by units without Vigilance");
            OPPORTUNIST
                    .setToolTip("Will not trigger Attack of Opportunity when using items or casting spells");
            VIGILANCE
                    .setToolTip("Unit is always On Alert and will counter attack units with No Retaliation");
        }

        boolean var;
        private String name;
        private String toolTip;

        STANDARD_PASSIVES(String toolTip) {
            setToolTip(toolTip);
        }

        public String getToolTip() {
            return toolTip;
        }

        public void setToolTip(String toolTip) {
            this.toolTip = toolTip;
        }

        public String getImagePath() {
            return "UI\\value icons\\passives\\" + getName() + ".jpg";

        }

        public String getName() {
            if (name == null) {
                name = StringMaster.getWellFormattedString(name());
            }

            return name;
        }

    }

    public enum RESISTANCE_MODIFIERS {
        NO_SPELL_ARMOR, NO_ARMOR, NO_RESISTANCE, NO_DEFENCE, NO_DAMAGE_TO_UNDEAD,

    }

    public enum UNIT_TO_PLAYER_VISION {
        DETECTED, KNOWN, // CURRENTLY UNDETECTED
        UNKNOWN,
        CONCEALED,
        INVISIBLE,
        INVISIBLE_ALLY
    }

    public enum UNIT_TO_UNIT_VISION {
        IN_PLAIN_SIGHT, IN_SIGHT, BEYOND_SIGHT, CONCEALED;

        public boolean isSufficient(UNIT_TO_UNIT_VISION u_vision) {
            if (u_vision == BEYOND_SIGHT) {
                if (this == CONCEALED) {
                    return true;
                }
            }
            if (u_vision == IN_SIGHT) {
                if (this == IN_PLAIN_SIGHT) {
                    return true;
                }
            }
            return this == u_vision;
        }

    }

    public enum VISION_MODE {
        NORMAL_VISION, X_RAY_VISION, TRUE_SIGHT, WARP_SIGHT, INFRARED_VISION
    }

    public enum VISIBILITY_STATUS {
        CONCEALED_KNOWN, CONCEALED_UNKNOWN, INVISIBLE,

    }

    public enum FACING_MUTUAL {
        FACE_TO_FACE, FROM_BEHIND, FLANKING, PARALLEL
    }

    public enum FACING_SINGLE {

        IN_FRONT, BEHIND, TO_THE_SIDE, NONE

    }

    public enum TARGET_KEYWORD {
        SOURCE, TARGET, SUMMONER, BASIS, WEAPON, ARMOR, SPELL, ACTIVE
    }

    public enum TARGETING_MODIFIERS {
        NOT_SELF,
        NO_FRIENDLY_FIRE,
        NO_UNDEAD,
        ONLY_UNDEAD,
        NO_LIVING,
        ONLY_LIVING,
        CLEAR_SHOT,
        FACING,
        RANGE,
        SPACE,
        VISION,
        NO_VISION,

        SNEAKY,
        ONLY_EVIL,
        NO_EVIL,
        NO_ENEMIES,
        NO_WALLS,
        NO_WATER,
        NO_NEUTRALS

    }

    public enum ABILITY_TYPE {
        ACTIVES, PASSIVES
        // , OBJ
    }

    public enum ABILITY_GROUP {
        DAMAGE, BUFF, STD_ACTION, MOVE, ONESHOT,

        VALUE_MOD, TRIGGER, PASSIVE, STD_PASSIVE, TEMPLATE_PASSIVE, CUSTOM_PASSIVE

        // EXTENDED: GROUPS INTO TYPES, THEN SPLIT FURTHER

    }

    // SYNC WITH TARGETING?
    public enum EFFECTS_WRAP {
        SINGLE_BUFF, CHAIN, ZONE, CUSTOM_ZONE_STAR, CUSTOM_ZONE_CROSS, ZONE_RING,
    }

    public enum TARGETING_MODE {

        SELF, SINGLE, MULTI,

        DOUBLE, TRIPPLE, CELL,

        RAY, // TODO ARGS - N_OF_JUMPS ETC???
        WAVE,
        NOVA,
        BLAST,
        CORPSE,
        TRAP,
        BF_OBJ,
        ANY_UNIT,
        ANY_ENEMY,
        ANY_ALLY,
        ALL,
        ALL_UNITS,
        ALL_ENEMIES,
        ALL_ALLIES,
        BOLT_ANY,
        BOLT_UNITS,
        SPRAY,
        // GLOBAL_NOT_SELF,
        MY_WEAPON,
        MY_ARMOR,
        MY_ITEM,
        ENEMY_WEAPON,
        ENEMY_ARMOR,
        ENEMY_ITEM,
        ANY_ITEM,
        ANY_WEAPON,
        ANY_ARMOR,

    }

    public enum SPELL_TAGS {
        MIND_AFFECTING,
        DEATH_MAGIC,
        POISON,
        DEMONIC,
        DIVINE,
        ELDRITCH,
        RANGED_TOUCH,
        CHANNELING,
        INSTANT,
        MISSILE,

    }

    public enum RESISTANCE_TYPE {
        REDUCE_DAMAGE, CHANCE_TO_BLOCK, REDUCE_DURATION, IRRESISTIBLE,

    }

    public enum STATUS {
        // UNIT
        AIRBORNE,
        DEAD,
        LATE,
        DEFENDING,
        WAITING,
        IMMOBILE,
        SILENCED,
        CHARMED,
        POISONED,
        WOUNDED,
        CRITICALLY_WOUNDED,
        FATIGUED,
        EXHAUSTED,
        DISCOMBOBULATED,
        HAZED,

        // FATIGUED,
        // EXHAUSTED,
        // SPELL
        ILLUMINATED,

        INVISIBLE,
        SPOTTED,

        CONCEALED,
        BLOCKED,
        PREPARED, // COOLDOWN

        // OBJ
        CLAIMED,
        CHANNELED,
        // terrain

        TRAPPED,
        BLEEDING,
        FROZEN,
        RUNNING_LATE,
        ON_ALERT,
        ABLAZE,
        CONTAMINATED,
        ASLEEP,
        HIDDEN,
        FREEZING,
        SOAKED,
        ENSNARED,

        PRONE,
        BROKEN,
        UNLOCKED,
        LOCKED,
        UNCONSCIOUS,
        ENGAGED,
        VIRULENT, // cannot
        // act/counter,
        // gets
        // -75%
        // defense,
        // loses
        // armor
        // //
        // ever-sneak; loses Height obstruction
        // next time prone unit 'moves', it auto-starts a channeling
        // "getting up" which is a Move Action with
        // 4 / 4 ap/sta. If not enough Stamina... Or AP...
        // based on Weight! versus Strength...

        //

        // seems similar to Ensnare - must spend X ap/sta to get rid of the
        // stuff...

        ;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum DAMAGE_MODIFIER {
        VORPAL, PERIODIC, QUIET

    }

    public enum DAMAGE_TYPE {
        PIERCING(),
        BLUDGEONING(),
        SLASHING(),
        POISON(true),
        FIRE(true),
        COLD(true),
        LIGHTNING(true),
        ACID(true),

        SONIC(true),
        LIGHT(true),

        ARCANE(false),
        CHAOS(false),

        SHADOW(false),
        HOLY(false),
        DEATH(false),
        PSIONIC(false),

        //
        // chopping = bludg||slash ;
        PHYSICAL(false),
        PURE(false),
        MAGICAL(false),
        // ASTRAL(false),
        // ELEMENTAL(false)
        ;

        private boolean magical;
        private boolean natural;

        DAMAGE_TYPE() {

        }

        DAMAGE_TYPE(boolean natural) {
            this.setMagical(true);
            this.natural = natural;
        }

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }

        public String getResistanceName() {
            return name() + "_" + "RESISTANCE";
        }

        public boolean isMagical() {
            return magical;
        }

        public void setMagical(boolean magical) {
            this.magical = magical;
        }

        public boolean isNatural() {
            return natural;
        }

    }

    public enum ITEM_SLOT {
        MAIN_HAND(G_PROPS.MAIN_HAND_ITEM),
        ARMOR(G_PROPS.ARMOR_ITEM),
        OFF_HAND(G_PROPS.OFF_HAND_ITEM)

        // NECKLACE(G_PROPS.MAIN_HAND_ITEM),
        // RING_1(G_PROPS.ARMOR_ITEM),
        // RING_2(G_PROPS.OFF_HAND_ITEM)
        ;

        private PROPERTY prop;

        ITEM_SLOT(PROPERTY prop) {
            this.prop = prop;
        }

        public PROPERTY getProp() {
            return prop;
        }
    }

    public enum JEWELRY_TYPE {
        RING, AMULET
    }

    public enum JEWELRY_GROUP {
        RINGS, AMULETS
    }

    public enum ITEM_GROUP {
        POTIONS, CONCOCTIONS, COATING
        // THROWING, TRAP, GEMSTONE

    }

    public enum ITEM_TYPE {
        ALCHEMY, MAGICAL, THROWING

    }

    public enum ARMOR_GROUP {
        CLOTH, LEATHER, CHAIN, PLATE

    }

    public enum ARMOR_TYPE {
        LIGHT, HEAVY,

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
        NEEDLES,
        HOOVES,
        BEAKS,
        EYES,
        FORCE,
        MAWS,;
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

    public enum WEAPON_TYPE {
        BLADE, AXE, BLUNT, POLE_ARM, MAGICAL, SHIELD, RANGED, AMMO, NATURAL

    }

    public enum WEAPON_SIZE {
        TINY, SMALL, MEDIUM, LARGE, HUGE
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
            return StringMaster.getWellFormattedString(name());
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
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum ITEM_MATERIAL_GROUP {
        METAL, WOOD, LEATHER, CLOTH, BONE, STONE, NATURAL, CRYSTAL
    }

    public enum RESIST_GRADE {
        Impregnable(200), Resistant(150), Normal(100), Vulnerable(50), Ineffective(0);
        private int percent;

        RESIST_GRADE(int percent) {
            this.percent = percent;
        }

        public int getPercent() {
            return percent;
        }

    }

    public enum MATERIAL {

        RED_OAK(1, 2, 0.5, 5, 45, ITEM_MATERIAL_GROUP.WOOD, -1, null),
        IRONWOOD(2, 2, 0.75, 12, 85, ITEM_MATERIAL_GROUP.WOOD, 1, null),
        BLACKWOOD(4, 4, 1.25, 25, 130, ITEM_MATERIAL_GROUP.WOOD, 2, null),
        PALEWOOD(5, 6, 1, 50, 225, ITEM_MATERIAL_GROUP.WOOD, 3, null),
        BILEWOOD(6, 3, 2.5, 150, 175, ITEM_MATERIAL_GROUP.WOOD, 4, DAMAGE_TYPE.ACID),
        WAILWOOD(5, 3, 2, 190, 350, ITEM_MATERIAL_GROUP.WOOD, 4, DAMAGE_TYPE.SONIC),
        FEYWOOD(5, 2, 1, 250, 450, ITEM_MATERIAL_GROUP.WOOD, 4, DAMAGE_TYPE.MAGICAL),

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
        ADAMANTIUM(16, 18, 4, 325, 100, ITEM_MATERIAL_GROUP.METAL, 1, DAMAGE_TYPE.FIRE),
        METEORITE(14, 12, 1.5, 400, 150, ITEM_MATERIAL_GROUP.METAL, 3, DAMAGE_TYPE.LIGHTNING),
        // AURUM

        BRIGHT_STEEL(5, 4, 2, 180, 200, ITEM_MATERIAL_GROUP.METAL, 2, DAMAGE_TYPE.LIGHT),
        DEFILED_STEEL(6, 2, 3.5, 195, 125, ITEM_MATERIAL_GROUP.METAL, -1, DAMAGE_TYPE.ACID),

        DARK_STEEL(5, 4, 2, 150, 150, ITEM_MATERIAL_GROUP.METAL, 2, DAMAGE_TYPE.SHADOW),
        WRAITH_STEEL(4, 2, 0.5, 165, 220, ITEM_MATERIAL_GROUP.METAL, -1, DAMAGE_TYPE.DEATH),
        PALE_STEEL(6, 2, 1.5, 150, 180, ITEM_MATERIAL_GROUP.METAL, 3, DAMAGE_TYPE.COLD),
        WARP_STEEL(4, 3, 1.5, 190, 250, ITEM_MATERIAL_GROUP.METAL, 1, DAMAGE_TYPE.PSIONIC),
        DEMON_STEEL(5, 3, 2.5, 200, 200, ITEM_MATERIAL_GROUP.METAL, 1, DAMAGE_TYPE.CHAOS),
        MOON_SILVER(3, 5, 1, 175, 275, ITEM_MATERIAL_GROUP.METAL, 2, DAMAGE_TYPE.HOLY),
        ELDRITCH_STEEL(5, 3, 1.5, 250, 200, ITEM_MATERIAL_GROUP.METAL, 1, DAMAGE_TYPE.ARCANE),

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
        HUGE(14, 8, 275, 375, ITEM_MATERIAL_GROUP.NATURAL, 4),
        FEARSOME(18, 7, 375, 450, ITEM_MATERIAL_GROUP.NATURAL, 5),
        MONSTROUS(26, 9, 500, 650, ITEM_MATERIAL_GROUP.NATURAL, 6),
        BEHEMOTH(45, 15, 1500, 650, ITEM_MATERIAL_GROUP.NATURAL, 6), // sell
        // as
        // trophies...

        ;

        boolean magical;
        MATERIAL_TYPE type;
        private int durabilityMod;
        // "
        private ITEM_MATERIAL_GROUP group;
        private int code;
        private Color color;
        private String name;
        private int modifier;
        private double weight;
        private int hardness;
        private int enchantmentCapacity;
        private int cost;
        private DAMAGE_TYPE dmg_type;
        private Map<DAMAGE_TYPE, RESIST_GRADE> selfDamageGradeMap;
        private Map<DAMAGE_TYPE, RESIST_GRADE> resistGradeMap;

        MATERIAL(int modifier, int durability, double weight, int cost, int magic,
                 ITEM_MATERIAL_GROUP g, int code, DAMAGE_TYPE dmg_type) {
            this(modifier, weight, cost, magic, g, dmg_type, code);
            this.durabilityMod = durability;
            this.group = g;
        }

        MATERIAL(int modifier, double weight, int cost, int magic, ITEM_MATERIAL_GROUP g, int code) {
            this(modifier, weight, cost, magic, code);
            this.group = g;

        }

        MATERIAL(int modifier, double weight, int cost, int magic, ITEM_MATERIAL_GROUP g,
                 DAMAGE_TYPE dmg_type, int code) {
            this(modifier, weight, cost, magic, code);
            this.dmg_type = dmg_type;
            this.magical = true;
            this.group = g;
        }

        MATERIAL(int modifier, double weight, int cost, int magic, int code) {
            this.name = StringMaster.getWellFormattedString(name());
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

    public enum TRAIT {
        CRIPPLE, MADMAN, ORPHAN, BASTARD,

    }

    public enum PERSONALITY {

        CONFIDENT(PRINCIPLES.LAW, PRINCIPLES.TRADITION),
        FANATICAL(PRINCIPLES.TRADITION, PRINCIPLES.HONOR),

        JOVIAL(PRINCIPLES.PEACE, PRINCIPLES.FREEDOM),
        EMPATHICAL(PRINCIPLES.PEACE, PRINCIPLES.CHARITY),

        FAITHFUL(PRINCIPLES.TRADITION, PRINCIPLES.CHARITY),
        SELFLESS(PRINCIPLES.CHARITY, PRINCIPLES.HONOR),

        ARROGANT(PRINCIPLES.FREEDOM, PRINCIPLES.PROGRESS),
        IMPULSIVE(PRINCIPLES.WAR, PRINCIPLES.PROGRESS),

        PARANOID(PRINCIPLES.TREACHERY, PRINCIPLES.AMBITION),
        GREEDY(PRINCIPLES.AMBITION, PRINCIPLES.TREACHERY),

        SADISTIC(PRINCIPLES.WAR, PRINCIPLES.LAW),
        SPITEFUL(PRINCIPLES.WAR, PRINCIPLES.FREEDOM),;

        PERSONALITY(PRINCIPLES... p) {

        }
    }

    public enum PRINCIPLES {

        WAR, PEACE, HONOR, TREACHERY, LAW, FREEDOM, CHARITY, AMBITION, TRADITION, PROGRESS,;

        static {
            HONOR.setOpposite(TREACHERY);
            TREACHERY.setOpposite(HONOR);

            AMBITION.setOpposite(CHARITY);
            CHARITY.setOpposite(AMBITION);

            FREEDOM.setOpposite(LAW);
            LAW.setOpposite(FREEDOM);

            PEACE.setOpposite(WAR);
            WAR.setOpposite(PEACE);

            TRADITION.setOpposite(PROGRESS);
            PROGRESS.setOpposite(TRADITION);

            HONOR.setDescription(Descriptions.HONOR);
            TREACHERY.setDescription(Descriptions.TREACHERY);
            AMBITION.setDescription(Descriptions.AMBITION);
            CHARITY.setDescription(Descriptions.CHARITY);
            FREEDOM.setDescription(Descriptions.FREEDOM);
            LAW.setDescription(Descriptions.LAW);
            PEACE.setDescription(Descriptions.PEACE);
            WAR.setDescription(Descriptions.WAR);
            TRADITION.setDescription(Descriptions.TRADITION);
            PROGRESS.setDescription(Descriptions.PROGRESS);
        }

        private PRINCIPLES opposite;
        private String description;

        PRINCIPLES() {

        }

        public PRINCIPLES getOpposite() {
            return opposite;
        }

        public void setOpposite(PRINCIPLES opposite) {
            this.opposite = opposite;
        }

        @Override
        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public enum RANK {
        NEW_HERO(1), NEOPHYTE(2), DISCIPLE(10), ADEPT(20), CHAMPION(30), AVATAR(40);

        RANK(int hero_level) {

        }
    }

    public enum CLASSIFICATIONS {
        MECHANICAL(true),
        UNDEAD(true),
        SKELETAL,
        DEMON(true),
        HUMANOID(true),
        MONSTER,
        OUTSIDER,
        PSIONIC,
        ANIMAL,
        WRAITH,
        HERO(true),
        STRUCTURE(true),

        ELEMENTAL,
        CONSTRUCT(true),
        TALL(true),
        HUGE(true),
        SMALL(true),
        SHORT(true),
        ATTACHED,
        INSECT,
        REPTILE,
        AVIAN,
        GIANT,;
        boolean displayed;
        private String toolTip;

        CLASSIFICATIONS() {

        }

        CLASSIFICATIONS(boolean displayed) {
            this.displayed = displayed;
        }

        public String getToolTip() {
            if (toolTip == null) {
                return getName();
            }
            return toolTip;
        }

        public void setToolTip(String toolTip) {
            this.toolTip = toolTip;
        }

        public String getImagePath() {
            return "UI\\value icons\\classifications\\" + getName() + ".jpg";

        }

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }

        public boolean isDisplayed() {
            return displayed;
        }

        public void setDisplayed(boolean displayed) {
            this.displayed = displayed;
        }
    }

    public enum RACIAL_BACKGROUND {
        // as skill?
    }

    public enum SPELL_POOL {
        MEMORIZED, DIVINED, VERBATIM,;
    }

    public enum SPELL_TYPE {
        SORCERY, SUMMONING, ENCHANTMENT

    }

    public enum SPELL_TABS {
        ARCANE,
    }

    public enum SPELL_GROUP {
        FIRE,
        AIR,
        WATER,
        EARTH,
        CONJURATION, ENCHANTMENT, SORCERY, TRANSMUTATION, VOID,

        WITCHERY, SHADOW, PSYCHIC,

        NECROMANCY, AFFLICTION, BLOOD_MAGIC,

        WARP, DEMONOLOGY, DESTRUCTION,

        CELESTIAL, BENEDICTION, REDEMPTION,

        SYLVAN, ELEMENTAL, SAVAGE,;
    }

    public enum ASPECT {
        NEUTRAL(0, "Cosmic Crystal", "Tombstone", ""),
        ARCANUM(1, "Arcane Crystal", "Arcane Gateway", "Arcane Mastery"),
        LIFE(2, "Life Crystal", "Life Gateway", "Life Mastery"),
        DARKNESS(3, "Dark Crystal", "Shadow Gateway", "Shadow Mastery"),
        CHAOS(4, "Chaos Crystal", "Chaos Gateway", "Chaos Mastery"),
        LIGHT(5, "Lucent Crystal", "Lucent Gateway", "Holy Mastery"),
        DEATH(6, "Death Crystal", "Death Gateway", "Death Mastery"),
        // LIFE(6, "Life Crystal"),
        ;

        private int code;
        private String crystal;
        private String mastery;
        private String gateway;

        ASPECT(int code, String crystal, String gateway, String mastery) {
            this.code = code;
            this.crystal = crystal;
            this.setGateway(gateway);
            this.setMastery(mastery);
        }

        public static ASPECT getAspectByCode(int code) {
            for (ASPECT a : ASPECT.values()) {
                if (a.getCode() == code) {
                    return a;
                }
            }
            return null;
        }

        public static ASPECT getAspect(String name) {
            return valueOf(name.toUpperCase());
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        // public static ASPECTS getAspectByCode(int code){
        // return valueOf(name.toUpperCase());
        // }

        public String getCrystalName() {
            return crystal;
        }

        public String getMastery() {
            return mastery;
        }

        public void setMastery(String mastery) {
            this.mastery = mastery;
        }

        public String getGateway() {
            return gateway;
        }

        public void setGateway(String gateway) {
            this.gateway = gateway;
        }
    }

    public enum MASTERY {
        PSYCHIC_MASTERY,
        SHADOW_MASTERY,
        WITCHERY_MASTERY,
        FIRE_MASTERY,
        AIR_MASTERY,
        WATER_MASTERY,
        EARTH_MASTERY,
        REDEMPTION_MASTERY,
        BENEDICTION_MASTERY,
        CELESTIAL_MASTERY,

        CONJURATION_MASTERY,
        SORCERY_MASTERY,
        ENCHANTMENT_MASTERY,

        WARP_MASTERY,
        DESTRUCTION_MASTERY,
        DEMONOLOGY_MASTERY,

        AFFLICTION_MASTERY,
        BLOOD_MAGIC_MASTERY,
        NECROMANCY_MASTERY,

        ELEMENTAL_MASTERY,
        SAVAGE_MASTERY,
        SYLVAN_MASTERY,

        VOID_MASTERY,

        WIZARDRY_MASTERY,
        SPELLCRAFT_MASTERY,
        DIVINATION_MASTERY,

        BLADE_MASTERY,
        BLUNT_MASTERY,
        AXE_MASTERY,
        POLEARM_MASTERY,
        UNARMED_MASTERY,
        TWO_HANDED_MASTERY,
        DUAL_WIELDING_MASTERY,

        SHIELD_MASTERY,
        ARMORER_MASTERY,
        DEFENSE_MASTERY,

        STEALTH_MASTERY,
        DETECTION_MASTERY,
        MOBILITY_MASTERY,
        ATHLETICS_MASTERY,
        MEDITATION_MASTERY,
        DISCIPLINE_MASTERY,
        MARKSMANSHIP_MASTERY,
        ITEM_MASTERY,
        WARCRY_MASTERY;

        MASTERY() {

        }

    }

    public enum SOUNDSET {
        HUMAN, ROGUE, BLACKGUARD, FEMALE, WIZARD,

        ANGEL, DAEMON, HORROR, BEAST, ABOMINATION, WOLF, BAT, WRAITH, GHOST, ZOMBIE, CREATURE,

        // SPELL
        ARCANE,
        ARCANE1, // ELDRITCH SORCERY
        ARCANE2, // ILLUSION ENCHANTMENT

        DARK,

        DEATH,

        CHAOS,

        LIGHT,;

        public String getName() {
            return name();
        }
    }

    // public enum CV_

    // why do i need templates? for AV mostly; otherwise CV can be made up of
    // just a bunch of variables...
    // just define the order in which they are given; and some *types* of CV's -
    // MOD vs BONUS at least
    // then also application targets... actives, spells, in the future maybe
    // summoned units etc

    // use cases: 1) reduce cost 2) increase Spellpower/mastery: apply when?

    public enum CUSTOM_VALUE_TEMPLATE implements VarHolder {
        COST_REDUCTION_ACTIVE_TAG("cost value;tag", VariableManager.getVarIndex(0)
                + "_REDUCTION_FOR_ACTIVES_WITH_TAG_" + VariableManager.getVarIndex(1) + "_EQUALS_"
                + VariableManager.getVarIndex(2), VariableManager.PARAM_VAR_CLASS, String.class),

        COST_MOD_SPELL_TYPE("cost value;spell type", VariableManager.getVarIndex(0) + "_MOD_FOR_"
                + "" + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, SPELL_TYPE.class),

        COST_MOD_SPELL_GROUP("cost value;spell group", VariableManager.getVarIndex(0) + "_MOD_FOR_"
                + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, SPELL_GROUP.class),
        COST_MOD_SPELL_POOL("cost value;spell group", VariableManager.getVarIndex(0) + "_MOD_FOR_"
                + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, SPELL_POOL.class),

        COST_MOD_ACTIVE_NAME("cost value;name", VariableManager.getVarIndex(0) + "_MOD_FOR_"
                + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, String.class),

        COST_REDUCTION_SPELL_TYPE("cost value;spell type", VariableManager.getVarIndex(0)
                + "_REDUCTION_FOR_" + "" + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, SPELL_TYPE.class),

        COST_REDUCTION_SPELL_GROUP("cost value;spell group", VariableManager.getVarIndex(0)
                + "_REDUCTION_FOR_" + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, SPELL_GROUP.class),
        COST_REDUCTION_SPELL_POOL("cost value;spell group", VariableManager.getVarIndex(0)
                + "_REDUCTION_FOR_" + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, SPELL_POOL.class),

        COST_REDUCTION_ACTIVE_NAME("cost value;name", VariableManager.getVarIndex(0)
                + "_REDUCTION_FOR_" + VariableManager.getVarIndex(1), VariableManager.PARAM_VAR_CLASS, String.class),

        ENABLE_COUNTER(false, "mode", "ENABLE_COUNTER_FOR_" + VariableManager.getVarIndex(0), STD_MODES.class),;

        private String text;
        private Object[] vars;
        private String variableNames;
        private Boolean param = true;

        CUSTOM_VALUE_TEMPLATE(Boolean param, String variableNames, String text,
                              Object... vars) {
            this(variableNames, text, vars);
            this.param = param;
        }

        CUSTOM_VALUE_TEMPLATE(String variableNames, String text, Object... vars) {

            this.text = text;
            this.vars = (vars);
            this.setVariableNames(variableNames);
        }

        public String getText(Object[] variables) {
            return VariableManager.getVarText(text, variables);

        }

        @Override
        public Object[] getVarClasses() {
            return vars;
        }

        @Override
        public String getVariableNames() {
            return variableNames;
        }

        public void setVariableNames(String variableNames) {
            this.variableNames = variableNames;
        }

        public Boolean getParam() {
            return param;
        }

        public void setParam(Boolean param) {
            this.param = param;
        }
    }

    public enum CUSTOM_VALUES {
        COST_REDUCTION_VERBATIM_SPELLS,
        COST_REDUCTION_MEMORIZED_SPELLS,
        COST_REDUCTION_DIVINED_SPELLS,

        DEFENSE_BONUS_VS_AOO_UPON_SPELL,
    }

    public enum LOOT_TYPE {
        COMMON,

    }

    public enum LOOT_GROUP {

        AMMO, POTIONS, POISONS, CONCONCTIONS,

        JEWELRY, JEWELRY_SPECIAL,

        WEAPONS, ARMOR,

        LIGHT_WEAPONS, LIGHT_ARMOR, HEAVY_WEAPONS, HEAVY_ARMOR,

        WEIRD_WEAPONS, WEIRD_ARMOR, WEIRD_ITEMS, // ANCIENT/DAMAGED + weird
        // materials

        MAGIC_ITEMS,
        MAGIC_ARMOR,
        MAGIC_WEAPONS,
        MAGIC_MATERIAL_WEAPONS,;

        // materials/ench

    }

    // via *Quests*?
    public enum ARCADE_REWARD_TYPE {

        VOLUNTEERS, HONOR_GUARD, GOLD, GLORY, DISCOUNTS,

    }

    public enum ARCADE_LOOT_TYPE { // should implement VarHolder and have
        // some args I suppose
        JEWELRY(LOOT_GROUP.JEWELRY, LOOT_GROUP.JEWELRY_SPECIAL),
        MAGIC_CHEST(LOOT_GROUP.MAGIC_ITEMS, LOOT_GROUP.MAGIC_ITEMS, LOOT_GROUP.MAGIC_ITEMS,

                LOOT_GROUP.JEWELRY_SPECIAL),
        TREASURE_CHEST,
        MAGIC_ITEM,

        WAR_GEAR,
        GOLD_STASH,
        GLORY,

        CAGED_BEASTS,
        PRISONERS, // good idea, free some prisoners, man up
        // some deserters, gain gratitude of the
        // hardy townsfolk...
        ;
        private LOOT_GROUP[] groups;

        ARCADE_LOOT_TYPE(LOOT_GROUP... groups) {
            this.groups = groups;
        }
    }

    // the
    public enum ARCADE_ROUTE {
        WILD_PATH(75, 1, "Dire Grizzly;Warg Pack;", "Wolf Pack;Feline Predators", ""),
        PLAINS_OF_DESOLATION(50, 1, "Naga Patrol;Mutant Pack;", "", ""),
        DARK_PATH(75, 1, "Wolf Pack;", "Night Terrors", ""),
        HIGH_ROAD(35, 1, "Robber Band;", "Sharadrim Squad", ""),

        MURKY_GROVE(50, 1, "Naga Patrol;Mutant Pack;", "", ""),
        MISTVEILED_TRAIL(65, 1, "Naga Patrol;Mutant Pack;", "", ""),

        EERIE_COAST(65, 1, "Naga Patrol;Mutant Pack;", "", ""),
        ROCKY_CLIMB(65, 1, "Griff;Ogre;", "Goblin Band;Orc Band;", ""),
        SHALLOW_FORD(65, 1, "Naga Patrol;Mutant Pack;", "", ""),
        SWAMPY_TRAIL(65, 1, "Naga Patrol;Mutant Pack;", "", ""),

        BLACK_SANDS(65, 1, "Naga Patrol;Mutant Pack;", "", ""),
        DEFILED_OASIS(65, 1, "Naga Patrol;Mutant Pack;", "", ""),
        PLAINS_OF_DEATH(65, 1, "Naga Patrol;Mutant Pack;", "", ""),
        SULFUROUS_WASTES(65, 1, "Naga Patrol;Mutant Pack;", "", ""),
        CANYON(65, 1, "Naga Patrol;Mutant Pack;", "", ""),;
        // TODO player should have at least 2 choices, right?
        // Day/Night should be used for ALTERNATION!
        // basically just counting by %n_of_dungeons_completed
        int danger;
        int length;
        String encounterPool;
        String altEncounterPool;

        // time will just consume gold I suppose? :)

        ARCADE_ROUTE(int danger, int length, String encounterPool, String altEncounterPool,
                     String imgPath) {

        }

        public int getDanger() {
            return danger;
        }

        public int getLength() {
            return length;
        }

        public String getEncounterPool() {
            return encounterPool;
        }

        public String getAltEncounterPool() {
            return altEncounterPool;
        }

    }

    // What is the disadvantage of using Enums over Types, why not?
    public enum ARCADE_REGION {
        // ++ general background picture
        DUSK_DALE(5, 6, "Cemetary;Bandit Camp;Crystal Cave;Ancient Crypt;Dungeon;"),
        // >> Ancient Crypt -
        // *Spec Loot: ancient magical items // jewelry
        // >> Ruined Fort - deserters, necro/demon cultists, fallen knight boss
        // *Spec Loot: fair weaponry or magical items
        // >> Dungeon - goblins, kobolds,
        // *Spec Loot: poison flasks
        GREYLEAF_WOODS(3, 4, "Ruined Fort;Demon Shrine;Forlorn Ruins;Plague House;Cavern;"),
        // *Spec Loot: glory
        // >> Forlorn Ruins
        // *Spec Loot: random
        // ++ Demon Shrine, Ancient Crypt
        WRAITH_MARSHES(3, 4, "Abandoned Sewer;Serpent Lair;Drowned Tomb;Haunted Mansion;Dwarven Halls;Ship Graveyard"),
        // *Spec Loot: poison flasks
        // >> Cavern
        // *Spec Loot: poison flasks
        // >> Haunted Mansion - warlocks
        // Shipwreck
        MISTY_SHORES(3, 4, "Dark-Reef Isle;Dark Castle;Arcane Tower;Elementum;Lichyard;Smuggler Grottos"),
        // ++ naga
        // >> Dwarven Halls - dark dwarves // orcs and trolls
        // *Spec Loot: magic metal items
        // >> Elementum -

        BLIGHTSTONE_DESERT(1, 2, "Undercity;Sanctuary;Crystal Chamber;Abyss;Doom Forge"),;
        // CITY OF ANCIENTS
        // EXCAVATION
        // MONOLITH

        // TODO perhaps *here* we need a dungeon sequence! just 1 final dungeon
        // might seem little!
        // keep-forge-core, mausoleum-undercity-crystal chamber,
        // Blightstone Desert (1/2 to loot), (level 12+)
        // >> Crystal Chamber- [great demons]||wraiths, void servants, archangel
        // Final Prize: Crystal Shard (Aspect?)
        // >> Undercity - undead, dungeon beasts, monstrous insects, black
        // dragon
        // Final Prize: Void Mask
        // >> Sanctuary- witch spawn, arcane guardians, elementals
        // Final Prize: Aether Cloak
        // >> Mechanicum Core// <?>
        // Final Prize: Warp Ring
        int minToLoot;
        int minPoolSize;
        String dungeonPool;

        ARCADE_REGION(int minToLoot, int minPoolSize, String dungeons) {
            this.minPoolSize = minPoolSize;
            this.dungeonPool = dungeons;
            this.minToLoot = minToLoot;

        }

        // shop "level" - materials, qualities, special items *level*
        public String toString() {
            return StringMaster.getWellFormattedString(name());

        }

        public ARCADE_REGION getNext() {
            int i = new ListMaster<ARCADE_REGION>().getList(values()).indexOf(this);
            i++;
            if (values().length == i) {
                return null;
            }
            return values()[i];
        }

        public int getMinToLoot() {
            return minToLoot;
        }

        public int getMinPoolSize() {
            return minPoolSize;
        }

        public String getDungeonPool() {
            return dungeonPool;
        }

    }

    public enum HERO_SOUNDSET {
        ANGEL("chars\\male\\angel\\", false),
        FIGHTER("chars\\male\\quiet\\", false),
        TOUGH("chars\\male\\dwarf\\", false),
        JOVIAL("chars\\male\\trickster\\", false),
        CAUTOUS("chars\\male\\rogue\\", false),
        LEADER("chars\\male\\fighter\\", false),
        LEARNED("chars\\male\\wizard\\", false),
        VALIANT("chars\\male\\champion\\", false),
        NOBLE("chars\\male\\noble\\", false),
        MANIAC("chars\\male\\blackguard\\", false),

        W_INNOCENT("chars\\female\\human\\", true),
        W_PLAYFUL("chars\\female\\bad girl\\", true),
        W_JOVIAL("chars\\female\\wood elf\\", true),
        W_NOBLE("chars\\female\\good girl\\", true),
        W_VALIANT("chars\\female\\fighter\\", true),
        W_FEISTY("chars\\female\\feisty\\", true),
        W_LEARNED("chars\\female\\noble\\", true),
        W_TOUGH("chars\\female\\husky\\", true),
        W_DEMENTED("chars\\female\\vampire female\\", true),
        W_VILE("chars\\female\\succubus\\", true),

        // ++ quiet =))
        ;
        private Boolean female;
        private String path;

        HERO_SOUNDSET(String path, Boolean female) {
            this.path = path;
            this.female = female;
        }

        public String getPropValue() {
            return path;
        }

        public String getName() {
            return StringMaster.getWellFormattedString(name().replace("W_", ""));
        }

        @Override
        public String toString() {
            return StringMaster.getWellFormattedString(name().replace("W_", "")).toUpperCase();
        }

        public boolean isFemale() {

            return female;
        }

    }

    public enum ENCOUNTER_GROUP {
        LIGHT, DARK, UNDEAD, ARCANE, DUNGEON, MONSTERS, DEMONS, HUMANS, MISC, MECHANICUM,
    }

    public enum ENCOUNTER_SUBGROUP {
        AVIANS,
        NOCTURNAL,
        WULFEN,
        SWAMP,
        DUNGEON_MONSTERS,
        CRITTERS,
        COLONY,
        ORCS,
        GOBLINS,
        GIANTS,
        WARP_DEMONS,
        DEMONS,
        FIRE_DEMONS,
        CHAOS_CULTISTS,
        DARK_CULTISTS,
        DEATH_CULTISTS,
        ARCANE_APOSTATES,
        SHADOW_APOSTATES,
        WIZARDS,
        BANDITS,
        DESERTERS,
        PIRATES,
        MAGICAL,
        RAVENGUARD,
        RED_DAWN,
        SILVERLANCE,
        PUTRID_UNDEAD,
        UNDEAD,
        GORY_UNDEAD,
        WRAITHS,
        VAMPIRES,
        TWILIGHT_FORCES,
        GHOSTS,
        MECHANICUM,
        POSSESSED,
        WOOD_ANIMALS,
        SPIDERS,

    }

    public enum GROWTH_PRIORITIES {
        GROUP, LEVEL, FILL, EXTEND
    }

    public enum ENCOUNTER_TYPE {
        CONTINUOUS, REGULAR, ELITE, BOSS
    }

}
