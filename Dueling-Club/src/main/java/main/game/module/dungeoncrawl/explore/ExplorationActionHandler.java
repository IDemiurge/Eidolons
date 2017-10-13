package main.game.module.dungeoncrawl.explore;

import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.elements.costs.Cost;
import main.elements.costs.Costs;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 9/10/2017.
 */
public class ExplorationActionHandler extends ExplorationHandler {
    private static List<PARAMETER> ignoredCosts = Arrays.asList(new PARAMETER[]{
     PARAMS.AP_COST,
    });
    private static String STA_MODIFIER="0.33";

    public ExplorationActionHandler(ExplorationMaster master) {
        super(master);
    }

    public static boolean isActivationEnabledByExploration() {
//searchMode - why not? some alternative? time? food/torch...
        //restoration modes? keep restoring until <?>
        // full restoration mode
        // auto-pathing

        return false;
    }
    public void playerActionActivated(DC_ActiveObj activeObj) {

//        int time = getTimeForAction(activeObj);
//        DequeImpl<UnitAI> aiList = master.getAiMaster().getActiveUnitAIs();
//        aiList.forEach(ai -> ai.setExplorationTimePassed(ai.getExplorationTimePassed() - time));
    }
    public   boolean isActivationDisabledByExploration(DC_ActiveObj action) {
switch (action.getName()){
    case "Defend":
        return true;
}
        return false;
    }

    public List<DC_ActiveObj> getExplorationActions(Unit unit) {
        List<DC_ActiveObj> list = new LinkedList<>();
        return list;
    }
    private static void adjustCosts(Costs costs) {
        costs.getCosts().removeIf(cost ->
         ignoredCosts.contains(cost.getCostParam())
        );
//        for (PARAMETER p :     modifiedCosts) {
//            Cost sub = costs.getCost(p);
//            String modifier=  modifiedCosts.get(p);
//            sub.getPayment().getAmountFormula().append("*"+modifier);
//        }

        Cost sub = costs.getCost(PARAMS.STA_COST);
        if (sub!=null )
        sub.getPayment().getAmountFormula().append("*"+STA_MODIFIER);
            sub = costs.getCost(PARAMS.FOC_COST);
        if (sub!=null )
        sub.getPayment().getAmountFormula().append("*"+STA_MODIFIER);
    }

    public void payCosts(DC_ActiveObj entity) {
        adjustCosts(entity.getCosts());
        entity.getCosts().pay(entity.getRef());
    }

}
