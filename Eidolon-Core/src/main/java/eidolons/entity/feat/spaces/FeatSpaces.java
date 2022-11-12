package eidolons.entity.feat.spaces;

import eidolons.ability.costs.DC_CostsFactory;
import eidolons.content.DC_Calculator;
import eidolons.content.PARAMS;
import eidolons.entity.unit.Unit;
import main.elements.costs.Cost;
import main.elements.costs.Costs;

import java.util.*;

public class FeatSpaces implements Comparator<FeatSpace> {
    private Unit unit;
    private List<FeatSpace> featSpaces;
    private FeatSpace current;
    private FeatSpace prev;
    private int switchedThisRound;
    boolean spellSpaces;

    public FeatSpaces(Unit unit, boolean spellSpaces) {
        this.unit = unit;
        this.spellSpaces = spellSpaces;
        featSpaces = new LinkedList<>();
    }

    public List<FeatSpace> getVisible() {
        ArrayList<FeatSpace> list = new ArrayList<>(this.featSpaces);
        Collections.sort(list, this);
        return list;
    }

    public void add(FeatSpace space) {
        featSpaces.add(space);
        if (current == null) {
            current = space;
        }
    }

    public void remove(FeatSpace space) {
        featSpaces.remove(space);
    }

    public void newRound() {
        switchedThisRound = 0;
        if (current == null || current.isLocked()) {
            current = getVisible().get(0);
        }
    }

    public boolean canSwitchTo(FeatSpace space) {
        return getSwitchCosts(unit).canBePaid(unit);
    }

    public void switchTo(FeatSpace space) {
        paySwitchCost(unit);
        switchedThisRound++;
        prev = current;
        current = space;

    }

    private void paySwitchCost(Unit unit) {
        getSwitchCosts(unit).pay(unit.getRef());
    }

    @Override
    public int compare(FeatSpace o1, FeatSpace o2) {
        if (o1.type.sortIndex == o2.type.sortIndex) {
            if (o1.index == o2.index)
                return 0;
            return o1.index > o2.index ? 1 : -1;
        }
        return o1.type.sortIndex > o2.type.sortIndex ? 1 : -1;
    }

    private Costs getSwitchCosts(Unit unit) {
        int atb = DC_Calculator.getAS_AtbSwitchCost(unit, switchedThisRound);
        int focus = DC_Calculator.getAS_FocusSwitchCost(unit, switchedThisRound);

        List<Cost> costs = new ArrayList<>();
        Cost cost = DC_CostsFactory.getCost(atb, PARAMS.AP_COST, PARAMS.C_ATB, false);
        costs.add(cost);
        cost = DC_CostsFactory.getCost(focus, PARAMS.FOC_COST, PARAMS.C_ATB, false);
        costs.add(cost);
        return new Costs(costs);

    }

    public List<FeatSpace> getSpaces() {
        return featSpaces;
    }

    public FeatSpace getCurrent() {
        return current;
    }

    public FeatSpace getPrev() {
        return prev;
    }

    public int getSwitchedThisRound() {
        return switchedThisRound;
    }

    @Override
    public String toString() {
        return   unit +
                "'s  Active Spaces:" + featSpaces;
    }
}
