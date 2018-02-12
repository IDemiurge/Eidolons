package main.game.module.adventure;

import main.game.module.adventure.entity.MacroParty;
import main.system.options.GameplayOptions.GAMEPLAY_OPTION;
import main.system.options.OptionsMaster;

/**
 * Created by JustMe on 2/10/2018.
 */
public class MacroTimeMaster {
//public static final float

    private float speed = 0;
    private float time = 0;
    private float lastTimeChecked;
    private float delta;
    private boolean guiDirtyFlag;

    public MacroTimeMaster() {
        speed = OptionsMaster.getGameplayOptions().
         getIntValue(GAMEPLAY_OPTION.GAME_SPEED);
    }

    public void timedCheck() {
        delta = time - lastTimeChecked;
        if (delta == 0) return;
        lastTimeChecked = time;

        updateDate();
        processMapObjects();
        checkScripts();


    }

    private void checkScripts() {
    }

    private void processMapObjects() {
        for (MacroParty party : MacroGame.getGame().getState().getParties()) {
            if (party.getCurrentDestination() == null)
                continue;
//    TravelManager.travel(delta);
            TravelMaster.travel(party, delta);

        }
    }

    private void updateDate() {
        //recalc from start? or accrue?
    }

    public void act(float delta) {
        time += delta;
    }
}
