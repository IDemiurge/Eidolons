package main.game.core.atb;

import main.entity.obj.unit.Unit;
import main.game.core.GenericTurnManager;
import main.game.core.atb.AtbController.AtbUnit;
import main.game.core.game.DC_Game;

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

    protected boolean chooseUnit() {
        AtbUnit unit = atbController.step();
        while (unit == null) {
            //TODO integrate properly!
            unit = atbController.step();
        }
        if (unit != null) {
            setActiveUnit(unit.getUnit());
            return game.getManager().activeSelect(getActiveUnit());
        } else {
            return false;
        }

    }

    @Override
    public int getTimeModifier() {
        return game.getRules().getTimeRule().getTimePercentageRemaining();
    }

    public void newRound() {
        atbController.newRound();
        super.newRound();
    }


    public AtbController getAtbController() {
        return atbController;
    }
}

