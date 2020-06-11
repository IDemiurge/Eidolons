package main.content.enums.entity;

import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.util.Map;

/**
 * Created by JustMe on 2/14/2017.
 * Contains all enums related to Units
 */
public final class UnitEnums {
    public enum CLASSIFICATIONS {
        BOSS(true),
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
        GIANT, UNIQUE;
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
            return "ui/value icons/classifications/" + getName() + ".jpg";

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

    public enum COUNTER {
        Bleeding,
        Blaze,
        Poison,
        Freeze,
        Disease,
        Ensnared,
        Moist,
        Charge {
            public boolean isNegativeAllowed() {
                return true;
            }
        },
        Lava,
        Ash,
        Clay,
        Encase,
        Grease,
        Rage,
        Madness,
        Despair,
        Lust,
        Hatred,

        Virtue,
        Light,
        Haze,
        Zeal,
        Encryption,
        Void,
        Magnetized,
        Time_Warped {
            public boolean isNegativeAllowed() {
                return true;
            }
        },
        Mutagenic,
        Zen,
        Loyalty,
        Demon_Debt {
            public boolean isNegativeAllowed() {
                return true;
            }
        },
        Demon_Names,
        Ward,

        Soul,
        Undying ,
        Blight,
        Corrosion, Oblivion,
        Taint, Aether,
        Warp, Suffocation;

        private Map<COUNTER, COUNTER_INTERACTION> interactionMap;
        private COUNTER down;
        private COUNTER up;
        private final String imagePath;
        private final String name =
         StringMaster.getWellFormattedString(name()) + StringMaster.COUNTER;

        COUNTER() {
            imagePath = ImageManager.getValueIconsPath() + "counters/" + toString() + ".png";
        }

        public boolean isNegativeAllowed() {
            return false;
        }

        public String getName() {
            return name;
        }

        public void isNegative() {
            // TODO Auto-generated method stub

        }

        public Map<COUNTER, COUNTER_INTERACTION> getInteractionMap() {
            return interactionMap;
        }

        public void setInteractionMap(Map<COUNTER, COUNTER_INTERACTION> interactionMap) {
            this.interactionMap = interactionMap;
        }

        public COUNTER getDown() {
            return down;
        }

        public void setDown(COUNTER down) {
            this.down = down;
        }

        public COUNTER getUp() {
            return up;
        }

        public void setUp(COUNTER up) {
            this.up = up;
        }

        public String getImagePath() {
            return imagePath;
        }

    }

    public enum COUNTER_INTERACTION {
        CONVERT_TO, CONVERT_FROM, MUTUAL_DELETION, DELETE_OTHER, DELETE_SELF,
        TRANSFORM_UP, TRANSFORM_DOWN,
        GROW_SELF, GROW_OTHER, GROW_BOTH,
    }

    public enum COUNTER_OPERATION {
        TRANSFER_TO,
        TRANSFER_FROM,;

    }

    public enum FACING_MUTUAL {
        FACE_TO_FACE, FROM_BEHIND, FLANKING, PARALLEL
    }

    public enum FACING_SINGLE {

        IN_FRONT, BEHIND, TO_THE_SIDE, NONE

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
        HOLY_PRAYER("Will restore Morale when receiving already known spells thru Divination"),
        // CORVIDAE("Immune to sneak attacks"),
        VOIDWALKER("Can traverse Void cells"),
        IMMOBILE("Cannot act");


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
            return "ui/value icons/passives/" + getName() + ".jpg";

        }

        public String getName() {
            if (name == null) {
                name = StringMaster.getWellFormattedString(name());
            }

            return name;
        }

    }

    public enum STANDARD_WOUNDS {
        MISSING_RIGHT_HAND, MISSING_LEFT_HAND, MISSING_RIGHT_EYE, MISSING_LEFT_EYE,
    }

    public enum STATUS {
        // UNIT
        SNEAKING,
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
        GUARDING,

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
        REVEALED,
        PRONE,
        BROKEN,
        UNLOCKED,
        LOCKED,
        UNCONSCIOUS,
        UNDYING,
        ENGAGED,
        VIRULENT,
        DISABLED, //("Is not active now")

        // cannot
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

