package eidolons.game.exploration.handlers;

import eidolons.entity.unit.Unit;
import eidolons.game.core.ActionInput;
import eidolons.game.core.Core;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.system.math.PositionMaster;

/**
 * Created by JustMe on 9/10/2017.
 */
public class ExplorationResetHandler extends ExplorationHandler {
    private boolean resetNeeded;
    private boolean resetNotRequired;

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
        master.getGame().getObjMaster().clearCaches();

//        master.getGame().getStateManager().checkTriggers();

        //position-based effects?
        master.getGame().getVisionMaster().getIllumination().removeIllumination();
//        IlluminationRule.applyLightEmission(master.game);

//        checkCounterRules();
//        master.getGame().getManager().checkForChanges(true);

        //reset once after *all* ai moves?


        resetNeeded = false;
//        GuiEventManager.trigger(GuiEventType.UPDATE_GUI);
        //check time triggers!
    }

    public boolean isAggroCheckNeeded(ActionInput input) {
        Unit unit = Core.getMainHero();
        //input.getAction().getGame().getAiManager().getAnalyzer().
        //         getClosestEnemy(unit)
        Unit enemy =  input.getAction().getOwnerUnit();
        if (enemy == null)
            return false;
        if (!isCloseEnoughToReset(enemy, unit)){
            return false;
        }

        if (input.getAction().getActionGroup() == ACTION_TYPE_GROUPS.MOVE) {
            return true;
        }
        return input.getAction().getActionGroup() == ACTION_TYPE_GROUPS.TURN;
    }

    public static boolean isCloseEnoughToReset(Unit enemy, Unit unit) {
        double distance = PositionMaster.getExactDistance(enemy.getCoordinates(),
                unit.getCoordinates());
        //TODO visible?
        //stealth: when is *that* check made?
        return !(distance > enemy.getSightRangeTowards(unit));
    }

    public boolean isResetNotRequired() {
        return resetNotRequired;
    }

    public void setResetNotRequired(boolean b) {
        this.resetNotRequired = b;
    }
}
