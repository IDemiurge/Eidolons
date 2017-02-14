package main.game.ai.logic.types.brute;

import main.entity.active.DC_ActiveObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.DC_UnitObj;
import main.game.ai.logic.DC_AI;
import main.game.ai.logic.DC_AI_Logic;
import main.rules.DC_ActionManager;

public class BruteAI extends DC_AI_Logic {

    public BruteAI(DC_AI ai) {
        super(ai);
    }

    @Override
    public void init() {
        tManager = new BruteTargeter(this);
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
                if (((DC_UnitObj) unit).isHero()) {
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
            result &= ((DC_UnitObj) obj).isDone();
        }
        return result;
    }

    @Override
    public boolean isUnitDone(Obj unit) {
        return ((DC_UnitObj) unit).isDone();
    }

    @Override
    public void reset() {
        setAction(null);
        setTarget(null);
        setActive(null);
        setUnit(null);

    }

}
