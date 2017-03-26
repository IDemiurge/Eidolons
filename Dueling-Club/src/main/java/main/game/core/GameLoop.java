package main.game.core;

import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.game.core.game.DC_Game;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 3/23/2017.
 */
public class GameLoop {
DC_ActiveObj action;
Ref ref;
DC_Obj target;
DC_Game game;

    public  void loop(){
        while (true){

            waitForPlayerInput();

            getActionForAI();

            activateAction();


        }
    }

    private void activateAction() {
        action.getHandler().activateOn(ref);

        game.getManager().endTurn();
    }

    private void getActionForAI() {
    }

    private void waitForPlayerInput() {
        WaitMaster.waitForInput(WAIT_OPERATIONS.SELECTION);
    }
}
