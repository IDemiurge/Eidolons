package main.content.enums.entity;

import main.content.CONTENT_CONSTS.SOUNDSET;
import main.content.ContentValsManager;
import main.content.text.Descriptions;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;

import static main.content.enums.entity.HeroEnums.PERK_PARAM.*;

/**
 * Created by JustMe on 2/14/2017.
 */
public class HeroEnums {
    public enum BACKGROUND {
        MAN_OF_KINGS_REALM(RACE.HUMAN, SOUNDSET.ANGEL) {
            @Override
            public String getTypeName() {
                return "Man of King's Realm";
            }
        },
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
        MAN_OF_EAST_EMPIRE(RACE.HUMAN, SOUNDSET.BLACKGUARD),

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
        GREEN_ORC(RACE.GOBLINOID),
        ;
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
            return StringMaster.format(super.toString());
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
            return name().substring(0, 5).equalsIgnoreCase(Strings.WOMAN);
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

        public String getTypeName() {
            return toString();
        }
    }

    public enum CLASS_GROUP {
        SORCERER, WIZARD,
        FIGHTER,
        ROGUE,
        KNIGHT,
        TRICKSTER,
        HERMIT,
        ACOLYTE,
        RANGER,
        MULTICLASS,
        ;

        public String getName() {
            return StringMaster.format(name());
        }
    }

    public enum CLASS_PERK_GROUP {

        FIGHTER(TOUGHNESS, ATTACK, ARMOR, FORTITUDE, ENDURANCE, CARRYING_CAPACITY),
        FIGHTER_SOLDIER(TOUGHNESS, ATTACK, ARMOR, FORTITUDE, ENDURANCE, CARRYING_CAPACITY),
        FIGHTER_WARRIOR(TOUGHNESS, ATTACK, ARMOR, FORTITUDE, ENDURANCE, CARRYING_CAPACITY),

        SQUIRE(LEADERSHIP_MASTERY, RESISTANCE, SPIRIT, CARRYING_CAPACITY, FORTITUDE),
        SQUIRE_KNIGHT(TOUGHNESS, ATTACK, RESISTANCE, SPIRIT, FORTITUDE),
        SQUIRE_OFFICER(LEADERSHIP_MASTERY, SIGHT_RANGE, RESISTANCE, SPIRIT, FORTITUDE),

        ROGUE(STEALTH, ATTACK, TOUGHNESS, INITIATIVE, SNEAK_ATTACK_MOD),
        ROGUE_SWASHBUCKLER(DEFENSE, ATTACK, INITIATIVE, FOCUS_SPEED),
        ROGUE_THUG(STARTING_FOCUS, STEALTH, ATTACK, TOUGHNESS, INITIATIVE, SNEAK_ATTACK_MOD),

        TRICKSTER(DEFENSE, QUICK_SLOTS, STARTING_FOCUS, INITIATIVE, STEALTH, ATTACK, SNEAK_ATTACK_MOD),
        TRICKSTER_ACROBAT(DEFENSE, QUICK_SLOTS, STARTING_FOCUS, INITIATIVE, STEALTH, ATTACK, SNEAK_ATTACK_MOD),
        TRICKSTER_THIEF(DEFENSE, QUICK_SLOTS, STARTING_FOCUS, INITIATIVE, STEALTH, ATTACK, SNEAK_ATTACK_MOD),

        SCOUT(SIGHT_RANGE, STEALTH, DETECTION, ATTACK, INITIATIVE),
        SCOUT_RANGER(SIGHT_RANGE, STEALTH, ATTACK, FOCUS_SPEED, DETECTION, INITIATIVE),

        HERMIT(SPELLPOWER, RESISTANCE, FORTITUDE, SPIRIT, ESSENCE, ESSENCE_ABSORPTION),
        HERMIT_SHAMAN(SPELLPOWER, RESISTANCE_PENETRATION, FORTITUDE, SPIRIT, ESSENCE_ABSORPTION),
        HERMIT_DRUID(KNOWLEDGE_CAP, SPELLPOWER, RESISTANCE, FOCUS_SPEED, SPIRIT, ESSENCE, ESSENCE_ABSORPTION),

        ACOLYTE(DIVINATION_CAP, SPIRIT, ESSENCE, ESSENCE_ABSORPTION, FOCUS_SPEED, RESISTANCE),
        ACOLYTE_CLERIC(DIVINATION_CAP, KNOWLEDGE_CAP, ESSENCE, ESSENCE_ABSORPTION, FOCUS_SPEED, RESISTANCE),
        ACOLYTE_PRIEST(DIVINATION_CAP, SPELLPOWER, ESSENCE, ESSENCE_ABSORPTION, FOCUS_SPEED, RESISTANCE),
        ACOLYTE_MONK(SPIRIT, FOCUS_SPEED, RESISTANCE, DEFENSE, INITIATIVE),

