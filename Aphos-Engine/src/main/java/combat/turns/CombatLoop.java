package combat.turns;

import combat.sub.BattleManager;

import static framework.client.ClientConnector.client;

/**
 * Created by Alexander on 10/21/2023
 * <p>
 * controls game over etc? STRATEGIC PHASE! is this some kind of separate THREAD?
 */
public class CombatLoop {
    public CombatLoop(BattleManager manager) {

    }

    public void playerGroupFinish() {
        //when all current are out of AP + auto-pass | or player manually presses <?>
    }

    public void start() {
        // client().sendEvent();
    }

    /*
    turn order

     */

}
