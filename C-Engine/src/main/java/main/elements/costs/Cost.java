package main.elements.costs;

import main.content.parameters.PARAMETER;
import main.entity.Ref;
import main.entity.Referred;

public interface Cost extends Referred {
    // public boolean isPaid();

    boolean pay(Ref ref);

    boolean canBePaid(Ref ref);

    Integer getActiveId();

    void setActiveId(Integer active);

    boolean isVariable();

    void setVariable(boolean var);

    Payment getPayment();

    PARAMETER getCostParam();

    void setCostParam(PARAMETER costParam);

    String getReason();

    int compare(Cost c);

    void addAltCost(Cost cost);

    boolean isPaidAlt();

    Cost getAltCost();

}
