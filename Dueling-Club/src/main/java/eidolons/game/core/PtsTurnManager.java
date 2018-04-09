package eidolons.game.core;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import main.system.GuiEventManager;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.Comparator;

import static main.system.GuiEventType.ACTIVE_UNIT_SELECTED;

/**
 * After each Action, recalculates Initiative for each unit,
 * rebuilds Queue and makes the top unit Active.
 */

public class PtsTurnManager extends GenericTurnManager implements Comparator<Unit> {

    public PtsTurnManager(DC_Game game) {
        super(game);
    }

    public void resetInitiative(boolean first) {

        for (Unit unit : getUnits()) {
            if (unit.getAI().isOutsideCombat())
                continue;
            resetInitiative(unit, first);
        }
    }


    @Override
    protected void addToQueue() {
        super.addToQueue();
    }

    protected boolean chooseUnit() {
        setActiveUnit(unitQueue.peek());
        if (!game.getManager().activeSelect(getActiveUnit())) {
            return false;
        }
        LogMaster.gameInfo(StringMaster.getStringXTimes(50 - getActiveUnit().toString().length(), ">")
         + "Active unit: " + getActiveUnit());
        GuiEventManager.trigger(ACTIVE_UNIT_SELECTED, activeUnit);
        return true;
    }

    @Override
    public int getTimeModifier() {
        return game.getRules().getTimeRule().getTimePercentageRemaining();
    }

    @Override
    public String getTimeString() {
        return getTimeModifier() + "";
    }

    @Override
    public Float getTotalTime() {
        return getGame().getRules().getTimeRule().getTimeRemaining()/10f;
    }

    public void newRound() {
        resetInitiative(true);
        super.newRound();
    }

    protected void resetInitiative(Unit unit, boolean first) {
        if (first) {
            int amplitude = unit.getIntParam(PARAMS.INITIATIVE_BONUS);
            int initiativeBonus = RandomWizard.getRandomInt(amplitude);
            initiativeBonus += unit.getIntParam(PARAMS.C_INITIATIVE_TRANSFER);
            unit.setParam(PARAMS.C_INITIATIVE_TRANSFER, 0);// TODO percent?
            unit.setParam(PARAMS.C_INITIATIVE_BONUS, initiativeBonus);

        }
        unit.recalculateInitiative();
    }

    @Override
    public int compare(Unit u1, Unit u2) {
        int a1 = u1.getIntParam(PARAMS.C_INITIATIVE);
        int a2 = u2.getIntParam(PARAMS.C_INITIATIVE);
        if (a1 > a2) {
            return -1;
        }
        return 1;
    }

}
