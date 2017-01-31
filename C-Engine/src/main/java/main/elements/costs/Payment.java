package main.elements.costs;

import main.content.parameters.PARAMETER;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.system.auxiliary.RandomWizard;
import main.system.math.Formula;

import java.io.Serializable;

public class Payment implements Serializable {

    private PARAMETER valueToPay;
    private Formula amountFormula;
    private Ref ref;

    public Payment(PARAMETER valueToPay, Formula amountFormula) {
        this.valueToPay = valueToPay;
        this.amountFormula = amountFormula;
    }

    @Override
    public String toString() {
        if (amountFormula.toString().equals("0")
                || amountFormula.toString().equals(""))
            return "";
        return "Pay: " + amountFormula.toString() + " of "
                + valueToPay.toString();
    }

    public boolean pay(Obj payee, Ref ref) {
        Number n = amountFormula.evaluate();

        if (n instanceof Double) {
            this.ref = ref;
            if (isRolledRounded()) {
                Double D = (Double) n;
                int chance = (int) (D * 100 + 0) % 100;
                if (chance >= 50) {
                    boolean up = RandomWizard.chance(chance);
                    int i = (int) Math.round((n.doubleValue()));
                    if (!up) {
                        try {
                            ref.getGame().getLogManager()
                                    .logFastAction(payee, ref);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        i--;
                    }
                    return payee.modifyParameter(valueToPay, -i);
                }
            }

        }
        int amount = -amountFormula.getInt(ref);
        return payee.modifyParameter(valueToPay, amount);
    }

    private boolean isRolledRounded() {
        return ref.getGame().getValueManager().isRolledRoundind(valueToPay);
    }

    public PARAMETER getParamToPay() {
        return valueToPay;
    }

    public Formula getAmountFormula() {
        return amountFormula;
    }

    public void setAmountFormula(Formula amountFormula) {
        this.amountFormula = amountFormula;
    }

    public void setValueToPay(PARAMETER valueToPay) {
        this.valueToPay = valueToPay;
    }
}
