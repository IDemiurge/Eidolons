package eidolons.netherflame.feature.reflectItem.data;

import main.content.enums.GenericEnums;

import static main.content.enums.GenericEnums.DAMAGE_TYPE.*;

public class Inv2Enums {

    public enum PrismSlot{
        WeaponSet1,
        WeaponSet2,
        Armor,
        Garments,
        Jewelry,
        Tokens

    }
    //if we wanted to make this extendable by modders, how to do it? We cannot use enum-switch then for sure ... but must instead rely on String-based format
        public enum SigilType{
        Warrior("Brute","Warrior", "Champion" ), //two-handed or dual brute weapons
        Assassin("Rogue","Assassin", "Slayer" ), //dual blades
        Knight("Squire","Knight", "Guardian" ),
        Archer("Archer","Marksman", "Sniper" ),
        Wizard("Apprentice","Wizard", "Archmage" ),
        Battle_Mage("Spellsword","Battle Mage", "Eldritch Warrior" ), //sword&sphere,
        Trickster("Trickster","Daredevil", "Shadow" )  //unarmed, exotic, crossbows, firearms,
        ;

            SigilType(String... gradeNames) {
                this.gradeNames = gradeNames;
            }

            String[] gradeNames;
    }
    /*
    3 tiers of single-energy, and custom 2-type hybrids
    physical?
    this one would be good to have via STRING for modding extension!
    would be nice to use some yaml or such!

    Same for all materials?
    :: Adjectives ::

     */
        public enum EnergyGrade{
        //TODO Content missing
        Unearthly(1),
        Sanguine(1),
        Boreal(1),
        Pearlescent(1),
        Luminous(1),
        Jade(1),

        Whisper(true, 1),
        Storm(true, 1),
        Doom(true, 1),

        Fel(2, CHAOS, SHADOW),
        ;
        boolean useHyphen; //truncate material name? Whisper-Adamantium doesn't sound too good... but who cares?

        EnergyGrade(int grade, GenericEnums.DAMAGE_TYPE... types) {
            this(false, grade, types);
        }
            EnergyGrade(boolean useHyphen, int grade, GenericEnums.DAMAGE_TYPE... types) {
            this.grade = grade;
            this.types = types;
            switch (types[0]) {
                case POISON:
                    break;
                case FIRE:
                    break;
                case COLD:
                    break;
                case LIGHTNING:
                    break;
                case ACID:
                    break;
                case SONIC:
                    break;
                case LIGHT:
                    break;
                case ARCANE:
                    break;
                case CHAOS:
                    break;
                case SHADOW:
                    break;
                case HOLY:
                    break;
                case DEATH:
                    break;
                case PSIONIC:
                    break;
                case PHYSICAL:
                    break;
                case PURE:
                    break;
                case MAGICAL:
                    break;
            }
        }

        int grade;
        GenericEnums.DAMAGE_TYPE[] types;
    }


}
