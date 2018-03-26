package main.game.core;

import main.content.PARAMS;
import main.entity.obj.unit.Unit;
import main.system.GuiEventManager;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;

import static main.system.GuiEventType.INITIATIVE_CHANGED;

/**
 * Created by JustMe on 3/24/2018.
 */
public class AtbController implements Comparator<Unit> {
    public static final int ATB_MOD = 20;
    private static final float TIME_IN_ROUND = 10;
    AtbTurnManager manager;
    private Stack<AtbUnit> unitsInAtb;
    private float time = 0f;

    public AtbController(AtbTurnManager manager) {
        this.manager = manager;
        unitsInAtb = new Stack<>();
        for (Unit sub : manager.getUnits()) {
            addUnit(new AtbUnitImpl(sub));
        }
    }

    public static int compareForSort(AtbUnit first, AtbUnit second) {
        if (first.getTimeTillTurn() == second.getTimeTillTurn())
            return 0;
        if (first.getTimeTillTurn() < second.getTimeTillTurn())
            return -1;
        else
            return 1;
    }

    public int compare(Unit first, Unit second) {
        return compareForSort(getAtbUnit(first), getAtbUnit(second));
    }

    public void newRound() {
        time = 0;
        /*
        readiness is not lost!
         */
    }

    public AtbUnit step() {
        float timeElapsed = this.unitsInAtb.get(0).getTimeTillTurn();
        if (timeElapsed > TIME_IN_ROUND / 10) {
            timeElapsed = TIME_IN_ROUND / 10; //gradual time loop! For modes etc
        }
        if (timeElapsed > 0)
//            return null;
            this.processTimeElapsed(timeElapsed + 0.0001f);
        this.updateTimeTillTurn();
        this.updateTurnOrder();
        if (this.unitsInAtb.get(0).getAtbReadiness() >= TIME_IN_ROUND) {
            return this.unitsInAtb.get(0);
        } else {
            return null; //this.step();
        }
    }

    public void processTimeElapsed(Float time) {
        this.time += time;
        if (this.time >= TIME_IN_ROUND) {
            manager.getGame().getStateManager().newRound();
            newRound();
        }
        manager.getGame().getLogManager().log(getTimeString(time) + " passed, " +
         getTimeString(TIME_IN_ROUND - this.time) +
         " until end of round");
        for (AtbUnit unit : this.unitsInAtb) {
            unit.setAtbReadiness(unit.getAtbReadiness() + time * unit.getInitiative());
        }
        manager.getGame().getManager().atbTimeElapsed(time);
    }

    private String getTimeString(float v) {
        return String.format(java.util.Locale.US, "%.1f", v) + " seconds";
    }

    public void updateTurnOrder() {
        this.unitsInAtb.sort((o1, o2) -> compareForSort(o1, o2));
    }

    public void updateTimeTillTurn() {
        for (AtbUnit unit : this.unitsInAtb) {
//            if(unit.getInitiative() && unit.getAtbReadiness())
            {
                unit.setTimeTillTurn((TIME_IN_ROUND - unit.getAtbReadiness()) / unit.getInitiative());

            }

        }
    }

    public void processAtbRelevantEvent() {
        this.updateTimeTillTurn();
        this.updateTurnOrder();
    }

    public void removeUnit(AtbUnit unit) {
        int index = this.unitsInAtb.indexOf(unit);
        if (index > -1) {
            this.unitsInAtb.remove(unit);
        }
        this.processAtbRelevantEvent();
    }

    public void addUnit(AtbUnit unit) {
        if (unit.getInitiative() > 0) {
            unit.setAtbReadiness(unit.getInitialInitiative());
            this.unitsInAtb.push(unit);
        }
    }

    private AtbUnit getAtbUnit(Unit unit) {
        for (AtbUnit sub : unitsInAtb) {
            if (sub.getUnit() == unit)
                return sub;
        }
        return null;
    }

    public void addUnit(Unit unit) {
        for (AtbUnit sub : unitsInAtb) {
            if (sub.getUnit() == unit)
                return;
        }
        addUnit(new AtbUnitImpl(unit));
    }

    public void removeUnit(Unit unit) {
        for (AtbUnit sub : new ArrayList<>(unitsInAtb)) {
            if (sub.getUnit() == unit)
                removeUnit(sub);
        }
    }

    public Stack<AtbUnit> getUnits() {
        return unitsInAtb;
    }

    public String getTimeString() {
        return getTimeString(TIME_IN_ROUND - getTime());
    }

    public float getTime() {
        return time;
    }

    public interface AtbUnit {

        float getAtbReadiness();

        void setAtbReadiness(float v);

        float getInitiative();

        float getTimeTillTurn();

        void setTimeTillTurn(float i);

        Unit getUnit();

        float getInitialInitiative();
    }

    public class AtbUnitImpl implements AtbUnit {
        Unit unit;
        private float timeTillTurn;

        public AtbUnitImpl(Unit unit) {
            this.unit = unit;
        }

        @Override
        public Unit getUnit() {
            return unit;
        }

        @Override
        public float getInitialInitiative() {
            return RandomWizard.getRandomFloatBetween() * TIME_IN_ROUND * 0.25f;
        }

        @Override
        public float getAtbReadiness() {
            return StringMaster.getFloat(unit.getParam(PARAMS.C_INITIATIVE));
        }

        @Override
        public void setAtbReadiness(float i) {
            double value = (i);

            manager.getGame().getLogManager().log(
             getUnit().getName() + " has " +
              value +
              "%" +
              " readiness");
            if (unit.getIntParam(PARAMS.C_INITIATIVE) == value)
                return;
            unit.setParam(PARAMS.C_INITIATIVE, value + "");
            GuiEventManager.trigger(
             INITIATIVE_CHANGED,
             new ImmutablePair<>(getUnit(), new ImmutablePair<>((int) Math.round(value * 10), getTimeTillTurn()))
            );
        }

        @Override
        public float getInitiative() {
            if (unit.canActNow())
                return new Float(unit.getParamDouble(PARAMS.N_OF_ACTIONS));
            return 0;
        }

        @Override
        public float getTimeTillTurn() {
            return timeTillTurn;
        }

        @Override
        public void setTimeTillTurn(float i) {
            if (timeTillTurn != i) {
                timeTillTurn = i;
                GuiEventManager.trigger(
                 INITIATIVE_CHANGED,
                 new ImmutablePair<>(getUnit(), new ImmutablePair<>(Math.round(getAtbReadiness() * 10)
                  , getTimeTillTurn()))
                );
            }
        }
    }


}
