package eidolons.game.exploration.handlers;

import eidolons.content.PARAMS;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.game.core.atb.AtbMaster;
import main.content.values.parameters.PARAMETER;
import main.elements.costs.Cost;
import main.elements.costs.Costs;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 9/10/2017.
 */
@Deprecated
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

    @Deprecated
    public static float calcBlockingTime(ActiveObj action) {
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
        Cost sub = costs.getCost(PARAMS.TOU_COST);
        if (sub != null)
            sub.getPayment().getAmountFormula().append("*" + STA_MODIFIER);
        sub = costs.getCost(PARAMS.FOC_COST);
        if (sub != null)
            sub.getPayment().getAmountFormula().append("*" + FOC_MODIFIER);
    }

    public void playerActionActivated(ActiveObj activeObj, Boolean result) {
        WaitMaster.receiveInput(WAIT_OPERATIONS.PLAYER_ACTION_FINISHED, result);
    }


    public void payCosts(ActiveObj entity) {
        if (!entity.getOwnerObj().isMine())
            return;
        adjustCosts(entity.getCosts());
        entity.getCosts().pay(entity.getRef());
    }

}
