package eidolons.entity.active.spaces;

import eidolons.ability.DC_CostsFactory;
import eidolons.content.DC_Calculator;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import main.elements.costs.Cost;
import main.elements.costs.Costs;

import java.util.*;

public class UnitActiveSpaces implements Comparator<ActiveSpace> {
    private Unit unit;
    private List<ActiveSpace> activeSpaces;
    private ActiveSpace current;
    private ActiveSpace prev;
    private int switched;

    public UnitActiveSpaces(Unit unit) {
        this.unit = unit;
        activeSpaces = new LinkedList<>();
    }

    public List<ActiveSpace> getVisible() {
        ArrayList<ActiveSpace> list = new ArrayList<>(this.activeSpaces);
        Collections.sort(list, this);
        return list;
    }

    public void add(ActiveSpace space) {
        activeSpaces.add(space);
    }

    public void remove(ActiveSpace space) {
        activeSpaces.remove(space);
    }

    public void newRound() {
        switched = 0;
        if (current == null || current.isLocked()) {
            current = getVisible().get(0);
        }
    }

    public boolean canSwitchTo(ActiveSpace space) {
        return getSwitchCosts(unit).canBePaid(unit);
    }

    public void switchTo(ActiveSpace space) {
        paySwitchCost(unit);
        switched++;
        prev = current;
        current = space;

    }

    private void paySwitchCost(Unit unit) {
        getSwitchCosts(unit).pay(unit.getRef());
    }

    @Override
    public int compare(ActiveSpace o1, ActiveSpace o2) {
        if (o1.type.sortIndex == o2.type.sortIndex) {
            if (o1.index == o2.index)
                return 0;
            return o1.index > o2.index ? 1 : -1;
        }
        return o1.type.sortIndex > o2.type.sortIndex ? 1 : -1;
    }

    private Costs getSwitchCosts(Unit unit) {
        int atb = DC_Calculator.getAS_AtbSwitchCost(unit, switched);
        int focus = DC_Calculator.getAS_FocusSwitchCost(unit, switched);

        List<Cost> costs = new ArrayList<>();
        Cost cost = DC_CostsFactory.getCost(atb, PARAMS.AP_COST, PARAMS.C_ATB, false);
        costs.add(cost);
        cost = DC_CostsFactory.getCost(focus, PARAMS.FOC_COST, PARAMS.C_ATB, false);
        costs.add(cost);
        return new Costs(costs);

    }

    public List<ActiveSpace> getActiveSpaces() {
        return activeSpaces;
    }

    public ActiveSpace getCurrent() {
        return current;
    }

    public ActiveSpace getPrev() {
        return prev;
    }

    public int getSwitched() {
        return switched;
    }
}
