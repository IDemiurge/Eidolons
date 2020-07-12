package eidolons.game.core.atb;

import com.badlogic.gdx.utils.Array;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.entity.Ref;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.NumberUtils;
import main.system.text.Log;
import main.system.text.LogManager;
import main.system.threading.WaitMaster;

import java.util.Comparator;

/**
 * Created by JustMe on 3/24/2018.
 */
public class AtbController implements Comparator<Unit> {
    public static final int ATB_READINESS_PER_AP = 2000; //20% readiness per Action Point
    public static final float SECONDS_IN_ROUND = 60; //seconds; to sync with clock
    public static final float ATB_TO_READY = 10;
    public static final Float TIME_LOGIC_MODIFIER = 1000f;
    protected static final Float ATB_PER_INITIATIVE_PER_SEC =0.1f ;
    protected AtbTurnManager manager;
    protected Array<AtbUnit> unitsInAtb;
    protected float time = 0f; //passed in this round
    protected float totalTime = 0f;
    protected int step;   //during this round
    protected boolean nextTurn;
    protected float unloggedTimePassed=0;

    public AtbController(AtbTurnManager manager) {
        this.manager = manager;
        unitsInAtb = new Array<>();
        AtbPrecalculator.init(this);

    }

    public AtbController(AtbController original, AtbPrecalculator calculator) {
        this.manager = original.getManager();
        unitsInAtb = calculator.cloneUnits(original);
        time = original.getTime();
    }

    protected static int compareForSort(AtbUnit first, AtbUnit second) {
        if (first.getTimeTillTurn() == second.getTimeTillTurn())
            return first.getUnit()== Eidolons.getMainHero()
                    ? -1 :
                    second.getUnit()== Eidolons.getMainHero()? 1 : 0; //EA check
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
            if (checkAllInactive()) {
                timeElapsed = getDefaultTimePeriod();
                int n = 100 * OptionsMaster.getGameplayOptions().getIntValue(
                        (GAMEPLAY_OPTION.ATB_WAIT_TIME));
                if (!isPrecalc())  //initial step may be 0
                    if (step > 0)
                        WaitMaster.WAIT(n);
            }
        }
        this.processTimeElapsed(timeElapsed + 0.0001f);
        this.updateTimeTillTurn();
        this.updateTurnOrder();
        if (this.time >= SECONDS_IN_ROUND) {
            addTime(-SECONDS_IN_ROUND);
            setNextTurn(true);
            return null;
//            manager.getGame().getManager().endRound();
//            newRound();
        }
        if (this.unitsInAtb.get(0).getAtbReadiness() >= ATB_TO_READY *0.99f) {
            return this.unitsInAtb.get(0);
        } else {
            return null; //this.step();
        }
    }

    protected float getDefaultTimePeriod() {
        return 1;
    }

    protected boolean checkAllInactive() {
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
        while (time > 0) {
            float timeToPass = Math.min(time, SECONDS_IN_ROUND);
            time -= timeToPass;
            processTimeElapsed(timeToPass);
            processAtbRelevantEvent();
        }
    }

    protected void processTimeElapsed(Float time) {
        addTime(time);

        if (!isPrecalc()) {
        int toLog = Math.round(time * TIME_LOGIC_MODIFIER + unloggedTimePassed);
        if (toLog>0){
            unloggedTimePassed-=toLog;
            getManager().getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.TIME_ELAPSED,
                    new Ref(toLog)));
        } else {
            unloggedTimePassed+=time * TIME_LOGIC_MODIFIER;
        }
        GuiEventManager.trigger(GuiEventType.TIME_PASSED, time);
        GuiEventManager.trigger(GuiEventType.NEW_ATB_TIME, this.time);
        }
        if (!isPrecalc()) {
            if (Log.check(Log.LOG_CASE.atb))
            if (time > 0)
                manager.getGame().getLogManager().log(LogManager.LOGGING_DETAIL_LEVEL.FULL, getTimeString(time) + " passed, " +
                        getTimeString(SECONDS_IN_ROUND - this.time) +
                        " until end of round");
        }

        for (AtbUnit unit : this.unitsInAtb) {
            unit.setAtbReadiness(unit.getAtbReadiness() + getAtbGainForUnit(time, unit));
        }
        if (!isPrecalc())
            manager.getGame().getManager().atbTimeElapsed(time);
    }

    protected float getAtbGainForUnit(Float time, AtbUnit unit) {
        return time * unit.getInitiative() * ATB_PER_INITIATIVE_PER_SEC;
    }

    protected void addTime(Float time) {
        this.time += time;
        this.totalTime += time;
    }

    protected boolean isPrecalc() {
        return false;
    }

    protected String getTimeString(float v) {
        return
                NumberUtils.formatFloat(1, v) + " s. left";
    }

    protected void updateTurnOrder() {
        this.unitsInAtb.sort((o1, o2) -> compareForSort(o1, o2));
    }

    protected void updateTimeTillTurn() {
        for (AtbUnit unit : this.unitsInAtb) {
//            if (unit.getInitiative() <= 0) {
//                unit.setTimeTillTurn(Float.MAX_VALUE);
//            } else {
            unit.setTimeTillTurn(
                    calculateTimeTillTurn(unit));
//            }

        }
    }

    protected float calculateTimeTillTurn(AtbUnit unit) {
        float time = getAtbGainForUnit((ATB_TO_READY - unit.getAtbReadiness()) , unit);
        if (unit.isImmobilized()) {
            float duration = AtbMaster.getImmobilizingBuffsMaxDuration(unit.getUnit());
            if (duration == 0){
            if (unit.getUnit().getBuff("channeling") != null) {
                if (unit.getAtbReadiness()>=9.99f) {
                    return 0;
                }
            }
                return Float.MAX_VALUE;
        }
            return (time + duration);
        }
        return time;
    }

    protected void removeUnit(AtbUnit unit) {
        int index = this.unitsInAtb.indexOf(unit, true);
        if (index > -1) {
            this.unitsInAtb.removeValue(unit, true);
        }
        this.processAtbRelevantEvent();
    }

    protected void addUnit(AtbUnit unit) {
        // if (unit.getInitiative() > 0) { wtf???
            unit.setAtbReadiness(unit.getInitialInitiative());
            this.unitsInAtb.add(unit);
        // }
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
        return getTimeString(SECONDS_IN_ROUND - getTime());
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
