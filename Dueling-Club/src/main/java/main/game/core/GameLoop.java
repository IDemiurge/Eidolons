package main.game.core;

import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.rules.combat.misc.ChargeRule;
import main.game.battlecraft.rules.magic.ChannelingRule;
import main.game.core.game.DC_Game;
import main.game.logic.action.context.Context;
import main.libgdx.anims.AnimMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;


/**
 * Created by JustMe on 3/23/2017.
 */
public class GameLoop {

    private static Integer MAX_ANIM_TIME;
    private Unit activeUnit;
    private DC_Game game;
    private DC_ActiveObj activatingAction;
    private boolean paused;

    public GameLoop(DC_Game game) {
        this.game = game;
    }

    public static void setMaxAnimTime(int maxAnimTime) {
        MAX_ANIM_TIME = maxAnimTime;
    }

    public void start() {
        while (true) {
            roundLoop();
        }
    }

    private void roundLoop() {
        game.getStateManager().newRound();
        boolean retainActiveUnit = false;
        while (true) {
            Boolean result = game.getTurnManager().nextAction();
            if (result == null) {
                break;
            }
            if (!result) {
                continue;
            }
            if (!retainActiveUnit)
                activeUnit = game.getTurnManager().getActiveUnit();
            retainActiveUnit = false;
            if (activeUnit == null) {
                break;
            }
            result = makeAction();
            if (result == null) {
                retainActiveUnit = true;
                continue;
            }
            if (result) {
                //end round
                break;
            }
        }
        game.getManager().endRound();
    }

    public DC_Game getGame() {
        return game;
    }

    /**
     *
     * @return
     * true if round must end, null if active unit is to be retained
     */
    private Boolean makeAction() {

        Boolean result;
        if (activeUnit.getHandler().getChannelingSpellData() != null)
        {
            ActionInput data = activeUnit.getHandler().getChannelingSpellData();
            ChannelingRule.channelingResolves(activeUnit);
            result = activateAction(data);
        }
        else if (game.getManager().getActiveObj().isAiControlled()) {
            result = activateAction(waitForAI());
        } else {
            result = activateAction(waitForPlayerInput());
        }
//        if ()
        waitForPause();
        waitForAnimations();
        return result;
    }

    private void waitForPause() {
        if (paused){
            WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_LOOP_PAUSE_DONE );
            paused=false;
        }
    }

    private void waitForAnimations() {
        if (MAX_ANIM_TIME != null) {
            if (MAX_ANIM_TIME > 0) {
                if (AnimMaster.getInstance().isDrawing()) {
                    WaitMaster.waitForInput(WAIT_OPERATIONS.ANIMATION_QUEUE_FINISHED, MAX_ANIM_TIME);
                }
            }
        }
    }

    private Boolean activateAction(ActionInput input) {
        if (input == null) {
            return true;
        }
        boolean result;
        try {
            activatingAction = input.getAction();
            activatingAction.setTargetObj(input.getContext().getTargetObj());
            activatingAction.setTargetGroup(input.getContext().getGroup());
            result = input.getAction().getHandler().activateOn(input.getContext());
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        } finally {
            activatingAction = null;
        }
        if (!result) {
            return false;
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

        if (BooleanMaster.isTrue(endTurn)) {
            return true;
        } else {
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
        return (ActionInput) WaitMaster.waitForInput(WAIT_OPERATIONS.ACTION_INPUT);
    }

    public DC_ActiveObj getActivatingAction() {
        return activatingAction;
    }

    public void setPaused(boolean paused) { main.system.auxiliary.log.LogMaster.log(1,"Pause game: " +paused);
        this.paused = paused;
        if (!paused)
            WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_LOOP_PAUSE_DONE, true);
    }

    public boolean isPaused() {
        return paused;
    }
}
