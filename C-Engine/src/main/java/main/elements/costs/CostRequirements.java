package main.elements.costs;

import main.data.XLinkedMap;
import main.elements.conditions.Condition;
import main.elements.conditions.NumericCondition;
import main.elements.conditions.Requirement;
import main.elements.conditions.Requirements;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.math.Formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CostRequirements extends Requirements {

    private List<Payment> paymentList = new ArrayList<>();
    private Map<String, Condition> additionalReqList = new XLinkedMap<>();

    public CostRequirements(Payment p) {
        paymentList.add(p);
    }

    public CostRequirements(List<Payment> toPay) {
        this.paymentList = toPay;
    }

    public CostRequirements() {

    }

    @Override
    public void add(Requirement r) {
        additionalReqList.put(r.getText(), r.getCondition());
    }

    public void add(Payment p) {
        paymentList.add(p);
    }

    private void initConditions(List<Payment> toPay, Ref ref) {
        reqMap.clear();
        reqMap.putAll(additionalReqList);
        for (Payment payment : toPay) {
            if (payment.getParamToPay() == null) {
                continue;
            }
            if (payment.getAmountFormula().toString().equals("0")) {
                continue;
            }
            Formula amountFormula = payment.getAmountFormula();
            if (amountFormula.toString().contains(
             StringMaster.FORMULA_FUNC_OPEN_CHAR)) {
                continue;
            }
            String value = "" + amountFormula.getInt(ref);
            String r = InfoMaster.getParamReasonString(value,
             payment.getParamToPay());
            Condition c = new NumericCondition(false,
             StringMaster.getValueRef(KEYS.SOURCE.toString(), payment
              .getParamToPay().toString()), value);
            reqMap.put(r, c);

        }

    }

    public boolean preCheck(Ref ref) {
        // if (var)
        try {
            initConditions(paymentList, ref);
        } catch (Exception e) {
            return false;
        }
        try {
            super.preCheck(ref);
        } catch (Exception e) {
            return false;
        }
        return getReason() == null;
    }

}
