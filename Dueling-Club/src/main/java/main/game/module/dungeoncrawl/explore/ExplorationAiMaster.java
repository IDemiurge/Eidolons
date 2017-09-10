package main.game.module.dungeoncrawl.explore;

import main.content.PARAMS;
import main.content.enums.system.AiEnums.GOAL_TYPE;
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
import java.util.Stack;

/**
 * Created by JustMe on 9/9/2017.
 */
public class ExplorationAiMaster extends ExplorationHandler {

    private DequeImpl<UnitAI> activeUnitAIs;
    private boolean aiActs;
    private Stack<ActionInput> aiActionQueue;

    public ExplorationAiMaster(ExplorationMaster master) {
        super(master);
        aiActionQueue = new Stack<>();
        activeUnitAIs = new DequeImpl<>();
    }

    public void reset() {
        activeUnitAIs.clear();
        master.getGame().getUnits().forEach(unit ->
         {
             if (unit.isAiControlled())
                 activeUnitAIs.add(unit.getAI());
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
                isAiActs = true;
            }
        }
        if (isAiActs)
            aiActs = true;
    }

    private void aiMoves(UnitAI ai) {
        ActionInput input = new ActionInput(
         ai.getStandingOrders().getCurrentAction().getActive(),
         new Context(ai.getStandingOrders().getCurrentAction().getRef()));
        aiActionQueue.push(input);
        ai.setExplorationTimeOfLastAction(master.getTimeMaster().getTime());
        if (ai.getStandingOrders().nextAction() == null)
            ai.setStandingOrders(null);

    }

    private ActionSequence getOrders(UnitAI ai) {
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
        List<ActionPath> paths =    new LinkedList<>() ;
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
        return activeUnitAIs;
    }


    public boolean isAiActs() {
        return aiActs;
    }

    public void setAiActs(boolean aiActs) {
        this.aiActs = aiActs;
    }

    public Stack<ActionInput> getAiActionQueue() {
        return aiActionQueue;
    }
}
