package eidolons.game.battlecraft.ai.tools;

import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.game.DC_Game;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.game.logic.action.context.Context;

public class AiExecutor {

    public AiExecutor(DC_Game game) {
        // TODO Auto-generated constructor stub
    }

    public boolean execute(Action action) {
        return execute(action, false);
    }

    public boolean execute(Action action, boolean free) {
        boolean result = false;
        Context ref = new Context(action.getRef());
        if (Cinematics.ON|| free) {
            action.getActive().setFree(true);
        }
        boolean gameThread = false;
        try {
            if (!Cinematics.ON)
            if (!action.getActive().isChanneling()) {
                if (ref.getTargetObj() == null) {
                    if (!(action.getActive().getTargeting()
                     instanceof SelectiveTargeting)) {
                        gameThread=true;
                    }
                }
            }
            if (!gameThread) {
                action.getActive().getHandler().activateOnGameLoopThread();
                result = true;
            } else {
                action.getActive().getHandler().activateOn(ref);
                result = true;
            }

//            WaitMaster.waitForInput(WAIT_OPERATIONS.ACTION_COMPLETE);
            return result;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            action.getActive().setFree(false);
        }

        return result;
    }
}
