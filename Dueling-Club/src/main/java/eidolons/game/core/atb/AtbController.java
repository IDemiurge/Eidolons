package eidolons.game.core.atb;

import main.entity.Ref;
import eidolons.entity.obj.unit.Unit;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;

/**
 * Created by JustMe on 3/24/2018.
 */
public class AtbController implements Comparator<Unit> {
    public static final int ATB_MOD = 20;
    public static final float TIME_IN_ROUND = 12;
    public static final float TIME_TO_READY = 10;
    private static final Float TIME_LOGIC_MODIFIER = 10f;
    private static final Float TIME_DISPLAY_MODIFIER = 1f;
    private AtbCalculator calculator;
    private AtbTurnManager manager;
    private Stack<AtbUnit> unitsInAtb;
    private float time = 0f;

    public AtbController(AtbTurnManager manager) {
        this.manager = manager;
        unitsInAtb = new Stack<>();
        for (Unit sub : manager.getUnits()) {
            addUnit(new AtbUnitImpl(this, sub));
        }
        calculator = new AtbCalculator(this);
    }

    public AtbController(AtbController original, AtbCalculator calculator) {
        this.manager = original.getManager();
        unitsInAtb = calculator.cloneUnits(original);
        time = original.getTime();
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


    public AtbUnit step() {
        float timeElapsed = this.unitsInAtb.get(0).getTimeTillTurn();
        if (timeElapsed > getDefaultTimePeriod() || checkAllInactive()) {
            timeElapsed = getDefaultTimePeriod(); //gradual time loop! For modes etc
        }
        if (timeElapsed <= 0) {
            if (checkAllInactive())
                timeElapsed = getDefaultTimePeriod();
            int n = 100 * OptionsMaster.getGameplayOptions().getIntValue(
             (GAMEPLAY_OPTION.ATB_WAIT_TIME));
            WaitMaster.WAIT(n);
        }
        this.processTimeElapsed(timeElapsed + 0.0001f);
        this.updateTimeTillTurn();
        this.updateTurnOrder();
        if (this.unitsInAtb.get(0).getAtbReadiness() >= TIME_TO_READY) {
            return this.unitsInAtb.get(0);
        } else {
            return null; //this.step();
        }
    }

    private float getDefaultTimePeriod() {
        return 1;
    }

    private boolean checkAllInactive() {
        for (AtbUnit sub : getUnits()) {
            if (sub.getInitiative() > 0)
                return false;
        }
        return true;
    }

    public void newRound() {
        processAtbRelevantEvent();
        /*
        readiness is not lost!
         */
    }
    public void processTimeElapsed(Float time) {
        this.time += time;

        getManager().getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.TIME_ELAPSED,
         new Ref(Math.round(time * TIME_LOGIC_MODIFIER))));
        GuiEventManager.trigger(GuiEventType.TIME_PASSED, time);
        GuiEventManager.trigger(GuiEventType.NEW_ATB_TIME, this.time);
        if (this.time >= TIME_IN_ROUND) {
            this.time = this.time-TIME_IN_ROUND;
            manager.getGame().getStateManager().newRound();
            newRound();
        }

        if (!isPrecalc())
            manager.getGame().getLogManager().log(getTimeString(time) + " passed, " +
             getTimeString(TIME_IN_ROUND - this.time) +
             " until end of round");
        for (AtbUnit unit : this.unitsInAtb) {
            unit.setAtbReadiness(unit.getAtbReadiness() + time * unit.getInitiative());
        }
        if (!isPrecalc())
            manager.getGame().getManager().atbTimeElapsed(time);
    }

    protected boolean isPrecalc() {
        return false;
    }

    private String getTimeString(float v) {
        return String.format(java.util.Locale.US, "%.1f", v) + " seconds";
    }

    public void updateTurnOrder() {
        this.unitsInAtb.sort((o1, o2) -> compareForSort(o1, o2));
    }

    public void updateTimeTillTurn() {
        for (AtbUnit unit : this.unitsInAtb) {
            if (unit.getInitiative() <= 0) {
                unit.setTimeTillTurn(Float.MAX_VALUE);
            } else {
                unit.setTimeTillTurn((TIME_TO_READY - unit.getAtbReadiness()) / unit.getInitiative());

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

    public AtbUnit getAtbUnit(Unit unit) {
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
        addUnit(new AtbUnitImpl(this, unit));
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

    public AtbTurnManager getManager() {
        return manager;
    }

    public interface AtbUnit {

        float getAtbReadiness();

        void setAtbReadiness(float v);

        float getInitiative();

        float getTimeTillTurn();

        void setTimeTillTurn(float i);

        Unit getUnit();

        float getInitialInitiative();

        int getDisplayedAtbReadiness();
    }


}
