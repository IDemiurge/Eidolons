package eidolons.game.battlecraft.ai.tools;

import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.story.cinematic.Cinematics;
import main.elements.targeting.SelectiveTargeting;
import main.game.logic.action.context.Context;

public class AiExecutor {

    public AiExecutor(DC_Game game) {
        // TODO Auto-generated constructor stub
    }

    public boolean execute(AiAction aiAction) {
        return execute(aiAction, false, true);
    }

    public boolean execute(AiAction aiAction, boolean free, boolean gameThread) {
        boolean result = false;
        Context ref = new Context(aiAction.getRef());
        if (Cinematics.ON || free) {
            aiAction.getActive().setFree(true);
        }
        try {
                if (aiAction.getSource().isAiControlled())
                if (!aiAction.getActive().isChanneling()) {
                    if (ref.getTargetObj() == null) {
                        if ((aiAction.getActive().getTargeting()
                                instanceof SelectiveTargeting)) {
                            return false;
                        }
                    }

                }
            if (gameThread) {
                aiAction.getActive().getHandler().activateOnGameLoopThread();
                result = true;
            } else {
                aiAction.getActive().getHandler().activateOn(ref);
                result = true;
            }

//            WaitMaster.waitForInput(WAIT_OPERATIONS.ACTION_COMPLETE);
            return result;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            aiAction.getActive().setFree(false);
        }

        return result;
    }
}
