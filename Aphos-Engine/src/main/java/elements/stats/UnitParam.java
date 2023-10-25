package elements.stats;

import elements.stats.generic.Stat;

/**
 * Created by Alexander on 8/2/2023
 * //TODO flexible naming? without duplicates in the map?
 * //Auto-replace on entity init? AYE maybe
 */
public enum UnitParam implements Stat {
        Health(true),
        Armor(true),
        Soul(true),
        Faith(true),
        Sanity(true),
        AP(true),
        Moves(true),
        Chaos(true),

        //Daemons
        Essence(true),
        Essence_Max,
        Power,

        Health_Max,
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



        DR, //DR - make duplicate entries in map? Not the best idea
        DR_Soul,
        Melee_block,
        Ranged_block,
        Energy_block,

        //Initiative ?
        Ap_retain,
        Moves_retain,
        AP_saved(true),
        Moves_saved(true),
        Initiative(true),

        // Bonus_, should we have a separate param for that?..
        Defense_Auto_Fail(false, true),
        Attack_Auto_Fail(false, true),
        Resist_Auto_Fail(false, true),
        Spell_Auto_Fail(false, true),
        Defense_Auto_Success(false, true),
        Attack_Auto_Success(false, true),
        Resist_Auto_Success(false, true),
        Spell_Auto_Success(false, true),
        ;
        boolean cur;
        boolean bonus; //just to keep it separate from main? transparency aye, but it might complicate some calc
        //if a formula needs final value e.g.!

        UnitParam() {

        }
        UnitParam(boolean cur) {
                this.cur = cur;
        }

        UnitParam(boolean cur, boolean bonus) {
                this.cur = cur;
                this.bonus = bonus;
        }

        public boolean isCur() {
                return cur;
        }

        public boolean isBonus() {
                return bonus;
        }
}
