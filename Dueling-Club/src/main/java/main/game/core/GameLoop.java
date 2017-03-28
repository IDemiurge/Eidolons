package main.game.core;

import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.ai.elements.actions.Action;
import main.game.core.game.DC_Game;
import main.game.logic.action.context.Context;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 3/23/2017.
 */
public class GameLoop {
    private static boolean enabled=true;
    Unit activeUnit;
    private DC_ActiveObj action;
    //    private  Ref ref;
    private DC_Obj target;
    private DC_Game game;
    private Context context;

    public GameLoop(DC_Game game) {
        this.game = game;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        GameLoop.enabled = enabled;
    }

    public void start() {
        while (true) {
            game.getStateManager().newRound();
            roundLoop();
        }
    }

    private void roundLoop() {
        while (true) {
            if (!game.getTurnManager().nextAction())
            continue;
            activeUnit = game.getTurnManager().getActiveUnit();
            if (activeUnit == null) break;
Boolean result =false;
           try{
               result = makeAction();
           }catch(Exception e){
               e.printStackTrace();
           }
            if (BooleanMaster.isFalse(result))
                game.getManager().endTurn();
            else {
                game.getTurnManager().
                 resetInitiative(false);
            }
        }
    }

    private Boolean makeAction() {

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
        action = aiAction.getActive();
        context = new Context(aiAction.getRef());
    }

    private Boolean activateAction() {
        if (context == null) {
            context = new Context(game.getManager().getActiveObj(), target);
        }
        return activateAction(action, context);

    }

    private Boolean activateAction(DC_ActiveObj action, Context context) {
        return action.getHandler().activateOn(context);

    }


    private void waitForPlayerInput() {
        WaitMaster.waitForInput(WAIT_OPERATIONS.PLAYER_ACTION_SELECTION);
    }

    public void setAction(DC_ActiveObj action) {
        this.action = action;
    }

    public void setTarget(DC_Obj target) {
        this.target = target;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