        OFF;



        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum STD_UNDEAD_TYPES {
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

    // PERHAPS ENCOUNTERS COULD HAVE DEITY PROPERTY AS WELL;
    public enum UNIT_GROUP {
        Ravenguard("Traitor,Corrupted,Royal"),
        PRISONERS,
        HUMANS("Militia,Scum,Guards,Army,"),
        BANDITS("Pirates,Thieves Guild,Robbers,"),
        HUMANS_KNIGHTS("ravenguard,holy,"),
        HUMANS_CRUSADERS,
        PIRATES,
        HUMANS_BARBARIANS,

        DWARVEN_SCUM(),
        DWARVEN_LORDS(),
        DWARVES("forsworn,clansmen,"),
        NORTH("norse,woads,brutes,north"),

        ORCS("goblins,orcs"),
        PALE_ORCS,
        TUTORIAL,
        UNDEAD("Plague,Crimson,Wraith,Pale"),
        UNDEAD_PLAGUE("Plague,Crimson,Wraith,Pale"),
        UNDEAD_CRIMSON("Plague,Crimson,Wraith,Pale"),
        UNDEAD_WRAITH("Plague,Crimson,Wraith,Pale"),

        DEMONS("chaos,abyss,demons,demon worshippers"),
        DEMONS_HELLFIRE("chaos,abyss,demons,demon worshippers"),
        DEMONS_ABYSS("chaos,abyss,demons,demon worshippers"),
        DEMONS_WARPED("chaos,abyss,demons,demon worshippers"),

        MAGI,
        MISTSPAWN,
        CULT_CONGREGATION,
        CULT_CERBERUS("constructs,apostates,magi,,"),
        CULT_DEATH,
        CULT_DARK,
        CULT_CHAOS,

        ELEMENTALS(""),
        CONSTRUCTS,
        DARK_ONES,
        MUTANTS,
        CELESTIALS,

        CRITTERS("critters,spiders,nocturnal"),
        CRITTERS_SPIDERS("critters,spiders,nocturnal"),
        CRITTERS_COLONY,
        DUNGEON("chaos,demons,worshippers,"),


        FOREST("greenies,creatures"),
        ANIMALS("Animals,Wolves,Wargs,wild,"),
        REPTILES, BANDIT_SCUM(), SPIDERS,;

        private final String subgroups;

        UNIT_GROUP(String groups) {
            this.subgroups = groups;
        }
        UNIT_GROUP( ) {
            this.subgroups = name();
        }

        public String getSubgroups() {
            return subgroups;
        }

    }

    public enum UNITS_TYPES implements OBJ_TYPE_ENUM {
        TRICKSTER_RAVEN,
        VOID_EIDOLON,
        VOID_SPAWN,
        TWISTED_MUTANT,
        ELDRITCH_STRIX,
        GARGOYLE_SENTINEL,
        IRON_GOLEM,
        VOID_PHANTOM,
        STEEL_GOLEM,
        VOID_ARCHON,
        ELDRITCH_KNIGHT,
        VOID_WALKER,
        AETHER_GOLEM,
        GARGANTUAN_BEAST,
        RABID_MUTANT,
        HORSESHOE_CRAB,
        GARGOYLE,
        ANCIENT_AUTOMATA,
        STONE_GOLEM,
        MECHANICUM_TITAN,
        APPRENTICE_CONJURER,
        APPRENTICE_ENCHANTER,
        APPRENTICE_ELEMENTALIST,
        OLD_RUSTY_GOLEM,
        FAMILIAR,
        DEMON_CARNIFEX,
        DEMON_BRUTE,
        INFERNAL_GOLEM,
        HELLGUARD,
        DEMON_GORGER,
        DEMON_PRINCE,
        LORD_OF_DESPAIR,
        POSSESSED,
        DEMON_TORMENTOR,
        MISTRESS,
        FIEND,
        DEVIL_MAGE,
        AVENGER,
        SCREAMER,
        LORD_OF_HATRED,
        DEMON_CALLER,
        DEMON_WORSHIPPER,
        DEMON_LORD,
        ABYSSAL_FIEND,
        DEMON_VINDICATOR,
        CHAOS_WARLOCK,
        SUCCUBUS,
        INFERI_SORCEROR,
        SATYR_DEMONCALLER,
        INCUBUS,
        LORD_OF_TERROR,
        LORD_OF_PAIN,
        WARP_HUNTER,
        IMP,
        WARP_STALKER,
        HELLUNICODE39CODEENDS_TYRANT,
        CHAOS_LEGIONNAIRE,
        TWILIGHT_LEGIONNAIRE,
        BLACK_WOLF,
        EVIL_EYE,
        WEREWOLF,
        NIGHTMARE,
        CERBERUS,
        MANTICORE,
        HYDRA,
        TROGLODYTE,
        MINOTAUR,
        HARPY,
        GUARDIAN_OF_THE_UNDERWORLD,
        MIND_FLAYER,
        DEMONIO_NOCTURNO,
        SHADOW_WOLF,
        BLACK_DRAGON,
        SHADE,
        DEADLY_SPIDER,
        NIGHT_BAT,
        PALE_WEAVER,
        MYCOSA,
        BLACK_WIDOW,
        SPIDERLING,
        SHADOW_DISCIPLE,
        SHADOW_WEAVER,
        HUNTED_WITCH,
        TWILIGHT_DISCIPLE,
        DARK_ANGEL,
        SPIDERITE,
        WITCH,
        SHADOW,
        DARK_FEY,
        MALEFIC_SHADE,
        DARK_URUK,
        POSSESSED_GARGOYLE,
        SATYR_GLOOMWEAVER,
        PALE_FIEND,
        CORRUPTED_MIND_FLAYER,
        MINOTAUR_PRAETOR,
        TROGLODYTE_MUTANT,
        PSYCHIC_ABERRATION,
        SHARADRIM,
        PUNY_SPIDERLING,
        VENOMOUS_HAIRY_SPIDER,
        DARK_SCALES,
        MIST_EYE,
        MURKBORN,
        MURKBORN_DEFILER,
        DEMENTED_PRISONER,
        DEMENTED_WOMAN,
        ABOMINATION,
        MUZZLED_MAN,
        FRENZIED_WOMAN,
        FRENZIED_MAN,
        RABID_MAN,
        CRIMSON_MASKED_CULTIST,
        WOLF_MASKED_CULTIST,
        WORM_MASKED_CULTIST,
        ONYX_MASKED_CULTIST,
        STEEL_MASKED_CULTIST,
        MURK_SPIDER,
        MURK_WEAVER,
        MISTBORN_GARGANTUAN,
        ESCAPED_PRISONER,
        SKULL_MASKED_CULTIST,
        FERAL_CULTIST,
        DEMON_CULTIST,
        SCORCHED_CULTIST,
        PLACEHOLDER_DUNGEON,
        PLACEHOLDER_DUNGEON_CORRUPTED,
        PLACEHOLDER_DUNGEON_MONSTER,
        PLACEHOLDER_CRITTER,
        PLACEHOLDER_CRITTER_SPIDERS,
        COLONY_QUEEN,
        SKELETON,
        HUSK,
        VAMPIRE_BAT,
        ZOMBIE,
        BLOOD_GHAST,
        GHOST,
        REVENANT,
        CORPSEBORN,
        BLOOD_REVENANT,
        DEATH_LORD,
        COLONY_OVERSEER,
        COLONY_HARVESTER,
        COLONY_WARRIOR,
        COLONY_DRONE,
        UNDEAD_BEAST,
        SKELETAL_BEAST,
        ZOMBIE_BEAST,
        WRAITH_BEAST,
        VAMPIRE_BEAST,
        HUORN,
        ELEDARI_NECROMANCER,
        ELEDARI_DARK_DRUID,
        BLOODMAGE_APPRENTICE,
        DEATH_KNIGHT,
        DEATH_WORSHIPPER,
        PLAGUE_SERVANT,
        VAMPIRE,
        GHOUL,
        UNDEAD_MONSTROCITY,
        VAMPIRE_LORD,
        VAMPIRE_MISTRESS,
        INFESTED_HUSK,
        GHAST_ZOMBIE,
        WRAITH_WHISPERER,
        CRIMSON_CHAMPION,
        WRAITH,
        PLAGUE_ZOMBIE,
        PLAGUE_RAT,
        AFFLICTED_NOBLEMAN,
        LICH,
        DEATH_ADEPT,
        BONE_DRAGON,
        SKELETON_ARCHER,
        PLAGUE_BRINGER,
        KING_OF_THE_DEAD,
        BONE_KNIGHT,
        GHAST_WRAITH,
        SATYR_DEATHUNICODE45CODEENDEATER,
        WRAITH_KNIGHT,
        ASH_EATER,
        HEADLESS_HORSEMAN,
        SMELLY_ZOMBIE,
        CORPSE_SLUG,
        PLACEHOLDER_UNDEAD,
        PLACEHOLDER_UNDEAD_CRIMSON,
        PLACEHOLDER_UNDEAD_PLAGUE,
        PLACEHOLDER_UNDEAD_WRAITH,
        PLACEHOLDER_UNDEAD_MONSTER,
        PLACEHOLDER_UNDEAD_BOSS,
        GREY_WOLF,
        DIRE_GRIZZLY,
        FIRE_ELEMENTAL,
        EARTH_ELEMENTAL,
        AIR_ELEMENTAL,
        WATER_ELEMENTAL,
        SHADOW_ELEMENTAL,
        POISON_ELEMENTAL,
        MAGIC_ELEMENTAL,
        WARG,
        BLACK_PANTHER,
        TREANT_SAPLING,
        TREANT,
        DRAKE,
        BASILISK,
        SLAAG,
        WYVERN,
        GRIFF,
        WILLUNICODE45CODEENDOUNICODE45CODEENDWISP,
        DRAKELING,
        DIRE_WARG,
        ARMORED_WYVERN,
        YOUNG_WYVERN,
        ZOMBIFIED_WYVERN,
        HERMIT,
        FEY,
        MANTIS,
        GIANT_DRAGONFLY,
        MOSS_SPAWN,
        KAITHAR_DRUID,
        SKY_LION,
        GREY_EAGLE,
        CLOUD_TITAN,
        FANATIC,
        ROX,
        SKY_LORD,
        SQUIRE,
        KNIGHT_ERRANT,
        LUCENT_WISP,
        AYRIE,
        BATTLE_MONK,
        CRUSADER,
        INQUISITOR,
        VINDICATOR,
        LORD_CARDINAL,
        DEFENDER_OF_LIGHT,
        DEFENDER_OF_FAITH,
        MARTYR,
        RED_DAWN_PRIEST,
        PRIEST_OF_THE_THREE,
        SERVANT_OF_THE_THREE,
        SHAMAN,
        OGRE,
        BASE_HUMAN_UNIT,
        THUG,
        THIEF,
        MARAUDER,
        DESERTER,
        CUTTHROAT,
        GUARDSMAN,
        ORC_RAIDER,
        BARBARIAN,
        FALLEN_KNIGHT,
        PIRATE,
        HUMAN_APOSTATE,
        HUMAN_FIGHTER,
        DARK_APOSTATE,
        DARK_ADEPT,
        BASE_DWARF_UNIT,
        BASE_ELF_UNIT,
        WARLOCK,
        ENCHANTRESS,
        BANDIT_ARCHER,
        ORC_RAVAGER,
        APOSTATE,
        CHAOS_APOSTATE,
        CHAOS_CULTIST,
        FORSWORN_DWARF,
        DWARF_GUARDSMAN,
        GREY_ELF_SCOUT,
        BANDIT_LORD,
        ANTIUNICODE45CODEENDPALADIN,
        GRAVE_LOOTER,
        CANNIBAL,
        PLAGUE_BEARER,
        RENEGADE_WIZARD,
        SORCERESS,
        RABID_DOG,
        CONTAMINATED_DOG,
        GOBLIN_POACHER,
        GOBLIN_WATCHMAN,
        GOBLIN_SKIRMISHER,
        ORC_KHAN,
        ORC_EXECUTIONER,
        GOBLIN_SCOUT,
        BOUNTY_HUNTER,
        RONIN_BALLESTERO,
        HALFUNICODE45CODEENDORC_BOUNCER,
        ASSASSIN,
        GOBLIN_AMBUSHER,
        CONJURER,
        ORC_CHAMPION,
        DWARF_SHOOTER,
        DWARVEN_MILITIA,
        DWARF_WARRIOR,
        FORSWORN_MINER,
        HILL_GIANT,
        MOUNTAIN_GIANT,
        FROST_GIANT,
        DOGGY,
        STORM_GIANT,
        GIANTESS,
        DWARF_BRAWLER,
        DWARF_SHIELDMAN,
        DWARF_RUNEPRIEST,
        VALKYRIE,
        DWARF_SHARPEYE,
        TROLL,
        DWARF_VETERAN,
        EXILED_THANE,
        NAGA_ASSAILANT,
        SNIPER,
        MERCENARY_RANGER,
        GOBLIN_CREWUNICODE45CODEENDLEADER,
        RAVENGUARD_OVERSEER,
        RAVENGUARD_KNIGHT,
        RAVENGUARD_ENFORCER,
        RAVENGUARD_COMMANDER,
        RAVENGUARD_LIEUTENANT,
        RAVENGUARD_JAILOR,
        RAVENGUARD_SPECIALIST,
        RAVENGUARD_EXECUTIONER,
        RAVENGUARD_WARDEN,
        SHIELDMAN,
        AXEMAN,
        SOLDIER,
        BARBARIAN_CHIEF,
        BERSERKER,
        RAVENGUARD_CROSSBOWMAN,
        STAG_BEETLE,
        RHINO_BEETLE,
        CROSSBOWMAN,
        PEASANT,
        SWORDSMAN,
        PIKEMAN,
        DARK_SHAMAN,
        WAR_MAIDEN,
        BALLESTERO,
        SAVAGE_DWARF,
        PIRATE_SABOTEUR,
        PIRATE_BRUTE,
        PIRATE_SKIRMISHER,
        PIRATE_FIRST_MATE,
        PIRATE_TASKMASTER,
        PIRATE_CAPTAIN,
        GLORY_SEEKER,
        COLDSKIN_WARRIOR,
        COLDSKIN_ELDER,
        NORDHEIM_WARRIOR,
        SLAYER,
        NORDHEIM_CHAMPION,
        BRAWLER,
        ORC_ARCHER,
        UNSUSPECTING_GOBLIN_ARCHER,
        DOOMED_GOBLIN_FODDER,
        GROWLBELLY,
        DESPERATE_ORC_SLAVE,
        RAVENGUARD_WITCHUNICODE45CODEENDKNIGHT,
        RAVENGUARD_TORTURER,

        SILVERLANCE_COMMANDER,
        SILVERLANCE_KNIGHT,
        ;
    }
    public enum  UNITS_TYPES_FAMILIARS implements OBJ_TYPE_ENUM {
        TRICKSTER_RAVEN,
        ELDRITCH_STRIX,
        DRAKELING,
        MANTIS,
        STAG_BEETLE,
        RHINO_BEETLE,
        ;
    }
    public enum  UNITS_TYPES_DEMO implements OBJ_TYPE_ENUM {
        VOID_EIDOLON,
        GARGANTUAN_BEAST,
        GARGOYLE,
        ANCIENT_AUTOMATA,
        STONE_GOLEM,
        APPRENTICE_ELEMENTALIST,
        DEMON_GORGER,
        POSSESSED,
        FIEND,
        AVENGER,
        HELLUNICODE39CODEENDS_TYRANT,
        TWILIGHT_LEGIONNAIRE,
        BLACK_WOLF,
        NIGHTMARE,
        TROGLODYTE,
        HARPY,
        DEMONIO_NOCTURNO,
        SHADOW_WOLF,
        SHADE,
        NIGHT_BAT,
        SHADOW_DISCIPLE,
        SHADOW_WEAVER,
        HUNTED_WITCH,
        TWILIGHT_DISCIPLE,
        SKELETON,
        VAMPIRE_BAT,
        ZOMBIE,
        COLONY_OVERSEER,
        COLONY_HARVESTER,
        COLONY_WARRIOR,
        COLONY_DRONE,
        SKELETON_ARCHER,
        BONE_KNIGHT,
        FIRE_ELEMENTAL,
        EARTH_ELEMENTAL,
        AIR_ELEMENTAL,
        WATER_ELEMENTAL,
        MOSS_SPAWN,
        SKY_LION,
        GREY_EAGLE,
        FANATIC,
        ROX,
        SKY_LORD,
        SQUIRE,
        KNIGHT_ERRANT,
        THUG,
        BANDIT_LORD,
        GRAVE_LOOTER,
        CANNIBAL,
        PLAGUE_BEARER,
        RENEGADE_WIZARD,
        RABID_DOG,
        GOBLIN_AMBUSHER,
        RAVENGUARD_OVERSEER,
        RAVENGUARD_KNIGHT,
        RAVENGUARD_ENFORCER,
        RAVENGUARD_COMMANDER,
        RAVENGUARD_LIEUTENANT,
        RAVENGUARD_JAILOR,
        RAVENGUARD_SPECIALIST,
        RAVENGUARD_EXECUTIONER,
        RAVENGUARD_WARDEN,
        SHIELDMAN,
        AXEMAN,
        SOLDIER,
        CROSSBOWMAN,
        PEASANT,
        SWORDSMAN,
        PIKEMAN,
        RAVENGUARD_WITCHUNICODE45CODEENDKNIGHT,
        RAVENGUARD_TORTURER,
        ;
    }
    public enum  UNITS_TYPES_NOCTURNAL implements OBJ_TYPE_ENUM {
        VOID_SPAWN,
        TWISTED_MUTANT,
        IRON_GOLEM,
        VOID_PHANTOM,
        VOID_ARCHON,
        VOID_WALKER,
        AETHER_GOLEM,
        RABID_MUTANT,
        WEREWOLF,
        ASH_EATER,
        ;
    }
    public enum  UNITS_TYPES_ implements OBJ_TYPE_ENUM {
        GARGOYLE_SENTINEL,
        ELDRITCH_KNIGHT,
        HORSESHOE_CRAB,
        APPRENTICE_CONJURER,
        APPRENTICE_ENCHANTER,
        DEMON_PRINCE,
        DEMON_CALLER,
        DEMON_WORSHIPPER,
        DEMON_LORD,
        ABYSSAL_FIEND,
        DEMON_VINDICATOR,
        CHAOS_WARLOCK,
        SUCCUBUS,
        INFERI_SORCEROR,
        SATYR_DEMONCALLER,
        INCUBUS,
        LORD_OF_TERROR,
        WARP_HUNTER,
        WARP_STALKER,
        CERBERUS,
        DARK_ANGEL,
        WITCH,
        DARK_FEY,
        DARK_URUK,
        POSSESSED_GARGOYLE,
        SATYR_GLOOMWEAVER,
        PALE_FIEND,
        PSYCHIC_ABERRATION,
        SHARADRIM,
        DARK_SCALES,
        MIST_EYE,
        MURKBORN,
        MURKBORN_DEFILER,
        DEMENTED_PRISONER,
        DEMENTED_WOMAN,
        ABOMINATION,
        MUZZLED_MAN,
        FRENZIED_WOMAN,
        FRENZIED_MAN,
        RABID_MAN,
        CRIMSON_MASKED_CULTIST,
        WOLF_MASKED_CULTIST,
        WORM_MASKED_CULTIST,
        ONYX_MASKED_CULTIST,
        STEEL_MASKED_CULTIST,
        MURK_SPIDER,
        MURK_WEAVER,
        MISTBORN_GARGANTUAN,
        ESCAPED_PRISONER,
        SKULL_MASKED_CULTIST,
        FERAL_CULTIST,
        DEMON_CULTIST,
        SCORCHED_CULTIST,
        PLACEHOLDER_DUNGEON,
        PLACEHOLDER_DUNGEON_CORRUPTED,
        PLACEHOLDER_DUNGEON_MONSTER,
        PLACEHOLDER_CRITTER,
        COLONY_QUEEN,
        ELEDARI_NECROMANCER,
        ELEDARI_DARK_DRUID,
        BLOODMAGE_APPRENTICE,
        DEATH_WORSHIPPER,
        PLAGUE_SERVANT,
        WRAITH_WHISPERER,
        PLAGUE_RAT,
        LICH,
        DEATH_ADEPT,
        PLAGUE_BRINGER,
        SATYR_DEATHUNICODE45CODEENDEATER,
        PLACEHOLDER_UNDEAD,
        PLACEHOLDER_UNDEAD_BOSS,
        SHADOW_ELEMENTAL,
        POISON_ELEMENTAL,
        MAGIC_ELEMENTAL,
        TREANT_SAPLING,
        TREANT,
        SLAAG,
        WYVERN,
        WILLUNICODE45CODEENDOUNICODE45CODEENDWISP,
        ARMORED_WYVERN,
        YOUNG_WYVERN,
        ZOMBIFIED_WYVERN,
        HERMIT,
        FEY,
        KAITHAR_DRUID,
        LUCENT_WISP,
        BATTLE_MONK,
        CRUSADER,
        INQUISITOR,
        VINDICATOR,
        LORD_CARDINAL,
        DEFENDER_OF_LIGHT,
        DEFENDER_OF_FAITH,
        MARTYR,
        RED_DAWN_PRIEST,
        PRIEST_OF_THE_THREE,
        SERVANT_OF_THE_THREE,
        SHAMAN,
        OGRE,
        BASE_HUMAN_UNIT,
        THIEF,
        MARAUDER,
        DESERTER,
        CUTTHROAT,
        GUARDSMAN,
        ORC_RAIDER,
        BARBARIAN,
        FALLEN_KNIGHT,
        PIRATE,
        HUMAN_APOSTATE,
        HUMAN_FIGHTER,
        DARK_APOSTATE,
        DARK_ADEPT,
        BASE_DWARF_UNIT,
        BASE_ELF_UNIT,
        WARLOCK,
        ENCHANTRESS,
        BANDIT_ARCHER,
        ORC_RAVAGER,
        APOSTATE,
        CHAOS_APOSTATE,
        CHAOS_CULTIST,
        FORSWORN_DWARF,
        DWARF_GUARDSMAN,
        GREY_ELF_SCOUT,
        ANTIUNICODE45CODEENDPALADIN,
        SORCERESS,
        CONTAMINATED_DOG,
        GOBLIN_POACHER,
        GOBLIN_WATCHMAN,
        GOBLIN_SKIRMISHER,
        ORC_KHAN,
        ORC_EXECUTIONER,
        GOBLIN_SCOUT,
        BOUNTY_HUNTER,
        RONIN_BALLESTERO,
        HALFUNICODE45CODEENDORC_BOUNCER,
        ASSASSIN,
        CONJURER,
        ORC_CHAMPION,
        DWARF_SHOOTER,
        DWARVEN_MILITIA,
        DWARF_WARRIOR,
        FORSWORN_MINER,
        HILL_GIANT,
        MOUNTAIN_GIANT,
        FROST_GIANT,
        DOGGY,
        STORM_GIANT,
        GIANTESS,
        DWARF_BRAWLER,
        VALKYRIE,
        DWARF_SHARPEYE,
        TROLL,
        DWARF_VETERAN,
        EXILED_THANE,
        NAGA_ASSAILANT,
        SNIPER,
        MERCENARY_RANGER,
        GOBLIN_CREWUNICODE45CODEENDLEADER,
        BARBARIAN_CHIEF,
        BERSERKER,
        RAVENGUARD_CROSSBOWMAN,
        DARK_SHAMAN,
        WAR_MAIDEN,
        BALLESTERO,
        SAVAGE_DWARF,
        PIRATE_SABOTEUR,
        PIRATE_BRUTE,
        PIRATE_SKIRMISHER,
        PIRATE_FIRST_MATE,
        PIRATE_TASKMASTER,
        PIRATE_CAPTAIN,
        GLORY_SEEKER,
        COLDSKIN_WARRIOR,
        COLDSKIN_ELDER,
        NORDHEIM_WARRIOR,
        SLAYER,
        NORDHEIM_CHAMPION,
        BRAWLER,
        ORC_ARCHER,
        ;
    }
    public enum  UNITS_TYPES_CONSTRUCTS implements OBJ_TYPE_ENUM {
        STEEL_GOLEM,
        MECHANICUM_TITAN,
        ;
    }
    public enum  UNITS_TYPES_TUTORIAL implements OBJ_TYPE_ENUM {
        OLD_RUSTY_GOLEM,
        PUNY_SPIDERLING,
        VENOMOUS_HAIRY_SPIDER,
        SMELLY_ZOMBIE,
        UNSUSPECTING_GOBLIN_ARCHER,
        DOOMED_GOBLIN_FODDER,
        GROWLBELLY,
        DESPERATE_ORC_SLAVE,
        ;
    }
    public enum  UNITS_TYPES_LESSER_DEMON implements OBJ_TYPE_ENUM {
        FAMILIAR,
        DEMON_BRUTE,
        HELLGUARD,
        IMP,
        CHAOS_LEGIONNAIRE,
        ;
    }
    public enum  UNITS_TYPES_DEMON implements OBJ_TYPE_ENUM {
        DEMON_CARNIFEX,
        LORD_OF_DESPAIR,
        DEMON_TORMENTOR,
        MISTRESS,
        DEVIL_MAGE,
        LORD_OF_HATRED,
        LORD_OF_PAIN,
        ;
    }
    public enum  UNITS_TYPES_LESSERDEMON implements OBJ_TYPE_ENUM {
        INFERNAL_GOLEM,
        ;
    }
    public enum  UNITS_TYPES_GREATER_DEMON implements OBJ_TYPE_ENUM {
        SCREAMER,
        ;
    }
    public enum  UNITS_TYPES_CORRUPTED implements OBJ_TYPE_ENUM {
        EVIL_EYE,
        GUARDIAN_OF_THE_UNDERWORLD,
        MIND_FLAYER,
        CORRUPTED_MIND_FLAYER,
        TROGLODYTE_MUTANT,
        ;
    }
    public enum  UNITS_TYPES_MONSTER implements OBJ_TYPE_ENUM {
        MANTICORE,
        HYDRA,
        MINOTAUR,
        BLACK_DRAGON,
        MINOTAUR_PRAETOR,
        UNDEAD_BEAST,
        SKELETAL_BEAST,
        ZOMBIE_BEAST,
        VAMPIRE_BEAST,
        UNDEAD_MONSTROCITY,
        BONE_DRAGON,
        PLACEHOLDER_UNDEAD_MONSTER,
        ;
    }
    public enum  UNITS_TYPES_SPIDERS implements OBJ_TYPE_ENUM {
        DEADLY_SPIDER,
        PALE_WEAVER,
        MYCOSA,
        BLACK_WIDOW,
        SPIDERLING,
        SPIDERITE,
        PLACEHOLDER_CRITTER_SPIDERS,
        ;
    }
    public enum  UNITS_TYPES_SHADOW implements OBJ_TYPE_ENUM {
        SHADOW,
        MALEFIC_SHADE,
        ;
    }
    public enum  UNITS_TYPES_PLAGUE implements OBJ_TYPE_ENUM {
        HUSK,
        CORPSEBORN,
        GHOUL,
        INFESTED_HUSK,
        PLAGUE_ZOMBIE,
        HEADLESS_HORSEMAN,
        PLACEHOLDER_UNDEAD_PLAGUE,
        ;
    }
    public enum  UNITS_TYPES_CRIMSON implements OBJ_TYPE_ENUM {
        BLOOD_GHAST,
        BLOOD_REVENANT,
        VAMPIRE,
        VAMPIRE_LORD,
        VAMPIRE_MISTRESS,
        GHAST_ZOMBIE,
        CRIMSON_CHAMPION,
        AFFLICTED_NOBLEMAN,
        PLACEHOLDER_UNDEAD_CRIMSON,
        ;
    }
    public enum  UNITS_TYPES_WRAITH implements OBJ_TYPE_ENUM {
        GHOST,
        WRAITH_BEAST,
        WRAITH,
        KING_OF_THE_DEAD,
        GHAST_WRAITH,
        PLACEHOLDER_UNDEAD_WRAITH,
        ;
    }
    public enum  UNITS_TYPES_PALE implements OBJ_TYPE_ENUM {
        REVENANT,
        WRAITH_KNIGHT,
        ;
    }
    public enum  UNITS_TYPES_DEATH implements OBJ_TYPE_ENUM {
        DEATH_LORD,
        HUORN,
        DEATH_KNIGHT,
        CORPSE_SLUG,
        ;
    }
    public enum  UNITS_TYPES_WILD_ANIMAL implements OBJ_TYPE_ENUM {
        GREY_WOLF,
        WARG,
        DIRE_WARG,
        ;
    }
    public enum  UNITS_TYPES_DIRE_ANIMAL implements OBJ_TYPE_ENUM {
        DIRE_GRIZZLY,
        BLACK_PANTHER,
        GRIFF,
        ;
    }
    public enum  UNITS_TYPES_PET_MONSTER implements OBJ_TYPE_ENUM {
        DRAKE,
        BASILISK,
        GIANT_DRAGONFLY,
        ;
    }
    public enum  UNITS_TYPES_SKY implements OBJ_TYPE_ENUM {
        CLOUD_TITAN,
        AYRIE,
        ;
    }
    public enum UNIT_GROUPS  implements OBJ_TYPE_ENUM {
        //generate!
        ANIMALS,
        APOSTATES,
        ASSEMBLY_OF_MAGI,
        BANDITS,
        BLOOD,
        BRUTES,
        CELESTIALS,
        CHAOS,
        COLONY,
        CONSTRUCTS,
        CRITTERS,
        DEMON_WORSHIPPERS,
        DEMONS,
        DUNGEON,
        FOREST_CREATURES,
        GOBLINS,
        GREENIES,
        GUARDIANS,
        GUARDS,
        HOLY,
        MELEE,
        MERCS_EASY,
        MERCS_FULL,
        MILITIA,
        MUTANTS,
        NIGHT,
        NORSE,
        NORTH,
        ORCS,
        PIRATES,
        PLAGUE,
        RAVENGUARD_FULL,
        RAVENGUARD,
        ROBBERS_EASY,
        ROBBERS,
        SCUM,
        SOLDIERS_FULL,
        SPIDERS,
        THIEVING_CREW,
        TWILIGHT,
        UNDEAD_ADEPT,
        VAMPIRE,
        WARGS,
        WILD,
        WOADS,
        WOLVES,
        DWARVES
    }
}
