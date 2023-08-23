package elements.stats;

import elements.stats.generic.Stat;

/**
 * Created by Alexander on 8/2/2023
 */
public enum ActionParam implements Stat {

        Burn_Requirement,

        AP_Cost,
        MP_Cost,

        Min_Value, //can be heal or other stuff like counters
        Base_Value,
        Max_Value,


        Hp_Cost,
        Soul_Cost,
        Faith_Cost,
        Sanity_Cost,
}
