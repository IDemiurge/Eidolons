package combat.turns;

import framework.entity.field.Unit;

import java.util.List;

/**
 * Created by Alexander on 10/21/2023
 */
public class InitiativeGroup {
    protected int maxInitiative;
    protected  List<Unit> units;
    protected boolean ally;

    public InitiativeGroup(List<Unit> units, int maxInitiative, boolean ally) {
        this.maxInitiative = maxInitiative;
        this.units = units;
        this.ally = ally;
    }
}
