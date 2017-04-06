package main.game.core;

import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.ai.elements.actions.Action;
import main.game.core.game.DC_Game;
import main.game.logic.action.context.Context;
import main.rules.combat.ChargeRule;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;


/**
 * Created by JustMe on 3/23/2017.
 */
public class GameLoop {
    private Unit activeUnit;
    private DC_Game game;
    private DC_ActiveObj activatingAction;

    public GameLoop(DC_Game game) {
        this.game = game;
    }

    public void start() {
        while (true) {
            roundLoop();
        }
    }

    //
    private void roundLoop() {
        game.getStateManager().newRound();
        while (true) {
            Boolean result = game.getTurnManager().nextAction();
            if (result == null)
                break;
            if (!result)
                continue;
            activeUnit = game.getTurnManager().getActiveUnit();
            if (activeUnit == null) break;


            if (makeAction())
                break;


        }
        game.getManager().endTurn();
    }

    public DC_Game getGame() {
        return game;
    }

    private Boolean makeAction() {

        if (game.getManager().getActiveObj().isAiControlled()) {
            return activateAction(waitForAI());
        } else {
            return activateAction(waitForPlayerInput());
        }
    }

    private Boolean activateAction(ActionInput input) {
        activatingAction = input.getAction();
        activatingAction.setTargetObj(input.getContext().getTargetObj());
        activatingAction.setTargetGroup(input.getContext().getGroup());
        try {
            input.getAction().getHandler().activateOn(input.getContext());
        } catch (Exception e) {
            e.printStackTrace();
            getGame().getManager().unitActionCompleted(input.getAction(), true);
            return true;
        } finally {
            activatingAction = null;
        }
        int timeCost = input.getAction().getHandler().getTimeCost();
        Boolean endTurn = getGame().getRules().getTimeRule().
         actionComplete(input.getAction(), timeCost);
        if (!endTurn) {
            game.getManager().reset();
            if (ChargeRule.checkRetainUnitTurn(input.getAction())) {
                endTurn = null;
            }
        }

        getGame().getManager().unitActionCompleted(input.getAction(), endTurn);

        if (BooleanMaster.isTrue(endTurn))
            return true;
        else {
            game.getTurnManager().
             resetInitiative(false);
        }
        return endTurn;
    }

    private ActionInput waitForAI() {
        Action aiAction =
         game.getAiManager().getAction(game.getManager().getActiveObj());
        return new ActionInput(aiAction.getActive(), new Context(aiAction.getRef()));
    }


    private ActionInput waitForPlayerInput() {
        return (ActionInput) WaitMaster.waitForInput(WAIT_OPERATIONS.PLAYER_ACTION_SELECTION);
    }


    public DC_ActiveObj getActivatingAction() {
        return activatingAction;
    }

}
