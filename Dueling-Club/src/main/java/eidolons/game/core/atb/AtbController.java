package eidolons.game.core.atb;

import com.badlogic.gdx.utils.Array;
import eidolons.entity.obj.unit.Unit;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.entity.Ref;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;

import java.util.Comparator;

/**
 * Created by JustMe on 3/24/2018.
 */
public class AtbController implements Comparator<Unit> {
    public static final int ATB_READINESS_PER_AP = 20; //20% readiness per Action Point
    public static final float TIME_IN_ROUND = 12; //seconds; to sync with clock
    public static final float TIME_TO_READY = 10;
    public static final Float TIME_LOGIC_MODIFIER = 10f;
    private AtbTurnManager manager;
    private Array<AtbUnit> unitsInAtb;
    private float time = 0f; //passed in this round
    private float totalTime = 0f;
    private int step;   //during this round
    private boolean nextTurn;

    public AtbController(AtbTurnManager manager) {
        this.manager = manager;
        unitsInAtb = new Array<>();
        AtbCalculator.init(this);

    }

    public AtbController(AtbController original, AtbCalculator calculator) {
        this.manager = original.getManager();
        unitsInAtb = calculator.cloneUnits(original);
        time = original.getTime();
    }

    private static int compareForSort(AtbUnit first, AtbUnit second) {
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
        step++;
        float timeElapsed = this.unitsInAtb.get(0).getTimeTillTurn();
        if (timeElapsed > getDefaultTimePeriod() || checkAllInactive()) {
            timeElapsed = getDefaultTimePeriod(); //gradual time loop! For modes etc
        }
        if (timeElapsed <= 0) {
            if (checkAllInactive())
                timeElapsed = getDefaultTimePeriod();
            int n = 100 * OptionsMaster.getGameplayOptions().getIntValue(
             (GAMEPLAY_OPTION.ATB_WAIT_TIME));
          if (!isPrecalc())  //initial step may be 0
              if (step > 0)                 WaitMaster.WAIT(n);
        }
        this.processTimeElapsed(timeElapsed + 0.0001f);
        this.updateTimeTillTurn();
        this.updateTurnOrder();
        if (this.time >= TIME_IN_ROUND) {
            addTime(-TIME_IN_ROUND);
            setNextTurn(true);
            return null;
//            manager.getGame().getManager().endRound();
//            newRound();
        }
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
        step = 0;
        /*
        readiness is not lost!
         */
    }


    public void processAtbRelevantEvent() {
        this.updateTimeTillTurn();
        this.updateTurnOrder();
    }

    public void passTime(Float time) {
        while (time>0){
            float timeToPass = Math.min(time, TIME_IN_ROUND);
            time -= timeToPass;
            processTimeElapsed(timeToPass);
            processAtbRelevantEvent();
        }
    }

    private void processTimeElapsed(Float time) {
        addTime(time);

        getManager().getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.TIME_ELAPSED,
         new Ref(Math.round(time * TIME_LOGIC_MODIFIER))));
        GuiEventManager.trigger(GuiEventType.TIME_PASSED, time);
        GuiEventManager.trigger(GuiEventType.NEW_ATB_TIME, this.time);

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

    private void addTime(Float time) {
        this.time += time;
        this.totalTime += time;
    }

    protected boolean isPrecalc() {
        return false;
    }

    private String getTimeString(float v) {
        return
         StringMaster.formatFloat(1, v)  + " seconds";
    }

    private void updateTurnOrder() {
        this.unitsInAtb.sort((o1, o2) -> compareForSort(o1, o2));
    }

    private void updateTimeTillTurn() {
        for (AtbUnit unit : this.unitsInAtb) {
//            if (unit.getInitiative() <= 0) {
//                unit.setTimeTillTurn(Float.MAX_VALUE);
//            } else {
            unit.setTimeTillTurn(
             calculateTimeTillTurn(unit));
//            }

        }
    }

    private float calculateTimeTillTurn(AtbUnit unit) {
        float time = (TIME_TO_READY - unit.getAtbReadiness()) / unit.getInitiative();
        if (unit.isImmobilized()) {
            float duration = AtbMaster.getImmobilizingBuffsMaxDuration(unit.getUnit());
            if (duration == 0)
                return Float.MAX_VALUE;
            return (time + duration);
        }
        return time;
    }

    private void removeUnit(AtbUnit unit) {
        int index = this.unitsInAtb.indexOf(unit, true);
        if (index > -1) {
            this.unitsInAtb.removeValue(unit, true);
        }
        this.processAtbRelevantEvent();
    }

    private void addUnit(AtbUnit unit) {
        if (unit.getInitiative() > 0) {
            unit.setAtbReadiness(unit.getInitialInitiative());
            this.unitsInAtb.add(unit);
        }
    }

    public AtbUnit getAtbUnit(Unit unit) {
        for (AtbUnit sub : unitsInAtb) {
            if (sub.getUnit() == unit)
                return sub;
        }
        return null;
    }

    public Array<AtbUnit> getUnitsInAtb() {
        return unitsInAtb;
    }

    public int getStep() {
        return step;
    }

    public void addUnit(Unit unit) {
        for (AtbUnit sub : unitsInAtb) {
            if (sub.getUnit() == unit)
                return;
        }
        addUnit(new AtbUnitImpl(this, unit));
        unit.getGame().fireEvent(new Event(
         STANDARD_EVENT_TYPE.UNIT_HAS_ENTERED_COMBAT, unit.getRef()));
    }

    public void removeUnit(Unit unit) {

        for (AtbUnit sub : new Array<>(unitsInAtb)) {
            if (sub.getUnit() == unit)
                removeUnit(sub);
        }
    }

    Array<AtbUnit> getUnits() {
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

    public Float getTotalTime() {
        return totalTime;
    }


    public boolean isNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(boolean nextTurn) {
        this.nextTurn = nextTurn;
    }
}
