package main.game.core;

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
    private ActionInput input;
    private DC_Game game;

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

            try {
                result = makeAction();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!result) {
                // TODO ???
            }
            int timeCost = input.getAction().getHandler().getTimeCost();
            Boolean endTurn = getGame().getRules().getTimeRule().actionComplete(input.getAction(), timeCost);
            if (!endTurn) {
                game.getManager().reset();
                if (ChargeRule.checkRetainUnitTurn(input.getAction())) {
                    endTurn = null;
                }
            }

            getGame().getManager().unitActionCompleted(input.getAction(), endTurn);


            if (BooleanMaster.isTrue(endTurn))
                break;
            else {
                game.getTurnManager().
                 resetInitiative(false);
            }
        }
        game.getManager().endTurn();
    }

    public DC_Game getGame() {
        return game;
    }

    private Boolean makeAction() {
        input = null;
        if (game.getManager().getActiveObj().isAiControlled()) {
            waitForAI();
        } else {
            waitForPlayerInput();
        }

        return activateAction();

    }

    private void waitForAI() {
        Action aiAction =
         game.getAiManager().getAction(game.getManager().getActiveObj());
        input = new ActionInput(aiAction.getActive(), new Context(aiAction.getRef()));
    }


    private Boolean activateAction() {
        return input.getAction().getHandler().activateOn(input.getContext());

    }


    private void waitForPlayerInput() {
        input = (ActionInput) WaitMaster.waitForInput(WAIT_OPERATIONS.PLAYER_ACTION_SELECTION);
    }


}
