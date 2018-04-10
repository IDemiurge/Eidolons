package eidolons.game.core;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.advanced.machine.train.AiTrainingRunner;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.battlecraft.rules.combat.misc.ChargeRule;
import eidolons.game.battlecraft.rules.magic.ChannelingRule;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.anims.AnimMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;
import main.game.logic.action.context.Context;
import main.system.auxiliary.log.Err;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;


/**
 * Created by JustMe on 3/23/2017.
 */
public class GameLoop {

    protected Unit activeUnit;
    protected DC_Game game;
    protected DC_ActiveObj activatingAction;
    protected boolean paused;
    protected boolean aiFailNotified;
    protected boolean aftermath;
    protected boolean skippingToNext;
    protected boolean exited;
    protected DequeImpl<ActionInput> actionQueue = new DequeImpl<>();
    private Thread thread;
    private boolean started;

    public GameLoop(DC_Game game) {
        this.game = game;
    }

    public GameLoop() {

    }

    public void start() {
        if (!CoreEngine.isGraphicsOff()) {
            WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY);
        }
        while (true) {
            if (!AiTrainingRunner.running) {
                try {
                    if (!roundLoop())
                        break;
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            } else
                try {
                    {
                        if (!roundLoop())
                            break;
                    }
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    break;
                }
        }
        loopExit();
        return;
    }

    protected void loopExit() {
        LogMaster.log(1, "Game Loop exit " + this);
        if (AiTrainingRunner.running) {
            WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_FINISHED, false);
        } else {
//            if (ExplorationMaster.isExplorationOn())
//                return;
//
//            Boolean result = (boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_FINISHED);
//            if (result) {
//                Eidolons.getGame().getBattleMaster().getOutcomeManager().next();
//            } else {
//                aftermath = true;
//                start();
//            }

        }


        main.system.auxiliary.log.LogMaster.log(1, this + " exited!");
        setExited(false);
    }

    public Thread startInNewThread() {
        if (thread == null)
            thread = new Thread(() -> {
                start();
            }, getThreadName());

        thread.start();

        LogMaster.log(1, "Game Loop started " + this);
        return thread;
    }

    protected String getThreadName() {
        return "Game Loop";
    }

    protected boolean roundLoop() {
        game.getStateManager().newRound();
        boolean retainActiveUnit = false;
        while (true) {
            Boolean result = game.getTurnManager().nextAction();
            if (result == null) {
                break;
            }
            if (!result) {
                if (DC_Engine.isAtbMode()) {
                    getGame().getManager().reset();
                }
                continue;
            }
            if (!retainActiveUnit)
                activeUnit = game.getTurnManager().getActiveUnit();
            retainActiveUnit = false;
            if (activeUnit == null) {
                break;
            }
            if (!started) {
                VisionManager.refresh();
                started = true;
            }

            result = makeAction();
            if (exited || ExplorationMaster.isExplorationOn())
                return false;
            if (!aftermath)
                if (game.getBattleMaster().getOutcomeManager().checkOutcomeClear()) {
                    return false;
                }
            if (result == null) {
                retainActiveUnit = true;
                continue;
            }
            if (result) {
                //end round
                break;
            }
        }
        if (skippingToNext)
            return false;
        if (aftermath)
            if (game.getBattleMaster().getOutcomeManager().checkOutcomeClear()) {
                return false;
            }
        return game.getManager().endRound();
    }


    public DC_Game getGame() {
        return game;
    }

    /**
     * @return true if round must end, null if active unit is to be retained
     */
    protected Boolean makeAction() {
        if (exited)
            return true;
        Boolean result = null;
        ActionInput action = null;
        boolean channeling = false;
        if (!actionQueue.isEmpty()) {
            action = (actionQueue.removeLast());
        } else if (activeUnit.getHandler().getChannelingSpellData() != null) {
            action = activeUnit.getHandler().getChannelingSpellData();
            channeling = true;
        } else if (activeUnit.isAiControlled()) {
            //SHOWCASE SECURITY
            try {
                action = (waitForAI());
                AI_Manager.setOff(false);
            } catch (Exception e) {
                AI_Manager.setOff(true);
                if (!aiFailNotified) {
                    Err.error("Sorry, AI failed, but you can control their units now...");
                    aiFailNotified = true;
                    action = (waitForPlayerInput());
                }
                main.system.ExceptionMaster.printStackTrace(e);
            }
        } else {
            action = (waitForPlayerInput());
        }
        if (channeling)
            ChannelingRule.channelingResolves(activeUnit);

        waitForAnimations();

        result =
         activateAction(action);
        waitForPause();
        if (exited)
            return true;
        return result;
    }


    protected void waitForPause() {
        if (paused) {
            WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_LOOP_PAUSE_DONE);
            paused = false;
        }
    }

    protected void waitForAnimations() {
        Integer MAX_ANIM_TIME =
         OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.MAX_ANIM_WAIT_TIME);
        if (MAX_ANIM_TIME != null) {
            if (MAX_ANIM_TIME > 0) {
                if (AnimMaster.getInstance().isDrawing()) {
                    WaitMaster.waitForInput(WAIT_OPERATIONS.ANIMATION_QUEUE_FINISHED, MAX_ANIM_TIME);
                }
            }
        }
    }

    protected Boolean activateAction(ActionInput input) {
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
            main.system.ExceptionMaster.printStackTrace(e);
            return null;
        } finally {
            activatingAction = null;
        }
        if (!result) {
            return false;
        }
        return checkEndRound(input);
    }

    protected Boolean checkEndRound(ActionInput input) {
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

    protected ActionInput waitForAI() {
        Action aiAction =
         game.getAiManager().getAction(game.getManager().getActiveObj());
        if (!aiAction.getActive().isChanneling())
            if (!aiAction.canBeTargeted()) {
                {
                    AI_Manager.getBrokenActions().add(aiAction.getActive());
                    return null;
                }
            }
        return new ActionInput(aiAction.getActive(), new Context(aiAction.getRef()));
    }

    protected ActionInput waitForPlayerInput() {
        return (ActionInput) WaitMaster.waitForInput(WAIT_OPERATIONS.ACTION_INPUT);
    }

    public DC_ActiveObj getActivatingAction() {
        return activatingAction;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        game.getLogManager().log(paused ? "Game paused" : "Game resumed");
        this.paused = paused;
        SuperActor.setAlphaFluctuationOn(!paused);
        if (!paused)
            WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_LOOP_PAUSE_DONE, true);
    }

    public void setSkippingToNext(boolean skippingToNext) {
        this.skippingToNext = skippingToNext;
    }

    public void setAftermath(boolean aftermath) {
        this.aftermath = aftermath;
    }

    public void queueActionInput(ActionInput actionInput) {
        actionQueue.add(actionInput);
    }

    public void actionInput(ActionInput actionInput) {
        WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_INPUT, actionInput);
    }

    public Unit getActiveUnit() {
        return activeUnit;
    }

    public void setExited(boolean exited) {
        this.exited = exited;
        if (exited)
            try {
                game.getGameLoopThread().interrupt();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
    }

    public void togglePaused() {
        setPaused(!isPaused());
    }

    public Float getTime() {
        return game.getDungeonMaster().getExplorationMaster().getTimeMaster().getTime();
    }
}
