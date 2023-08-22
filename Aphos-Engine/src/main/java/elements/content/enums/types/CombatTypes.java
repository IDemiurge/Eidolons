package elements.content.enums.types;

import elements.stats.UnitParam;

/**
 * Created by Alexander on 6/10/2023
 *
 * When a unit targets another unit with attack or spell, their respective Offense (Accuracy or Spellpower) and Defense (Defense or Resistance) values are compared to determine what Hit Type will result.
 * If the lesser of 2 compared values is below 4, the difference must be 3 or greater to result in a Critical or a Miss. Else, the difference must be 2x.
 * E.g. for Defense of 2, Attack equal to 5 is needed to deal a Critical Hit, while for Resistance of 8, Spellpower 4 or lower will be a Miss.
 * For other cases, if Offense value is less than Defense, the Hit Type is Graze
 * If it is equal or greater, it is a normal Hit.
 */
public class CombatTypes {
    //3 above, 3 below; and the diff required is sum of 2/4/8 - 2 6 14
    // And what is "infinity" for damage?
    //apart from damage, where is this relevant?
    //Spells -

    public enum RollGrade{
        Ultimate("Master Blow"),
        Max("Critical"),
        Rnd_Avrg_Max("Fair Blow"),
        // Rnd_Min_Max("Wild Blow"),
        Avrg("Hit"),
        Rnd_Min_Avrg("Feeble Blow"),
        Min("Graze"),
        Miss("Miss")
        ;

        private final String name;

        RollGrade(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum BlockType {
        Melee(UnitParam.Melee_block),
        Ranged(UnitParam.Ranged_block),
        Spell(UnitParam.Spell_block),
        Unblockable(null),
        ;
        // Flame, ?

        BlockType(UnitParam param) {
            this.param = param;
        }

        private UnitParam param;

        public UnitParam getParam() {
            return param;
        }
    }
    public enum DamageType {
        Strike,
        Missile,
        Splash,

        Stun,
        Poison,
        Bleed,

        Death,
        Eldritch, //Nether? Or Cosmic?
        Holy,

        Lightning,
        Fire,
        Cold,

        Hypnos,
        Chaos,
        Psychic,
        ;
        boolean hpOrSoul;
        boolean blockable; //should this be up to the ACTION?
        boolean sanityOrFaithBlock;

        public boolean isHp() {
           return hpOrSoul;
        }
    }
}
