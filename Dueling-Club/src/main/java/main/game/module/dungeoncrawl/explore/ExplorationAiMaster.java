package main.game.module.dungeoncrawl.explore;

import main.content.PARAMS;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.actions.AiActionFactory;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.ai.elements.task.Task;
import main.game.battlecraft.ai.tools.path.ActionPath;
import main.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.battlecraft.logic.dungeon.universal.Positioner;
import main.game.bf.Coordinates;
import main.game.core.ActionInput;
import main.game.logic.action.context.Context;
import main.game.module.dungeoncrawl.ai.WanderMaster;
import main.system.datatypes.DequeImpl;

import java.util.LinkedList;
import java.util.List;
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

    public void reset() {

        allies = master.getGame().getPlayer(true).getControlledUnits_();
        activeUnitAIs.clear();
        master.getGame().getUnits().forEach(unit ->
         {
             if (!unit.getAI().isAttached())
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
        if (isAiActs())
            return;
        boolean isAiActs = false;
        for (UnitAI ai : activeUnitAIs) {
            if (ai.getExplorationTimePassed() <= ExplorationTimeMaster.secondsPerAP)
                continue;
            try {
                if (tryMoveAi(ai))
                    isAiActs = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (isAiActs)
            aiActs = true;
    }

    private boolean tryMoveAi(UnitAI ai) {
        if (ai.getStandingOrders() == null) {
            ai.setStandingOrders(getOrders(ai));
        }
        Double cost = ai.getStandingOrders().getCurrentAction().getActive().
         getParamDouble(PARAMS.AP_COST);
        int timePassed = master.getTimeMaster().getTimePassedSinceAiActions(ai);
        //ai.getExplorationTimePassed()
        if (timePassed >= Math.round(cost *
         ExplorationTimeMaster.secondsPerAP)) {
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
            ai.setStandingOrders(getOrders(ai));
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


        ActionInput input = new ActionInput(
         ai.getStandingOrders().getCurrentAction().getActive(),
         new Context(ai.getStandingOrders().getCurrentAction().getRef()));
        queueAiAction(input);
        ai.setExplorationTimeOfLastAction(master.getTimeMaster().getTime());
        if (ai.getStandingOrders().getCurrentAction() == ai.getStandingOrders().getLastAction())
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
                    return new ActionSequence(GOAL_TYPE.IDLE, AiActionFactory.newAction("Cower", ai));
                } else {
                    return new ActionSequence(GOAL_TYPE.MOVE, move);
                }
            }
        //get orders?

        try {
            return getWanderOrders(ai);
        } catch (Exception e) {
            e.printStackTrace();
            return new ActionSequence(GOAL_TYPE.IDLE, AiActionFactory.newAction("Cower", ai));
        }
    }

    private ActionSequence getWanderOrders(UnitAI ai) {
        Coordinates c1 = null;
        try {
            WanderMaster.checkWanderDirectionChange(ai.getGroup(), GOAL_TYPE.WANDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            c1 = (WanderMaster.getWanderTargetCoordinatesCell(ai, GOAL_TYPE.WANDER));
        } catch (Exception e) {
            c1 = (CoordinatesMaster.getRandomAdjacentCoordinate(ai.getUnit().getCoordinates()));
            e.printStackTrace();
        }
        c1 = Positioner.adjustCoordinate(ai.getUnit(), c1, ai.getUnit().getFacing());

        List<Coordinates> c = new LinkedList<>();
        c.add(c1);
        master.getGame().getAiManager().setUnit(ai.getUnit());
//        master.getGame().getAiManager().getPathBuilder().init(null, null);
//        TimeLimitMaster.markTimeForAI(ai);
        List<ActionPath> paths = new LinkedList<>();
//         master.getGame().getAiManager().getPathBuilder().build(c);

        Task task = new Task(ai, GOAL_TYPE.WANDER, null);
        Action action = null;
        if (paths.isEmpty()) {
            if (c.get(0) != null)
                action = master.getGame().getAiManager().getAtomicAi().getAtomicMove(c.get(0), ai.getUnit());
            else
                action = master.getGame().getAiManager().getAtomicAi().getAtomicActionApproach(ai);

            return new ActionSequence(GOAL_TYPE.WANDER, action);
        } else
            action = paths.get(0).getActions().get(0);

        List<ActionSequence> sequences = master.getGame().getAiManager().getActionSequenceConstructor().
         getSequencesFromPaths(paths, task, action);
        return sequences.get(0);
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
        if (outOfBattleOnly) {
            deque.removeIf(ai -> !ai.isOutsideCombat());
        }
        return deque;
    }

}