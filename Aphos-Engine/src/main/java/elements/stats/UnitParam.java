package elements.stats;

import elements.stats.generic.Stat;

/**
 * Created by Alexander on 8/2/2023
 * flexible naming?
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

        Attack_Min,
        Attack_Base,
        Attack_Max,

        Defense_Min,
        Defense_Base,
        Defense_Max,

        Resistance_Min,
        Resistance_Base,
        Resistance_Max,

        Bonus_,


        DR, //DR - make duplicate entries in map? Not the best idea
        DR_Soul,
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
