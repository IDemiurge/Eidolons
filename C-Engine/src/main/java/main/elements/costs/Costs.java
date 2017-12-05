package main.elements.costs;

import main.content.values.parameters.PARAMETER;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Costs extends CostImpl {

    private List<Cost> costs;
    private List<String> reasons = new ArrayList<>();

    public Costs(CostRequirements requirements, Cost... costs) {
        this(requirements, new ArrayList<>(Arrays.asList(costs)));
    }

    public Costs(CostRequirements requirements, List<Cost> costs) {
        this.costs = costs;
        this.requirements = requirements;
    }

    public Costs(List<Cost> costs) {
        this.costs = costs;
    }

    public Costs(Map<PARAMETER, Formula> map) {
        costs = new ArrayList<>();
        for (PARAMETER param : map.keySet()) {
            costs.add(new CostImpl(new Payment(param, map.get(param)), param));
        }
    }

    @Override
    public String toString() {
        String req = (requirements == null) ? "" : "req: " + requirements.toString();
        List<String> strings = toStrings();
        if (strings.isEmpty()) {
            return req;
        }
        String costsString = "to pay: ";
        for (String s : strings) {
            costsString += s + ", ";
        }
        costsString = StringMaster.cropLast(costsString, 2, ", ");
        return req + " " + costsString;
    }

    public List<String> toStrings() {
        return toStrings(": ");
    }

    public List<String> toStrings(String separator) {
        List<String> list = new ArrayList<>();
        for (Cost c : getCosts()) {
            try {
                Integer amount = c.getPayment().getAmountFormula().getInt(ref);
                if (amount > 0) {
                    list.add(c.getPayment().getParamToPay().getName() + separator + amount);
                }
            } catch (Exception e) {

            }
        }

        return list;
    }

    public boolean addCost(Cost cost) {
        return costs.add(cost);
    }

    public Cost getCost(PARAMETER param) {
        for (Cost cost : costs) {
            if (cost.getCostParam() == param || cost.getPayment().getParamToPay() == param) {
                return cost;
            }
        }
        return null;
    }

    public void modifyCost(PARAMETER param, String mod) {
        for (Cost cost : costs) {
            if (cost.getCostParam() == param || cost.getPayment().getParamToPay() == param) {
                cost.getPayment().setAmountFormula(
                        new Formula(StringMaster.wrapInParenthesis(cost.getPayment()
                                .getAmountFormula().toString())
                                + mod));
            }
        }
    }

    public void setCost(PARAMETER param, String mod) {
        for (Cost cost : costs) {
            if (cost.getCostParam() == param || cost.getPayment().getParamToPay() == param) {
                cost.getPayment().setAmountFormula(new Formula(mod));
            }
        }
    }

    public void modifyRequirement(PARAMETER param, String mod) {
        // String previous_value = this.requirements.getMap().get(param); TODO
        // if (previous_value == null)
        // previous_value = "";
        // else
        // previous_value = StringMaster.wrapInParenthesis(previous_value);
        // this.requirements.getMap().put(param, previous_value + mod);
    }

    @Override
    public boolean isVariable() {
        boolean result = false;
        for (Cost c : costs) {
            result |= c.isVariable();
        }
        return result;

    }

    public int compare(Cost costs) {

        if (!(costs instanceof Costs)) {
            return 0; // still possible to compare!
        }

        if (equals(costs)) {
            return 0;
        }

        Boolean result = null;
        for (Cost c : getCosts()) {
            Cost c2 = ((Costs) costs).getCost(c.getCostParam());
            if (c2 == null) {
                continue;
            }
            int compare = c.compare(c2);
            if (compare == 0) {
                continue;
            } else {
                if (result == null) {
                    result = compare > 0;
                    continue;
                }
            }
            if (compare > 0) {
                if (!result) {
                    return 0;
                }
            }
            if (compare < 0) {
                if (result) {
                    return 0;
                }
            }

        }
        if (result == null) {
            return 0;
        }
        if (result) {
            return 1;
        }
        return -1;
    }

    @Override
    public boolean pay(Ref ref) {
        setRef(ref);

        boolean result = true;
        for (Cost cost : costs) {
            if (cost != null) {
                result &= cost.pay(this.ref);
            }
        }
        game.fireEvent(new Event(STANDARD_EVENT_TYPE.COSTS_HAVE_BEEN_PAID, ref));
        return result;
    }

    @Override
    public boolean canBePaid(Ref REF) {
        setRef(REF);
        reasons.clear();

        if (requirements != null) {
            if (!requirements.preCheck(ref)) {
                reasons.add(requirements.getReason());
                return false;
            }
        }

        boolean result = true;
        for (Cost cost : costs) {
            try {
                result &= cost.canBePaid(ref);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                return false;
            }
            if (cost.getReason() != null) {
                reasons.add(cost.getReason());
            }
        }

        return result;

    }

    public List<Cost> getCosts() {
        return costs;
    }

    public void setCosts(List<Cost> costs) {
        this.costs = costs;
    }

    public void setActive(ActiveObj active) {
        this.activeObj = active;
        setActiveId(activeObj.getId());
    }

    public List<String> getReasonList() {
        return reasons;
    }

    @Override
    public String getReason() {
        if (reason == null) {
            if (reasons != null) {
                if (reasons.size() > 0) {
                    return reasons.get(0);
                }
            }
        }
        return super.getReason();
    }

    public String getReasonsString() {
        if (getReasonList().size()<2)
            return getReason();
        return StringMaster.joinStringList(getReasonList(), ", ");
    }

    public void removeCost(PARAMETER param) {
        Cost cost = getCost(param);
        if (cost != null) {
            costs.remove(cost);
        }

    }

    public void removeRequirement(String string) {
        if (requirements != null) {
            if (requirements.getReqMap() != null) {
                requirements.getReqMap().remove(string);
            }
        }

    }

}
