package main.elements.costs.old;

import main.entity.Ref;

public abstract class AlternativeCost extends CostImpl {
    private int paidAmount;

    public AlternativeCost(String s) {
        super(s);
        // TODO Auto-generated constructor stub
    }

    public boolean checkIfUnitCanPay(SoEObj unit) {
        Ref tempref = new Ref();
        tempref.setSource(unit.getId());
        return canPayConditions.check(tempref);
    }

    public abstract boolean canBePaid();

    @Override
    public boolean isPaid() {
        return paid;
    }

    public abstract void pay();

    public int getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(int paidAmount) {
        this.paidAmount = paidAmount;
    }

    public enum ALT_COST_TYPES {
        SACRIFICE_CREATURE,
        SACRIFICE_ITEM,
        SACRIFICE_BUFF,
        SACRIFICE_SPELL,
        TAKE_DAMAGE,
    }

}
