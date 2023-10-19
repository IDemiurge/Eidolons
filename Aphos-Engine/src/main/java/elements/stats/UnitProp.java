package elements.stats;

import elements.stats.generic.Stat;

public enum UnitProp implements Stat {
    //bools | qualities
    Pure, Infiltrator, Agile, Large, Summoned,
    Dead, Death_Door,

    //bools | statuses
    Wound_Body,Wound_Head,Wound_Limbs,

    //containers
    Wards, Immune, Vulnerable,
    Perks, Passives, Extra_Actions,
    //single
    Faction,
    Standard_Attack,Power_Attack,Defense_Action,
}
