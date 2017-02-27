package main.entity.tools.active;

import main.content.PARAMS;
import main.entity.active.DC_ActiveObj;
import main.entity.tools.EntityCalculator;
import main.entity.tools.EntityMaster;

/**
 * Created by JustMe on 2/23/2017.
 */
public class ActiveCalculator extends EntityCalculator<DC_ActiveObj> {

    public ActiveCalculator(DC_ActiveObj entity, EntityMaster<DC_ActiveObj> entityMaster) {
        super(entity, entityMaster);
    }


    public int calculateTimeCost() {
        if (getEntity().getCosts().getCost(PARAMS.C_N_OF_ACTIONS) == null) {
            return 0;
        }
        return  getEntity().getCosts().getCost(PARAMS.C_N_OF_ACTIONS).getPayment()
         .getAmountFormula().getInt(getRef())
         * getEntity().getOwnerObj().getIntParam(PARAMS.INITIATIVE_MODIFIER);

    }
}
