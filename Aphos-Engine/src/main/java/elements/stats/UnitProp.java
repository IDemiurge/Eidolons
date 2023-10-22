package elements.stats;

import elements.stats.generic.Stat;

public enum UnitProp implements Stat {
    //bools | qualities
    Daemon, Pure, Infiltrator, Agile, Large,

    //bools | statuses
    Waiting, Active,FinishedTurn,
    Dead, Death_Door,
    Wound_Body,Wound_Head,Wound_Limbs,
    Summoned,
    //containers
    Wards, Immune, Vulnerable,
    Perks, Passives, Extra_Actions,
    //single
    Faction,
    Standard_Attack,Power_Attack,Defense_Action,
}
