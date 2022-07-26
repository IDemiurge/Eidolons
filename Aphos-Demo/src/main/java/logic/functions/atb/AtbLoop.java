package logic.functions.atb;

import content.LOG;
import eidolons.game.core.Core;
import logic.entity.Entity;
import main.system.GuiEventManager;
import content.AphosEvent;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static logic.content.consts.CombatConsts.*;
public class AtbLoop implements Comparator<Entity> {

    protected List<AtbEntity> unitsInAtb = new ArrayList<>(10);
    protected float time = 0f; //passed in this round
    protected float totalTime = 0f;
    protected int step;   //during this round
    protected boolean nextTurn;
    protected float unloggedTimePassed = 0;
    private int clockTicks=0;

//    public AtbLogic(AtbTurnManager manager) {
//        this.manager = manager;
//        unitsInAtb = new Array<>();
//        AtbPrecalculator.init(this);
//
//    }
//
//    public AtbLogic(AtbController original, AtbPrecalculator calculator) {
//        this.manager = original.getManager();
//        unitsInAtb = calculator.cloneUnits(original);
//        time = original.getTime();
//    }


    public int compare(Entity first, Entity second) {
        return AtbHelper.compareForSort(getAtbEntity(first), getAtbEntity(second));
    }


    public AtbEntity step() {
        step++;
        float timeElapsed = this.unitsInAtb.get(0).getTimeTillTurn();
        if (timeElapsed > getDefaultTimePeriod() || checkAllInactive()) {
            timeElapsed = getDefaultTimePeriod(); //gradual time loop! For modes etc
        }
        if (timeElapsed <= 0) {
            if (checkAllInactive()) {
                timeElapsed = getDefaultTimePeriod();
//                int n = 100 * OptionsMaster.getGameplayOptions().getIntValue(
//                        (GameplayOptions.GAMEPLAY_OPTION.ATB_WAIT_TIME));
                if (!isPrecalc())  //initial step may be 0
                    if (step > 0)
                        WaitMaster.WAIT(100);
            }
        }
        this.processTimeElapsed(timeElapsed + 0.0001f); //minimum?
        this.updateTimeTillTurn();
        this.updateTurnOrder();
        if (this.time >= SECONDS_IN_ROUND) {
            addTime(-SECONDS_IN_ROUND);
            setNextTurn(true);
            return null;
            //            manager.getGame().getManager().endRound();
            //            newRound();
        }
        if (this.unitsInAtb.get(0).getAtbReadiness() >= ATB_TO_READY * 0.99f) {
            clockTicks=0;
            return this.unitsInAtb.get(0);
        } else {
            return null; //this.step();
        }
    }

    protected float getDefaultTimePeriod() {
        return 1;
    }

    protected boolean checkAllInactive() {
        for (AtbEntity sub : getUnits()) {
            if (sub.getInitiative() > 0)
                return false;
        }
        return true;
    }

    public void newRound() {
        processAtbRelevantEvent();
        step = 0;
        setNextTurn(false);
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
        for (AtbEntity unit : this.unitsInAtb) {
            unit.setAtbReadiness(unit.getAtbReadiness() + AtbHelper.getAtbGainForEntity(time, unit));
        }

        if (isPrecalc()) {
            return ;
        }
        int toLog = Math.round(time * TIME_LOGIC_MODIFIER + unloggedTimePassed);
        if (toLog > 0) {
            unloggedTimePassed -= toLog;
//            getManager().getGame().fireEvent(new Event(Event.STANDARD_EVENT_TYPE.TIME_ELAPSED,
//                    new Ref(toLog)));
        } else {
            unloggedTimePassed += time * TIME_LOGIC_MODIFIER;
        }
        clockTicks++;
        float finalTime = this.time;
        Core.onNewThread(() ->
                {
                    int delay=  clockTicks*200;
                    WaitMaster.WAIT(delay);
//                    GuiEventManager.trigger(AphosEvent.NEW_ATB_TIME, finalTime);
                }
        );
//        GuiEventManager.trigger(AphosEvent.TIME_PASSED, time);
        if (time > 0)
            LOG.log(AtbHelper.getTimeString(time), " passed, ",
                    AtbHelper.getTimeString(SECONDS_IN_ROUND - this.time),
                    " until end of round");
//        Aphos.getGame().getManager().atbTimeElapsed(time);
    }

    protected void addTime(Float time) {
        this.time += time;
        this.totalTime += time;
    }

    protected boolean isPrecalc() {
        return false;
    }

    protected void updateTurnOrder() {
        this.unitsInAtb.sort(AtbHelper::compareForSort);
    }

    protected void updateTimeTillTurn() {
        for (AtbEntity unit : this.unitsInAtb) {
            //            if (unit.getInitiative() <= 0) {
            //                unit.setTimeTillTurn(Float.MAX_VALUE);
            //            } else {
            unit.setTimeTillTurn(
                    AtbHelper.calculateTimeTillTurn(unit));
            //            }

        }
    }

    protected void removeEntity(AtbEntity unit) {
        int index = this.unitsInAtb.indexOf(unit);
        if (index > -1) {
            this.unitsInAtb.remove(unit);
        }
        this.processAtbRelevantEvent();
    }

    protected void addEntity(AtbEntity unit) {
        // if (unit.getInitiative() > 0) { wtf???
        unit.setAtbReadiness(unit.getInitialInitiative());
        this.unitsInAtb.add(unit);
        // }
    }

    public AtbEntity getAtbEntity(Entity unit) {
        for (AtbEntity sub : unitsInAtb) {
            if (sub.getEntity() == unit)
                return sub;
        }
        return null;
    }


    public void addEntity(Entity unit) {
        for (AtbEntity sub : unitsInAtb) {
            if (sub.getEntity() == unit)
                return;
        }
        addEntity(new AtbEntityImpl(this, unit));
//        unit.getGame().fireEvent(new Event(
//                Event.STANDARD_EVENT_TYPE.UNIT_HAS_ENTERED_COMBAT, unit.getRef()));
    }

    public void checkForRemoval() {
        unitsInAtb.removeIf(u -> !u.getEntity().isOnAtb());
    }
    public void removeEntity(Entity unit) {
        unitsInAtb.removeIf(u -> u.getEntity() == unit);
    }

    List<AtbEntity> getUnits() {
        return unitsInAtb;
    }

    public String getTimeString() {
        return AtbHelper.getTimeString(SECONDS_IN_ROUND - getTime());
    }

    public float getTime() {
        return time;
    }

    public int getStep() {
        return step;
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
