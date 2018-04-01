package eidolons.game.battlecraft.ai.logic.types.brute;

import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.DC_UnitModel;
import eidolons.game.battlecraft.ai.logic.DC_AI;
import eidolons.game.battlecraft.ai.logic.DC_AI_Logic;
import main.entity.obj.Obj;

public class BruteAI extends DC_AI_Logic {

    public BruteAI(DC_AI ai) {
        super(ai);
    }

    @Override
    public void init() {
        targetingManager = new BruteTargeter(this);
        aManager = new BruteActionMaster(this);
        pManager = new BrutePrioritizer(this);

    }

    @Override
    public Obj getActive() {
        this.action = initAction();
        switch (action) {
            case ABILITY:
                // TODO
                break;
            case APPROACH:
                active = (DC_ActiveObj) actionManager
                 .getAction(DC_ActionManager.STD_ACTIONS.Move.name(), unit);
                break;
            case ATTACK:
                active = (DC_ActiveObj) actionManager
                 .getAction(DC_ActionManager.ATTACK, unit);
                if (((DC_UnitModel) unit).isHero()) {
                    active = (DC_ActiveObj) actionManager
                     .getAction(DC_ActionManager.STD_ACTIONS.Attack.name(), unit);
                }
                break;
            case CLOSE_IN:

                active = (DC_ActiveObj) actionManager
                 .getAction(DC_ActionManager.STD_ACTIONS.Move.name(), unit);
                break;
            case ESCAPE:
                active = (DC_ActiveObj) actionManager
                 .getAction(DC_ActionManager.STD_ACTIONS.Move.name(), unit);
                break;
            case SPELL:
                // TODO
                break;
            default:
                break;

        }
        // main.system.auxiliary.LogMaster
        // .log(LogMaster.AI_DEBUG, "Active chosen: " + active+ " for " + unit);
        return active;
    }

    @Override
    public boolean isTurnOver() {
        boolean result = true;
        for (Obj obj : units) {
            result &= ((DC_UnitModel) obj).isDone();
        }
        return result;
    }

    @Override
    public boolean isUnitDone(Obj unit) {
        return ((DC_UnitModel) unit).isDone();
    }

    @Override
    public void reset() {
        setAction(null);
        setTarget(null);
        setActive(null);
        setUnit(null);

    }

}