        APOSTATE(RESISTANCE_PENETRATION, SPELLPOWER, ESSENCE_ABSORPTION, STARTING_FOCUS, MEMORIZATION_CAP),
        APOSTATE_DARK(RESISTANCE_PENETRATION, SPELLPOWER, STARTING_FOCUS, MEMORIZATION_CAP),
        APOSTATE_ARCANE(ESSENCE, SPELLPOWER, ESSENCE_ABSORPTION, STARTING_FOCUS, MEMORIZATION_CAP),

        WIZARD_APPRENTICE(KNOWLEDGE_CAP, ESSENCE, FOCUS_SPEED, CARRYING_CAPACITY, MEMORIZATION_CAP),
        WIZARD(KNOWLEDGE_CAP, ESSENCE, ESSENCE_ABSORPTION, FOCUS_SPEED, STARTING_FOCUS, MEMORIZATION_CAP),
        MAGE(SPELLPOWER, ESSENCE, ESSENCE_ABSORPTION, FOCUS_SPEED, STARTING_FOCUS, MEMORIZATION_CAP),

        MULTICLASS,
        ;
        PERK_PARAM[] paramPerks;

        CLASS_PERK_GROUP(PERK_PARAM... paramPerks) {
            this.paramPerks = paramPerks;
        }

        public PERK_PARAM[] getParamPerks() {
            return paramPerks;
        }
    }

    public enum CLASS_TYPE {
        MELEE, SPECIALIST, SPELLCASTER, GODLY
    }

    public enum CUSTOM_HERO_GROUP {
        PLAYTEST, ERSIDRIS, EDALAR, TEST,

    }

    public enum GENDER {
        MALE, FEMALE, NONE
    }

    public enum HERO_SOUNDSET {
        ANGEL("chars/male/angel/", false),
        FIGHTER("chars/male/quiet/", false),
        TOUGH("chars/male/dwarf/", false),
        JOVIAL("chars/male/trickster/", false),
        CAUTOUS("chars/male/rogue/", false),
        LEADER("chars/male/fighter/", false),
        LEARNED("chars/male/wizard/", false),
        VALIANT("chars/male/champion/", false),
        NOBLE("chars/male/noble/", false),
        MANIAC("chars/male/blackguard/", false),

        W_INNOCENT("chars/female/human/", true),
        W_PLAYFUL("chars/female/bad girl/", true),
        W_JOVIAL("chars/female/wood elf/", true),
        W_NOBLE("chars/female/good girl/", true),
        W_VALIANT("chars/female/fighter/", true),
        W_FEISTY("chars/female/feisty/", true),
        W_LEARNED("chars/female/noble/", true),
        W_TOUGH("chars/female/husky/", true),
        W_DEMENTED("chars/female/vampire female/", true),
        W_VILE("chars/female/succubus/", true),

        // ++ quiet =))
        ;
        private final Boolean female;
        private final String path;

        HERO_SOUNDSET(String path, Boolean female) {
            this.path = path;
            this.female = female;
        }

        public String getPropValue() {
            return path;
        }

        public String getName() {
            return StringMaster.format(name().replace("W_", ""));
        }

        @Override
        public String toString() {
            return StringMaster.format(name().replace("W_", "")).toUpperCase();
        }

        public boolean isFemale() {
            return female;
        }

    }

    public enum PERK_UNIQUE {
        APOSTATE_,

    }

    public enum PERK_PASSIVE {
        VIGILANT,
        OPPORTUNISTIC,
        VENGEFUL,
        DISPASSIONATE,
        BLOODTHIRSTY,
        DARKVISION,
        DEXTEROUS,
        AGILE,
        FIRST_STRIKE,
        NO_RETALIATION,
        BERSERKER,
        // RELENTLESS("This unit is unaffected by Stamina effects"),
        // ZOMBIFIED("This unit is unaffected by Focus effects"),
        // SOULLESS("Will not provide Soul counters"), //
        // FLESHLESS("Poison, disease and bleeding have no effect on this unit"),
        // BLIND_FIGHTER("Concealed and Invisible units do not receive bonuses when fighting this unit"),
        // CHARGE("Charge"),
        // TRUE_STRIKE("True Strike"),
        // BROAD_REACH("Can attack melee targets to the sides"),
        // HIND_REACH("Can attack melee targets behind"),
        // CRITICAL_IMMUNE("Immune to Critical Hits"),
        // SNEAK_IMMUNE("Immune to Sneak Attacks"),
        // MIND_AFFECTING_IMMUNE("Immune to Mind-Affecting"),
        // FAVORED("This unit receives full bonus of its deity"),
        // SHORT("Non-Short units do not get their vision or missile attacks obstructed by this unit"),
        // TALL("Does not get its vision or missile attacks obstructed by non-Tall units"),
        // HUGE("")
    }

    public enum PERK_PARAM {
        SPIRIT(3, 7, 15, false, "Indomitable"),
        GRIT(3, 7, 15, false, "Relentless"),
        FORTITUDE(3, 7, 15, false, "Hardy"),
        REFLEX(3, 7, 15, false, "Adroit"),
        WIT(3, 7, 15, false, "Witty"),
        LUCK(3, 7, 15, false, "Lucky"),

