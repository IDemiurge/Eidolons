package main.elements.costs;

import main.content.values.parameters.PARAMETER;
import main.elements.ReferredElement;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard costs: Spell: Action: Unit Action: AP
 *
 * @author JustMe
 */
public class CostImpl extends ReferredElement implements Cost, Serializable {

    protected CostRequirements requirements;
    protected List<Payment> toPay = new ArrayList<>();
    protected boolean variable = false;
    protected String key = KEYS.SOURCE.toString();
    protected ActiveObj activeObj;
    protected String reason;
    boolean paidAlt = false;
    private Integer active;
    private Payment payment;
    private PARAMETER costParam;
    private Costs altCosts;

    public CostImpl() {

    }

    public CostImpl(Payment payment, PARAMETER costParam) {
        this(payment);
        this.setCostParam(costParam);
    }

    public CostImpl(Payment payment) {
        toPay.add(payment);
        this.setPayment(payment);
    }

    @Override
    public int compare(Cost c) {
        if (c instanceof Costs) {
            return 0;
        }
        if (c.getCostParam() != getCostParam()) {
            return 0;
        }

        return c.getPayment().getAmountFormula().getInt(ref).compareTo(
                getPayment().getAmountFormula().getInt(ref));

    }

    @Override
    public boolean pay(Ref ref) {
        setRef(ref);
        ref.setID(KEYS.PAYEE, ref.getId(key));
        Obj payee = ref.getGame().getObjectById(ref.getId(key));

        boolean result = true;
        if (altCosts != null) {
            if (!canBePaid(ref, true)) {
                paidAlt = true;
                return altCosts.pay(ref);
            }
        }
        for (Payment payment : toPay) {
            result &= payment.pay(payee, ref);
        }
        paidAlt = false;
        return result;
    }

    public boolean canBePaid(Ref REF, boolean noAlt) {
        setRef(REF);
        if (requirements == null) {
            requirements = new CostRequirements(toPay);
        }
        boolean result = requirements.preCheck(ref);
        if (!result) {
            if (!noAlt) {
                if (altCosts != null) {
                    result = altCosts.canBePaid(ref);
                    setReason(altCosts.getReason());
                    // setReasons(altCosts.getReasonList());
                    return result;
                }
            }
        }
        setReason(requirements.getReason());
        return result;
    }

    @Override
    public boolean canBePaid(Ref REF) {
        return canBePaid(REF, false);

    }

    @Override
    public void setRef(Ref REF) {
        super.setRef(REF);
        ref.setID(KEYS.PAYEE, ref.getId(key));
        if (getActiveId() != null) {

            ref.setID(Ref.KEYS.ACTIVE, getActiveId());
        }
    }

    @Override
    public String toString() {
        return

                ((requirements == null) ? "" : "req: " + requirements.toString()) + " "

                        + "to pay: " + toPay.toString();
    }

    public CostRequirements getRequirements() {
        return requirements;
    }

    public void setRequirements(CostRequirements requirements) {
        this.requirements = requirements;
    }

    public boolean isVariable() {
        return variable;
    }

    public void setVariable(boolean variable) {
        this.variable = variable;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean add(Payment e) {
        return toPay.add(e);
    }

    public Integer getActiveId() {
        return active;
    }

    public void setActiveId(Integer active) {
        this.active = active;
    }

    @Override
    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @Override
    public PARAMETER getCostParam() {
        return costParam;
    }

    @Override
    public void setCostParam(PARAMETER costParam) {
        this.costParam = costParam;
    }

    public String getReason() {

        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void addAltCost(Cost cost) {
        if (altCosts == null) {
            altCosts = new Costs(new ArrayList<>());
        }
        altCosts.addCost(cost);

    }

    @Override
    public boolean isPaidAlt() {
        return paidAlt;
    }

    @Override
    public Cost getAltCost() {
        return altCosts;
    }

}
