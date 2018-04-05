package eidolons.game.battlecraft.ai.tools;

import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.core.game.DC_Game;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;

public class AiExecutor {

    public AiExecutor(DC_Game game) {
        // TODO Auto-generated constructor stub
    }

    public boolean execute(Action action) {
        return execute(action, false);
    }

    public boolean execute(Action action, boolean free) {
        boolean result = false;
        Ref ref = action.getRef();
        if (free) {
            action.getActive().setFree(true);
        }
        try {

            if (!action.getActive().isChanneling()) {
                if (ref.getTargetObj() == null) {
                    if (!(action.getActive().getTargeting()
                     instanceof SelectiveTargeting)) {
                        result = true;
                        action.getActive().getHandler().activateOnGameLoopThread();

                    }
                }
            }
            if (!result) {
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
