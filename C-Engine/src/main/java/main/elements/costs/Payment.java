package main.elements.costs;

import main.content.values.parameters.PARAMETER;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.system.math.Formula;

import java.io.Serializable;

public class Payment implements Serializable {

    private PARAMETER valueToPay;
    private Formula amountFormula;
    private Ref ref;
    private int lastPaid;

    public Payment(PARAMETER valueToPay, Formula amountFormula) {
        this.valueToPay = valueToPay;
        this.amountFormula = amountFormula;
    }

    public Payment(PARAMETER p, Integer value) {
        this(p, new Formula(String.valueOf(value)));
    }

    @Override
    public String toString() {
        if (amountFormula.toString().equals("0")
         || amountFormula.toString().equals("")) {
            return "";
        }
        return "Pay: " + amountFormula.toString() + " of "
         + valueToPay.toString();
    }

    public boolean pay(Obj payee, Ref ref) {
        if (valueToPay == null) {
            return true;
        }
        Number n = amountFormula.evaluate(ref);
        if (n instanceof Double) {
            this.ref = ref;
        }
        lastPaid = amountFormula.getInt(ref);
        return payee.modifyParameter(valueToPay, -lastPaid);
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

    public int getLastPaid() {
        return lastPaid;
    }

    public void setValueToPay(PARAMETER valueToPay) {
        this.valueToPay = valueToPay;
    }
}
