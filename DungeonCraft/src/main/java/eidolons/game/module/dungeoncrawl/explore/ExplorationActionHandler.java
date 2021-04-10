package eidolons.game.module.dungeoncrawl.explore;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.atb.AtbMaster;
import main.content.values.parameters.PARAMETER;
import main.elements.costs.Cost;
import main.elements.costs.Costs;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 9/10/2017.
 */
public class ExplorationActionHandler extends ExplorationHandler {
    private static final List<PARAMETER> ignoredCosts = Arrays.asList(new PARAMETER[]{
            PARAMS.AP_COST,
    });
    private static final String STA_MODIFIER = "0.5";
    private static final String FOC_MODIFIER = "0.5";

    public ExplorationActionHandler(ExplorationMaster master) {
        super(master);
    }

    public static boolean isActivationEnabledByExploration() {
        return false;
    }

    public static float calcBlockingTime(DC_ActiveObj action) {
        float coef = 1f;
        if (action.isAttackAny()) {
            coef = 2f;
        }
        coef /= action.getGame().getDungeonMaster().getExplorationMaster().getTimeMaster().getSpeed();
        return AtbMaster.getReadinessCost(action) / 10000 * coef;

    }

    private static void adjustCosts(Costs costs) {
        costs.getCosts().removeIf(cost ->
                ignoredCosts.contains(cost.getCostParam())
        );
        //        for (PARAMETER p :     modifiedCosts) {
        //            Cost sub = costs.getCost(p);
        //            String modifier=  modifiedCosts.getVar(p);
        //            sub.getPayment().getAmountFormula().append("*"+modifier);
        //        }

        Cost sub = costs.getCost(PARAMS.TOU_COST);
        if (sub != null)
            sub.getPayment().getAmountFormula().append("*" + STA_MODIFIER);
        sub = costs.getCost(PARAMS.FOC_COST);
        if (sub != null)
            sub.getPayment().getAmountFormula().append("*" + FOC_MODIFIER);
    }

    public void playerActionActivated(DC_ActiveObj activeObj, Boolean result) {
        WaitMaster.receiveInput(WAIT_OPERATIONS.PLAYER_ACTION_FINISHED, result);
        //        int time = getTimeForAction(activeObj);
        //        DequeImpl<UnitAI> aiList = master.getAiMaster().getActiveUnitAIs();
        //        aiList.forEach(ai -> ai.setExplorationTimePassed(ai.getExplorationTimePassed() - time));
    }

    public boolean isActivationDisabledByExploration(DC_ActiveObj action) {
        switch (action.getName()) {
            case "Defend":
                return true;
        }
        return false;
    }

    public List<DC_ActiveObj> getExplorationActions(Unit unit) {
        return new ArrayList<>();
    }

    public void payCosts(DC_ActiveObj entity) {
        if (!entity.getOwnerObj().isMine())
            return;
        adjustCosts(entity.getCosts());
        entity.getCosts().pay(entity.getRef());
    }

}
