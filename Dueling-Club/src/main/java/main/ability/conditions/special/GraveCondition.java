package main.ability.conditions.special;

import main.elements.Filter;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.MicroCondition;
import main.entity.Entity;
import main.entity.obj.DC_Cell;
import main.entity.obj.Obj;
import main.system.entity.ConditionMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.List;
import java.util.Set;

public class GraveCondition extends MicroCondition {

    private Conditions conditions;

    public GraveCondition() {
        this(ConditionMaster.getStdRaiseConditions());
    }

    // for resurrect this will be Ownership, e.g.
    public GraveCondition(Condition c) {
        this.conditions = new Conditions(c);
    }

    @Override
    public boolean check() {
        List<Obj> deadUnits = ref.getGame().getGraveyardManager().getDeadUnits(
                ref.getMatchObj().getCoordinates());
        if (!ListMaster.isNotEmpty(deadUnits)) {
            return false;
        }
        Set<Entity> filtered = new Filter<>(deadUnits, getRef(), getConditions()).getObjects();

        if (!(ref.getMatchObj() instanceof DC_Cell)) {
            return filtered.contains(ref.getMatchObj());
        }
        // for (Obj unit : //currently only top...
        if (ListMaster.isNotEmpty(deadUnits)) {
            return (conditions.check(deadUnits.get(0)));
        }
        return false;
    }

    public Conditions getConditions() {
        return conditions;
    }
}
