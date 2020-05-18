package eidolons.game.core.atb;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.GenericTurnManager;
import eidolons.game.core.game.DC_Game;

import java.util.List;

/**
 * After each Action, recalculates Initiative for each unit,
 * rebuilds Queue and makes the top unit Active.
 */

public class AtbTurnManager extends GenericTurnManager {
    AtbController atbController;

    public AtbTurnManager(DC_Game game) {
        super(game);
        atbController = new AtbController(this);
    }

    @Override
    public Float getTotalTime() {
        return atbController.getTotalTime();
    }

    @Override
    protected void sort(List<Unit> list) {
        list.sort(atbController);
    }

    @Override
    public String getTimeString() {
        return getAtbController().getTimeString();
    }

    @Override
    protected void addToQueue() {
        for (Unit sub : getUnits()) {
            if (sub.isDead())
                atbController.removeUnit(sub);
            else if (sub.isOutsideCombat())
                atbController.removeUnit(sub);
            else {
                atbController.addUnit(sub);
                unitQueue.add(sub);
            }
        }
    }

    protected Boolean chooseUnit() {
        //TODO EA check
        while (true) {
            AtbUnit unit = atbController.step();
            if (atbController.isNextTurn()) {
                atbController.setNextTurn(false);
                return null;
            }
            if (unit == null)
                continue;
            setActiveUnit(unit.getUnit());
            return game.getManager().activeSelect(getActiveUnit());
        }
    }


    public void newRound() {
        atbController.newRound();
        super.newRound();
    }


    public AtbController getAtbController() {
        return atbController;
    }
}

