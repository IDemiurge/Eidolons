package main.game.module.adventure;

import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.game.module.adventure.entity.MacroParty;
import main.game.module.adventure.global.GameDate;
import main.game.module.adventure.global.TimeMaster;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.options.GameplayOptions.GAMEPLAY_OPTION;
import main.system.options.OptionsMaster;

/**
 * Created by JustMe on 2/10/2018.
 */
public class MacroTimeMaster {
    private static final int DEFAULT_DAY_TIME = 0;
//public static final float

    int period = 24 / DAY_TIME.values().length;
    private float speed = 1;
    private float time = 0;
    private float lastTimeChecked;
    private float minuteCounter;
    private float delta;
    private boolean guiDirtyFlag;
    private int lastPeriod;
    private DAY_TIME[] times = DAY_TIME.values();
    private DAY_TIME dayTime;
    private GameDate date;
    private boolean fastforward;
    private boolean playerCamping;
    private float defaultSpeed;

    public MacroTimeMaster() {
        defaultSpeed = new Float(OptionsMaster.getGameplayOptions().
         getIntValue(GAMEPLAY_OPTION.GAME_SPEED)) / 100;
        setSpeed(defaultSpeed);
    }

    private void processTimedEffects() {
        processParameters();
        processModeEffects();
    }

    private void processParameters() {
        //fatigue etc
    }

    private void processModeEffects() {
        for (MacroParty sub : MacroGame.getGame().getParties()) {
            //dif modes per hero?

        }
    }

    private void updateDate() {
        //default: second == minute
        if (dayTime == null)
        {
            newDayTime(DEFAULT_DAY_TIME);
        }

        if (minuteCounter < 60) {
            return;
        }
        float passed = minuteCounter / 60;
        minuteCounter = minuteCounter%60;
        GameDate date = getDate();
        int hour = date.getHour();
        hour+=passed;
        boolean newDay = false;
        if (hour > 24) {
            newDay = true;
            hour =hour-24;
        }
        if (newDay)
            date.nextDay();
        date.setHour(hour);
        //check month changes too
        int newPeriod = hour / getPeriod();
        if (newPeriod >= getPeriods())
            newPeriod = 0;
        if (dayTime == null || newPeriod != lastPeriod) {
            lastPeriod = newPeriod;
            newDayTime(newPeriod);
        }
        GuiEventManager.trigger(MapEvent.DATE_CHANGED, date);
        //recalc from start? or accrue?
    }

    private void newDayTime(int newPeriod) {
        dayTime = times[newPeriod];
        MacroGame.getGame().setTime(dayTime);
        date.setDayTime(dayTime);
        GuiEventManager.trigger(MapEvent.TIME_CHANGED, dayTime);
    }

    private int getPeriods() {
        return DAY_TIME.values().length;
    }

    private int getPeriod() {
        return period;
    }

    private GameDate getDate() {
        //base date + time
        if (date == null)
            date = TimeMaster.getDate();
        return date;
    }
//after combat or for camping
    public void hoursPassed(int i) {
        fastforward = true;
        for (int h = 0; h < i-1; h++) {
            time += 60;
            timedCheck();
        }
        //main party is ignored if in combat
        fastforward = false;
        timedCheck();
    }
    public void timedCheck() {
        delta = time - lastTimeChecked;
        if (delta == 0) return;
        lastTimeChecked = time;
        minuteCounter += delta;
        //perhaps create new Date each time instead?
//        Calendar.getInstance().getTime().get

        updateDate();
        processMapObjects();
        checkScripts();

    }

    private void checkScripts() {
    }

    private void processMapObjects() {
        for (MacroParty party : MacroGame.getGame().getState().getParties()) {
            if (party.getCurrentDestination() == null)
                continue;
//    TravelManager.travel(delta);
            TravelMaster.travel(party, delta);
        }
    }


    public void act(float delta) {
        time += delta * speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setPlayerCamping(boolean playerCamping) {
        this.playerCamping = playerCamping;
    }

    public boolean isPlayerCamping() {
        return playerCamping;
    }

    public void resetSpeed() {
        if (MacroGame.getGame().getLoop().isPaused()) {
            setSpeed(defaultSpeed/4);
        } else {
            setSpeed(defaultSpeed);
        }
    }

    public float getSpeed() {
        return speed;
    }

    public float getMinuteCounter() {
        return minuteCounter;
    }

    public void speedDown() {
        speed-=defaultSpeed/3;
        if (speed<=0.1f)
            speed=0.1f;
    }
    public void speedUp() {
        speed+=defaultSpeed/3;
        if (speed>=10f)
            speed=10f;
    }
}
