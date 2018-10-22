package eidolons.game.module.dungeoncrawl.explore;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.AiActionFactory;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.explore.behavior.AiBehavior;
import eidolons.game.core.ActionInput;
import eidolons.game.core.Eidolons;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.game.logic.action.context.Context;
import main.system.datatypes.DequeImpl;
import main.system.math.PositionMaster;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 9/9/2017.
 */
public class ExplorationAiMaster extends ExplorationHandler {

    private DequeImpl<UnitAI> activeUnitAIs;
    private boolean aiActs;
    private DequeImpl<ActionInput> aiActionQueue;
    private Set<Unit> allies;

    public ExplorationAiMaster(ExplorationMaster master) {
        super(master);
        aiActionQueue = new DequeImpl<>();
        activeUnitAIs = new DequeImpl<>();
    }

    public void act(float delta) {
        master.getGame().getAiManager().getBehaviorManager().act(delta);
    }
    public void reset() {

        allies = master.getGame().getPlayer(true).getControlledUnits_();
        activeUnitAIs.clear();
        master.getGame().getUnits().forEach(unit ->
         {
             if (!unit.getAI().isAutoFollow())
                 if (unit.isAiControlled()) {
                     if (unit.canActNow())
                         activeUnitAIs.add(unit.getAI());
                 }
         }
        );
    }


    public void checkAiActs() {
//        master.getGame().getAiManager().getBehaviorMaster().
        activeUnitAIs.forEach(ai -> ai.setExplorationTimePassed(
         master.getTimeMaster().getTime() - ai.getExplorationTimeOfLastAction()));
//        if (isAiActs())
//            return;
        boolean isAiActs = false;
        for (UnitAI ai : activeUnitAIs) {
            if (!ExplorationMaster.isExplorationOn())
                return;
            if (ai.getExplorationTimePassed() <= ExplorationTimeMaster.secondsPerAP)
                continue;
            double distance = PositionMaster.getExactDistance(ai.getUnit().getCoordinates(),
             Eidolons.getMainHero().getCoordinates());
            if (distance > getMaxDistance(ai))
                continue;
            try {
                if (tryMoveAi(ai))
                    isAiActs = true;
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        }
        if (isAiActs) {
            aiActs = true;
            if (ExplorationMaster.isExplorationOn())
                master.getLoop().signal();
        }
    }

    private double getMaxDistance(UnitAI ai) {
        return 5;
    }

    private boolean tryMoveAi(UnitAI ai) {
        ActionSequence orders = ai.getStandingOrders();
        if (ai.getStandingOrders() == null) {
            ai.setStandingOrders(orders =getOrders(ai));
        }
        if (orders == null) {
            return false; //TODO concurrency fail - someone set orders outside this thread!
        }
        Double cost = orders.getCurrentAction().getActive().
         getParamDouble(PARAMS.AP_COST);
        int timePassed = master.getTimeMaster().getTimePassedSinceAiActions(ai);
        //ai.getExplorationTimePassed()
        if (timePassed >= Math.round(cost *
         ExplorationTimeMaster.secondsPerAP * ai.getExplorationMoveSpeedMod())) {
            aiMoves(ai);
            return true;
        }

        return false;
    }

    public void tryMoveAiTurnBased(float timePercentage) {
        reset();
        for (UnitAI ai : getActiveUnitAIs(true)) {
            tryMoveAiTurnBased(ai, timePercentage);
        }
    }

    private boolean tryMoveAiTurnBased(UnitAI ai, float timePercentage) {
        if (ai.getStandingOrders() == null) {
            try {
                ai.setStandingOrders(getOrders(ai));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                return false;
            }
        }
        if (ai.getStandingOrders() == null) {
            return false;
        }
        Double cost = ai.getStandingOrders().getCurrentAction().getActive().
         getParamDouble(PARAMS.AP_COST) / ai.getUnit().getIntParam(PARAMS.N_OF_ACTIONS);
        if (timePercentage >= cost) {
            ActionInput input = new ActionInput(
             ai.getStandingOrders().getCurrentAction().getActive(),
             new Context(ai.getStandingOrders().getCurrentAction().getRef()));
            master.getGame().getGameLoop().actionInput(input);
            return true;
        }
        return false;
    }

    private void aiMoves(UnitAI ai) {


        ActionSequence orders = ai.getStandingOrders();
        ActionInput input = new ActionInput(
         orders.getCurrentAction().getActive(),
         new Context(orders.getCurrentAction().getRef()));
        queueAiAction(input);
        ai.setExplorationTimeOfLastAction(master.getTimeMaster().getTime());
        if (orders.getLastAction() != null)
            if (orders.getCurrentAction().getActive() ==
             orders.getLastAction().getActive())
                ai.setStandingOrders(null);

    }

    public void queueAiAction(ActionInput input) {
        aiActionQueue.add(input);
    }

    private ActionSequence getOrders(UnitAI ai) {

        if (ai.getUnit().isMine()) //TODO
            if (master.getPartyMaster().isFollowOn(ai.getUnit())) { //isFollow
                Action move = master.getPartyMaster().getFollowMove(ai.getUnit());
                if (move == null) {
                    return null;
//                    return getIdleOrders(ai);
                } else {
                    return new ActionSequence(GOAL_TYPE.MOVE, move);
                }
            }
        //get orders?
        AiBehavior behavior = getAiBehavior(ai);
        ActionSequence orders = behavior.getOrders();
        if (orders == null) {
//            orders= getIdleOrders(ai);
        }
        return orders;
    }

    private AiBehavior getAiBehavior(UnitAI ai) {
        //set as field?
        return master.getGame().getAiManager().getBehaviorMaster().getBehavior(ai);
    }

    private ActionSequence getIdleOrders(UnitAI ai) {
        return new ActionSequence(GOAL_TYPE.IDLE, AiActionFactory.newAction("Idle", ai));
    }


    public DequeImpl<UnitAI> getActiveUnitAIs() {
        return getActiveUnitAIs(false);
    }

    public DequeImpl<UnitAI> getActiveUnitAIs(boolean outOfBattleOnly) {
        if (outOfBattleOnly) {
            DequeImpl<UnitAI> d = new DequeImpl<UnitAI>(activeUnitAIs);
            d.removeIf(ai -> !ai.isOutsideCombat());
            return d;
        }
        return activeUnitAIs;
    }


    public boolean isAiActs() {
        return aiActs;
    }

    public void setAiActs(boolean aiActs) {
        this.aiActs = aiActs;
    }

    public DequeImpl<ActionInput> getAiActionQueue() {
        return aiActionQueue;
    }

    public Set<Unit> getAllies() {
        return allies;
    }

    public DequeImpl<UnitAI> getAlliesAndActiveUnitAIs(boolean outOfBattleOnly) {

        DequeImpl<UnitAI> deque = new DequeImpl<>(allies.stream().map
         (unit -> unit.getAI()).collect(Collectors.toList()), getActiveUnitAIs());

        deque.removeIf(ai -> ai.getUnit().isAnnihilated() ||
         (outOfBattleOnly && !ai.isOutsideCombat()));
        return deque;
    }

}
