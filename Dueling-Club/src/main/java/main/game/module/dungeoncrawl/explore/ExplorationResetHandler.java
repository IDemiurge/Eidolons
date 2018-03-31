package main.game.module.dungeoncrawl.explore;

import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.entity.obj.unit.Unit;
import main.game.core.ActionInput;
import main.system.math.PositionMaster;

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
        master.getGame().getMaster().clearCaches();

//        master.getGame().getStateManager().checkTriggers();

        //position-based effects?
        master.getGame().getRules().getIlluminationRule().resetIllumination();
//        IlluminationRule.applyLightEmission(master.game);

//        checkCounterRules();
//        master.getGame().getManager().checkForChanges(true);

        //reset once after *all* ai moves?


        resetNeeded = false;
//        GuiEventManager.trigger(GuiEventType.UPDATE_GUI);
        //check time triggers!
    }

    public boolean isAggroCheckNeeded(ActionInput input) {
        Unit unit = input.getAction().getOwnerObj();
        Unit enemy = input.getAction().getGame().getAiManager().getAnalyzer().
         getClosestEnemy(unit);
        if (enemy == null)
            return false;
        double distance = PositionMaster.getExactDistance(enemy.getCoordinates(),
         input.getAction().getOwnerObj().getCoordinates());
        //TODO visible?
        //stealth: when is *that* check made?
        if (distance > unit.getSightRangeTowards(enemy)) {
            return false;
        }
        if (input.getAction().getActionGroup() == ACTION_TYPE_GROUPS.MOVE) {
            return true;
        }
        return input.getAction().getActionGroup() == ACTION_TYPE_GROUPS.TURN;
    }

    public boolean isFirstResetDone() {
        return firstResetDone;
    }

    public void setFirstResetDone(boolean firstResetDone) {
        this.firstResetDone = firstResetDone;
    }
}
