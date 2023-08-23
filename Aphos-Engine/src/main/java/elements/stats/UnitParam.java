package elements.stats;

import elements.stats.generic.Stat;

/**
 * Created by Alexander on 8/2/2023
 */
public enum UnitParam implements Stat {
        Hp(true),
        Armor(true),
        Soul(true),
        Faith(true),
        Sanity(true),
        AP(true),
        Moves(true),

        Essence(true),
        Essence_Max,
        Power,

        Hp_Max,
        Armor_Max,
        Soul_Max,
        Faith_Max,
        Sanity_Max,
        AP_Max,
        Moves_Max,

        Min_Attack,
        Min_Defense,
        Min_Resistance,
        Base_Attack,
        Base_Defense,
        Base_Resistance,
        Max_Attack,
        Max_Defense,
        Max_Resistance,

        Bonus_,


        Damage_Reduction,
        Melee_block,
        Ranged_block,
        Spell_block,

        //Initiative ?
;
        boolean cur;
        boolean bonus; //just to keep it separate from main? transparency aye, but it might complicate some calc
        //if a formula needs final value e.g.!

        UnitParam() {

        }
        UnitParam(boolean cur) {
                this.cur = cur;
        }

}
