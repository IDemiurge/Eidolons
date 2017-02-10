package main.elements.costs.old;

import main.entity.Ref;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Costs implements Cost {

    List<Cost> costs = new LinkedList<>();
    List<AlternativeCost> altCosts = new LinkedList<>();

    public Costs(Cost[] costs) {
        this.costs = Arrays.asList(costs);
    }

    public Costs(String string) {
        String[] tempArray = string.split(";");
        for (String s : tempArray) {
            costs.add(new CostImpl(s));
        }

    }

    public List<AlternativeCost> getAlternativeCosts() {
        List<AlternativeCost> list = new LinkedList<>();
        for (Cost cost : costs) {
            if (cost instanceof AlternativeCost) {
                list.add((AlternativeCost) cost);
            }
        }

        return list;
    }

    public int getPaidAmount() {
        return altCosts.get(0).getPaidAmount();
    }

    public int getPaidAmount(int i) {
        if (i >= 0 && i <= altCosts.size()) {
            return altCosts.get(i).getPaidAmount();
        }
        return 0;
    }

    public List<Integer> getPaidAmountList() {
        List<Integer> list = new LinkedList<>();
        for (AlternativeCost altcost : altCosts) {
            list.add(altcost.getPaidAmount());
        }
        return list;
    }

    public boolean canBePaid() {
        boolean result = true;
        for (Cost cost : costs) {
            result &= cost.canBePaid(getRef());
        }
        for (AlternativeCost cost : altCosts) {
            result &= cost.canBePaid();
        }
        return result;
    }

    @Override
    public Ref getRef() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setRef(Ref ref) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isPaid() {
        return false;
    }

    @Override
    public boolean pay(SoEObj payee, Ref ref) {
        return false;
    }

    @Override
    public boolean canBePaid(Ref ref) {
        // TODO Auto-generated method stub
        return false;
    }

    public void setVariable(boolean var) {
        // TODO Auto-generated method stub

    }
}
