package main.game.ai.tools;

import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.game.ai.elements.actions.Action;
import main.game.core.game.DC_Game;

public class AiExecutor {

    public AiExecutor(DC_Game game) {
        // TODO Auto-generated constructor stub
    }

    public boolean execute(Action action) {
        boolean result = false;
        Ref ref = action.getRef();
        try {

            if (!action.getActive().isChanneling())
                if (ref.getTargetObj() == null)
                    if (!(action.getActive().getTargeting()
                     instanceof SelectiveTargeting)) {
                        action.getActive().getHandler().activateOnActionThread();
                        result = true;
                    }
            if (!result) {
                action.getActive().getHandler().activateOn(ref);
                result = true;
            }

//            WaitMaster.waitForInput(WAIT_OPERATIONS.ACTION_COMPLETE);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
