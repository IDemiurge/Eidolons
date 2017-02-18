package main.content.enums.entity;

import main.content.CONTENT_CONSTS.SOUNDSET;
import main.content.text.Descriptions;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

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

    public enum CLASS_TYPE {
        MELEE, SPECIALIST, SPELLCASTER, GODLY
    }

    public   enum CUSTOM_HERO_GROUP {
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
