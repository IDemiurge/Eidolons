package eidolons.game.module.adventure.global.time;

import eidolons.game.module.adventure.MacroGame;
import eidolons.game.module.adventure.entity.party.MacroParty;
import eidolons.game.module.adventure.map.MapVisionMaster.MAP_OBJ_INFO_LEVEL;
import eidolons.game.module.adventure.map.Place;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.WEATHER;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.DirectionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.MapEvent;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 2/10/2018.
 */
public class MacroTimeMaster {
    private static final int DEFAULT_DAY_TIME = 0;
    //public static final float
    private static MacroTimeMaster instance;
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
    private WEATHER weather;
    private GameDate date;
    private boolean fastforward;
    private boolean playerCamping;
    private float defaultSpeed;
    private DIRECTION windDirection;
    private float timer;

    private MacroTimeMaster() {
        defaultSpeed = new Float(OptionsMaster.getGameplayOptions().
         getIntValue(GAMEPLAY_OPTION.GAME_SPEED)) / 100;
        setSpeed(defaultSpeed);
    }

    public static MacroTimeMaster getInstance() {
        if (instance == null) {
            instance = new MacroTimeMaster();
        }
        return instance;
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
        if (dayTime == null) {
            newDayTime(DEFAULT_DAY_TIME);
            return;
        }

        if (minuteCounter < 60) {
            return;
        }
        float passed = minuteCounter / 60;
        minuteCounter = minuteCounter % 60;
        GameDate date = getDate();
        int hour = date.getHour();
        hour += passed;
        boolean newDay = false;
        if (hour > 24) {
            newDay = true;
            hour = hour - 24;
        }
        if (newDay)
            date.nextDay();

        date.setHour(hour);
        //check month changes too
        int newPeriod = hour / getPeriod();
        if (newPeriod >= getPeriods())
            newPeriod = 0;

        if (newPeriod != lastPeriod) {
            lastPeriod = newPeriod;
            newDayTime(newPeriod);
        }
        GuiEventManager.trigger(MapEvent.DATE_CHANGED, date);
        //recalc from start? or accrue?
    }


    public void nextPeriod() {
        hoursPassed(4);

    }

    public void newMonth() {
        date.nextMonth();
        GuiEventManager.trigger(MapEvent.PREPARE_TIME_CHANGED,DAY_TIME. DAWN);
    }

    private void newDayTime(int newPeriod) {
        dayTime = times[newPeriod];
        timer = 0;
        weather = new EnumMaster<WEATHER>().getRandomEnumConst(WEATHER.class);
        windDirection = DirectionMaster.getRandomDirection();
        if (windDirection.growY == null || windDirection.growX == null)
            if (RandomWizard.random())
                windDirection = DIRECTION.UP_RIGHT;

        getDate().setDayTime(dayTime);
        GuiEventManager.trigger(MapEvent.PREPARE_TIME_CHANGED, dayTime);
        GuiEventManager.trigger(GuiEventType.LOG_ENTRY_ADDED, dayTime.getLogEntry());
    }

    public DIRECTION getWindDirection() {
        if (windDirection == null)
            windDirection = DIRECTION.UP_RIGHT;
        return windDirection;
    }

    public WEATHER getWeather() {
        if (weather == null)
            weather = WEATHER.CLEAR;
        return weather;
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
        for (int h = 0; h < i - 1; h++) {
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
        if (MacroGame.getGame() == null)
            return;
        Coordinates c = MacroGame.getGame().getPlayerParty().getCoordinates();
        for (Place place : MacroGame.getGame().getState().getPlaces()) {
            MAP_OBJ_INFO_LEVEL infoLevel = MAP_OBJ_INFO_LEVEL.UNKNOWN;
            if (place.getCoordinates().dst(c) < 500) {
                infoLevel = MAP_OBJ_INFO_LEVEL.KNOWN;
                place.setDetected(true);
            }
            place.setInfoLevel(infoLevel);
        }
//            for (MacroParty party : MacroGame.getGame().getState().getParties()) {
//            if (party.getCurrentDestination() == null)
//                continue;
//    TravelManager.travel(delta);
//            TravelMaster.travel(party, delta);
//        }
    }


    public void act(float delta) {
        delta = delta * speed;
        time += delta;
        timer += delta;
    }

    public float getTimer() {
        return timer;
    }

    public float getTime() {
        return time;
    }

    public boolean isPlayerCamping() {
        return playerCamping;
    }

    public void setPlayerCamping(boolean playerCamping) {
        this.playerCamping = playerCamping;
    }

    public void resetSpeed() {
        if (MacroGame.getGame().getLoop().isPaused()) {
            setSpeed(defaultSpeed / 4);
        } else {
            setSpeed(defaultSpeed);
        }
    }

    public float getPercentageIntoNextDaytime() {
        return 0.25f * (getDate().getHour() % 4 + getMinuteCounter() / 60);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getMinuteCounter() {
        return minuteCounter;
    }

    public void speedDown() {
        if (speed > defaultSpeed)
            speed = speed - defaultSpeed / 2;
        else speed -= speed / 3;
        if (speed <= 0.1f)
            speed = 0.1f;
    }

    public void speedUp() {
        if (speed < defaultSpeed)
            speed = speed + defaultSpeed / 2;
        else speed += speed / 3;
        if (speed >= getMaxSpeed())
            speed = getMaxSpeed();
    }

    private float getMaxSpeed() {
        if (CoreEngine.isFastMode())
            return 100f;
        return 10f;
    }

    public DAY_TIME getDayTime() {
        return dayTime;
    }
}
