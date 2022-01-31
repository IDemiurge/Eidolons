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
        public enum EnergyGrade{
        Fel(2, CHAOS, SHADOW),
        ;

        EnergyGrade(int grade, GenericEnums.DAMAGE_TYPE... types) {
            this.grade = grade;
            this.types = types;
        }

        int grade;
        GenericEnums.DAMAGE_TYPE[] types;
    }


}
