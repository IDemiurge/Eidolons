package elements.stats;

import elements.stats.generic.Stat;

public enum UnitProp implements Stat {
    //bools | qualities
    Daemon, Pure, Infiltrator, Agile, Large,

    //bools | statuses
    Waiting(true), Active(true),FinishedTurn(true),
    Dead(true), Death_Door(true),
    Wound_Body(true),Wound_Head(true),Wound_Limbs(true),
    Summoned(true),
    //containers
    Wards, Immune, Vulnerable,
    Perks, Passives, Extra_Actions,
    //single
    Faction,
    Standard_Attack,Power_Attack,Defense_Action,
    ;
    boolean persistent;

    UnitProp() {
    }

    UnitProp(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean isPersistent() {
        return persistent;
    }
}
