package main.content.enums.entity;

import main.content.CONTENT_CONSTS.SOUNDSET;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;

import static main.content.enums.entity.Hero2Enums.*;
import static main.content.enums.entity.PerkEnums.PERK_PARAM.*;

/**
 * Created by JustMe on 2/14/2017.
 */
public class HeroEnums {
    /*
    C | U | R
    1 racial q/perk, attribute bonus, RES/VUL (other grades?), Talent defaults (side to be picked)
    > Via Entity? Sure, that's a good way to keep it up to date via Hierarchy
    BUT we'd probably want to generate from code!
    profession bans

    highlander - Eagle?.. I've got savages and nords so far... could make some 'handsome rich knights' too ofc
    we've got enough brutes among dwarves? Nordheimers => Titanborn
     */
    public enum RACE {
        HUMAN(RACE_RARITY.C),
        ELF(RACE_RARITY.C),
        DWARF(RACE_RARITY.C),

        GODLING(RACE_RARITY.U),
        ENGEL(RACE_RARITY.U),
        MORTEN(RACE_RARITY.U),

        HYBRID(RACE_RARITY.R),
        FIEND(RACE_RARITY.R),
        HOLLOW(RACE_RARITY.R),
        ;
        RACE_RARITY rarity;

        RACE(RACE_RARITY rarity) {
            this.rarity = rarity;
        }
    }

    /*
🕀 Crownlander
✙ Highlander
❖ Ravenren
✠ Ersidrian
❖ Vesuvian
⛧ Imperial

Dwarf
🕀 Runesmith
✙ Stoneshield
☸ Frostbeard
❖ Ironhelm
✧ Grimbart

🕀 Sun
✙ Fey
❖ Dark
✠ Crimson
⛧ Pale
     */
    public enum SUBRACE {
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

        ;
        private RACE race;
        private SOUNDSET[] soundsets;

        String professionsBanned;


        SUBRACE(RACE race, SOUNDSET... soundsets) {
            this.setRace(race);
            this.setSoundsets(soundsets);
        }

        public SUBRACE getMale() {
            if (getRace() == RACE.HUMAN) {
                SUBRACE male = new EnumMaster<SUBRACE>().retrieveEnumConst(SUBRACE.class,
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

    /*
apply via entity as well?
Groups - by Talent?
++ Race-Specific
     */
    public enum BACKGROUND{

    }

    public enum CUSTOM_HERO_GROUP {
        PLAYTEST, ERSIDRIS, EDALAR, TEST,

    }

    public enum GENDER {
        MALE, FEMALE, NONE
    }

    public enum HERO_OPERATION {
        PICK_UP, DROP, UNEQUIP,  UNEQUIP_QUICK_SLOT, EQUIP, EQUIP_QUICK_SLOT, UNEQUIP_JEWELRY,
        ATTRIBUTE_INCREMENT,
        MASTERY_INCREMENT,
        NEW_MASTERY,

        NEW_SKILL, SKILL_RANK,
        NEW_CLASS, CLASS_RANK,

        SPELL_LEARNED,
        NEW_PERK, LEVEL_UP,
        SET_PROPERTY, SET_PARAMETER,   ADD_PARAMETER,

        APPLY_TYPE,

        BUY, SELL, UNSTASH, STASH, EQUIP_RESERVE,



    }
}
