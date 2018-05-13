package main.content.enums.entity;

import main.content.CONTENT_CONSTS.SOUNDSET;
import main.content.text.Descriptions;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import static main.content.enums.entity.HeroEnums.PERK_PARAM.*;

/**
 * Created by JustMe on 2/14/2017.
 */
public class HeroEnums {
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

    public enum CLASS_PERK_GROUP {

        FIGHTER(TOUGHNESS, ATTACK, ARMOR, FORTITUDE, ENDURANCE,  CARRYING_CAPACITY,STAMINA),
        FIGHTER_SOLDIER(TOUGHNESS, ATTACK, ARMOR, FORTITUDE, ENDURANCE,  CARRYING_CAPACITY,STAMINA),
        FIGHTER_WARRIOR(TOUGHNESS, ATTACK, ARMOR, FORTITUDE, ENDURANCE,  CARRYING_CAPACITY,STAMINA),

        SQUIRE(LEADERSHIP_MASTERY, RESISTANCE,SPIRIT, N_OF_COUNTERS, CARRYING_CAPACITY, FORTITUDE, STAMINA),
        SQUIRE_KNIGHT(TOUGHNESS, ATTACK, RESISTANCE, SPIRIT, N_OF_COUNTERS, FORTITUDE, STAMINA),
        SQUIRE_OFFICER(LEADERSHIP_MASTERY, SIGHT_RANGE, RESISTANCE,SPIRIT, FORTITUDE, STAMINA),

        ROGUE(STEALTH, ATTACK, TOUGHNESS, N_OF_ACTIONS, STAMINA, SNEAK_ATTACK_MOD),
        ROGUE_SWASHBUCKLER(DEFENSE, N_OF_COUNTERS, ATTACK, N_OF_ACTIONS, FOCUS_REGEN,STAMINA_REGEN),
        ROGUE_THUG(STARTING_FOCUS, STEALTH, ATTACK, TOUGHNESS, N_OF_ACTIONS,  SNEAK_ATTACK_MOD),

        TRICKSTER(DEFENSE, QUICK_SLOTS, STARTING_FOCUS, N_OF_ACTIONS, STEALTH, ATTACK, SNEAK_ATTACK_MOD),
        TRICKSTER_ACROBAT(DEFENSE, QUICK_SLOTS, STARTING_FOCUS, N_OF_ACTIONS, STEALTH, ATTACK, SNEAK_ATTACK_MOD),
        TRICKSTER_THIEF(DEFENSE, QUICK_SLOTS, STARTING_FOCUS, N_OF_ACTIONS, STEALTH, ATTACK, SNEAK_ATTACK_MOD),

        SCOUT(SIGHT_RANGE, STEALTH, DETECTION, ATTACK,STAMINA_REGEN, N_OF_ACTIONS, STAMINA),
        SCOUT_RANGER(SIGHT_RANGE, STEALTH, ATTACK, ENDURANCE_REGEN, FOCUS_REGEN, STAMINA_REGEN, DETECTION, N_OF_ACTIONS, STAMINA),

        HERMIT(SPELLPOWER,RESISTANCE, FORTITUDE, SPIRIT, ESSENCE, ESSENCE_REGEN, ENDURANCE_REGEN),
        HERMIT_SHAMAN(SPELLPOWER, RESISTANCE_PENETRATION, FORTITUDE, SPIRIT,  ESSENCE_REGEN, ENDURANCE_REGEN),
        HERMIT_DRUID(KNOWLEDGE, SPELLPOWER,RESISTANCE, FOCUS_REGEN,  SPIRIT, ESSENCE, ESSENCE_REGEN),

        ACOLYTE(DIVINATION_CAP, SPIRIT, ESSENCE, ESSENCE_REGEN, STAMINA, FOCUS_REGEN, RESISTANCE),
        ACOLYTE_CLERIC(DIVINATION_CAP, KNOWLEDGE, ESSENCE, ESSENCE_REGEN, FOCUS_REGEN, RESISTANCE),
        ACOLYTE_PRIEST(DIVINATION_CAP, SPELLPOWER, ESSENCE, ESSENCE_REGEN, FOCUS_REGEN, RESISTANCE),
        ACOLYTE_MONK(SPIRIT, FOCUS_REGEN, RESISTANCE, DEFENSE, N_OF_ACTIONS, N_OF_COUNTERS),

        APOSTATE(RESISTANCE_PENETRATION, SPELLPOWER, ESSENCE_REGEN,STARTING_FOCUS,MEMORIZATION_CAP ),
        APOSTATE_DARK(RESISTANCE_PENETRATION, SPELLPOWER, STARTING_FOCUS,MEMORIZATION_CAP ),
        APOSTATE_ARCANE(ESSENCE, SPELLPOWER, ESSENCE_REGEN,STARTING_FOCUS,MEMORIZATION_CAP ),

        WIZARD_APPRENTICE(KNOWLEDGE,  ESSENCE, FOCUS_REGEN, CARRYING_CAPACITY, MEMORIZATION_CAP ),
        WIZARD(KNOWLEDGE,  ESSENCE, ESSENCE_REGEN, FOCUS_REGEN, STARTING_FOCUS, MEMORIZATION_CAP ),
        MAGE(SPELLPOWER, ESSENCE, ESSENCE_REGEN, FOCUS_REGEN, STARTING_FOCUS, MEMORIZATION_CAP ),

        MULTICLASS,;
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

    public enum PERK_PARAM {
        SPIRIT(2, 5, 10, false, "Indomitable"),
        RESISTANCE(8, 16, 24, false, "Resilient"),
        RESISTANCE_PENETRATION(10, 20, 30, false, "Penetrating"),
        SPELLPOWER(3, 7, 16, false, "Overpowering"),
        KNOWLEDGE(3, 8, 18, false, "Erudite"),
        MEMORIZATION_CAP(14, 35, 80, false, "Prodigious"),
        DIVINATION_CAP(14, 35, 80, false, "Favored"),

        N_OF_COUNTERS(2, 5, 10, false, "Drilled"),
        N_OF_ACTIONS(5, 15, 30, false, "Swift"),
        ATTACK(15, 45, 120, false, "Accurate"),
        DEFENSE(12, 35, 100, false, "Nimble"),
        ARMOR(5, 18, 60, false, "Hard Skinned"),
        FORTITUDE(3, 6, 12, false, "Hardy"),

        ENDURANCE_REGEN(15, 45, 135, false, "Undying"),
        STAMINA_REGEN(1, 3, 9, false, "Crafty"),
        ESSENCE_REGEN(7, 20, 50, false, "Meditative"),
        FOCUS_REGEN(4, 10, 25, false, "Recovering"),

        TOUGHNESS(35, 125, 350, false, "Tough"),
        ENDURANCE(65, 250, 650, false, "Steadfast"),
        STAMINA(5, 17, 50, false, "Relentless"),
        ESSENCE(30, 90, 270 , false, "Aligned"),
        STARTING_FOCUS(7, 15, 30, false, "Sharp"),

        CARRYING_CAPACITY(65, 200, 500, false, "Mighty"),
        QUICK_SLOTS(2, 5, 10, false, "Resourceful"),
        SNEAK_ATTACK_MOD(25, 40, 75, false, "Cunning"),
        DETECTION(15, 45, 95, false, "Observant"),
        STEALTH(8, 22, 50, false, "Surreptitious"),
        SIGHT_RANGE(1, 2, 3, false, "Sharp Eyed"),
        LEADERSHIP_MASTERY(10, 22, 40, false, "Confident"),

        //sagacious
        ;

        //rolls?

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
