package main.game.module.dungeoncrawl.explore;

import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.game.battlecraft.rules.mechanics.IlluminationRule;
import main.game.core.ActionInput;

/**
 * Created by JustMe on 9/10/2017.
 */
public class ExplorationResetHandler extends ExplorationHandler {
    private boolean resetNeeded;
    private boolean firstResetDone;

    public ExplorationResetHandler(ExplorationMaster master) {
        super(master);
    }

    public boolean isResetNeeded() {
        return resetNeeded;
    }

    public void setResetNeeded(boolean resetNeeded) {
        this.resetNeeded = resetNeeded;
    }

    public void resetAll() {
       master.getGame(). getMaster(). clearCaches();

//        master.getGame().getStateManager().checkTriggers();

        //position-based effects?
        IlluminationRule.resetIllumination(master.game);
//        IlluminationRule.initLightEmission(master.game);

//        checkCounterRules();
//        master.getGame().getManager().checkForChanges(true);

        //reset once after *all* ai moves?


        resetNeeded = false;
//        GuiEventManager.trigger(GuiEventType.UPDATE_GUI);
        //check time triggers!
    }

    public boolean isAggroCheckNeeded(ActionInput input) {
        if (input.getAction().getActionGroup() == ACTION_TYPE_GROUPS.MOVE) {
            return true;
        }
        return false;
    }

    public boolean isFirstResetDone() {
        return firstResetDone;
    }

    public void setFirstResetDone(boolean firstResetDone) {
        this.firstResetDone = firstResetDone;
    }
}