        Q_SPIRIT(3, 7, 15, false, "Sycophantic"),
        Q_GRIT(3, 7, 15, false, "Daisy"),
        Q_FORTITUDE(3, 7, 15, false, "Wispy"),
        Q_REFLEX(3, 7, 15, false, "Stiff"),
        Q_WIT(3, 7, 15, false, "Dull"),
        Q_LUCK(3, 7, 15, false, "Unlucky"),

        RESISTANCE(10, 20, 32, false, "Resilient"),
        SPELLPOWER(3, 7, 16, false, "Overpowering"),

        Q_RESISTANCE(10, 20, 32, false, "Vulnerable"),
        Q_SPELLPOWER(3, 7, 16, false, "Feeble"),

        EXTRA_ATTACKS(3, 6, 10, false, "Bold"),
        FREE_MOVES(3, 6, 10, false, "Springy"),

        Q_EXTRA_ATTACKS(3, 6, 10, false, "Reserved"),
        Q_FREE_MOVES(3, 6, 10, false, "Heavy-Footed"),

        INITIATIVE(1, 2, 3, false, "Swift"),
        ATTACK(14, 40, 110, false, "Accurate"),
        DEFENSE(12, 35, 100, false, "Nimble"),
        ARMOR(6, 24, 80, false, "Hard Skinned"),

        Q_INITIATIVE(1, 2, 3, false, "Slow"),
        Q_ATTACK(14, 40, 110, false, "Cannon-shot"),
        Q_DEFENSE(12, 35, 100, false, "Clumsy"),
        Q_ARMOR(6, 24, 80, false, "Thin Skinned"),


        TOUGHNESS(30, 100, 250, false, "Tough"),
        ENDURANCE(75, 250, 650, false, "Steadfast"),
        ESSENCE(45, 110, 320, false, "Aligned"),
        STARTING_FOCUS(7, 15, 30, false, "Focused"),
        FOCUS_SPEED(1, 2, 3, false, "Adaptive"),

        Q_TOUGHNESS(30, 100, 250, false, "Fragile"),
        Q_ENDURANCE(75, 250, 650, false, "Faint"),
        Q_ESSENCE(45, 110, 320, false, "Unquiet"),
        Q_STARTING_FOCUS(7, 15, 30, false, "Distracted"),
        Q_CARRYING_CAPACITY(115, 300, 700, false, "Weak"),
        Q_DETECTION(25, 65, 125, false, "Inattentive"),
        Q_STEALTH(8, 22, 50, false, "Thunderous"),
        Q_FOCUS_SPEED(1, 2, 3, false, "Rigid"),

        CARRYING_CAPACITY(115, 300, 700, false, "Mighty"),
        DETECTION(25, 65, 125, false, "Observant"),
        STEALTH(8, 22, 50, false, "Surreptitious"),

        //UNIQUE PERKS
        RESISTANCE_PENETRATION(21, 42, 70, false, "Theurge"),
        BLOCK_CHANCE(6, 24, 80, false, "Crafty"),
        LEADERSHIP_MASTERY(10, 22, 40, false, "Confident"),

        KNOWLEDGE_CAP(3, 8, 18, false, "Erudite"),
        MEMORIZATION_CAP(14, 35, 80, false, "Prodigious"),
        DIVINATION_CAP(14, 35, 80, false, "Favored"),
        ESSENCE_ABSORPTION(11, 25, 60, false, "Meditative"),

        ARMOR_PENETRATION(6, 24, 80, false, "Deadly"),
        QUICK_SLOTS(2, 5, 10, false, "Resourceful"),
        SNEAK_ATTACK_MOD(25, 40, 75, false, "Cunning"),
        SIGHT_RANGE(1, 2, 3, false, "Sharp Eyed"),


        //sagacious
        ;

        //rolls?

        public PARAMETER param;
        public float[] values;
        public boolean percentage;
        public String name;

        PERK_PARAM(float value1, float value2, float value3, boolean percentage, String name) {
            this.values = new float[3];
            values[0] = value1;
            values[1] = value2;
            values[2] = value3;
            this.percentage = percentage;
            this.name = name;

        }

        public PARAMETER getParam() {
            param = ContentValsManager.getPARAM(name());
            return param;
        }
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
        SPITEFUL(PRINCIPLES.WAR, PRINCIPLES.FREEDOM),
        ;

        PERSONALITY(PRINCIPLES... p) {

        }
    }

    public enum PRINCIPLES {

        WAR, PEACE, HONOR, TREACHERY, LAW, FREEDOM, CHARITY, AMBITION, TRADITION, PROGRESS,
        ;

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
            return StringMaster.format(name());
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
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
        VAMPIRE,
        ;

        RACE() {

        }
    }

    public enum RACIAL_BACKGROUND {
        // as skill?
    }

    public enum STANDARD_HERO_TRAITS {
        VAGABOND, OUTLAW, DISCOMMUNICATED, DISGRACED, FUGITIVE, MERCENARY, ARTIST, MINSTREL,

        // AFFILIATION?

    }

    public enum TRAIT {
        CRIPPLE, MADMAN, ORPHAN, BASTARD,

    }
}
