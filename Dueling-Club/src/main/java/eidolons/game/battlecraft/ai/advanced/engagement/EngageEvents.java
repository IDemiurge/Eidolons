package eidolons.game.battlecraft.ai.advanced.engagement;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.advanced.engagement.EngageEvent.ENGAGE_EVENT;
import eidolons.game.module.dungeoncrawl.explore.ExplorationHandler;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.bf.grid.GridViewAnimator;
import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.ENGAGEMENT_LEVEL;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.DequeImpl;
import main.system.sound.SoundMaster;
import main.system.text.LogManager;

import java.util.LinkedHashSet;
import java.util.Set;

import static main.system.auxiliary.log.LogMaster.log;

public class EngageEvents extends ExplorationHandler {

    private static final float TIMER_PERIOD = 0.25f;
    DequeImpl<EngageEvent> eventQueue = new DequeImpl<>();
    EngageEventProcessor processor;
    Set<Unit> alertedEnemies = new LinkedHashSet<>();
    Set<BattleFieldObject> spottedEnemies = new LinkedHashSet<>();

    public EngageEvents(ExplorationMaster master) {
        super(master);
        processor = new EngageEventProcessor(master);
    }

    @Override
    public float getTimerPeriod() {
        return TIMER_PERIOD;
    }

    @Override
    protected void timerEvent() {
        EngageEvent processed = null;
        for (EngageEvent engageEvent : eventQueue) {
            if (engageEvent.delay <= 0) {
                processor.process(engageEvent);
                processed = engageEvent;
                timer = TIMER_PERIOD;
                break;
            }
        }
        if (processed != null) {
            eventQueue.remove(processed);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (EngageEvent engageEvent : eventQueue) {
            engageEvent.delay -= delta;
        }
    }

    public void combatStarted() {
        String path = null;
        soundEvent(path);
    }

    private void soundEvent(String path) {
        addEvent(new EngageEvent(ENGAGE_EVENT.sound, path));
    }

    private boolean isDetectionSoundOn(Unit source, BattleFieldObject object) {
        if (source.isPlayerCharacter()) {
            if (!object.isSneaking())
                if (!object.isDisabled())
                    return !object.isAlliedTo(source.getOwner());
        }
        return false;
    }

    public void newAlert(Unit source, BattleFieldObject object) {
        /*
        an outline came into sight
        - sound of alarm
        - possibly some text, if not already
        - change status parameters
         */
        if (source.isMine()) {
            if (isCombat()) {
                //warning about aggro of other enemies
                addEvent(ENGAGE_EVENT.popup, getAggroWarning(object));
                return;
            }
            spottedEnemies.add(object);
            addEvent(ENGAGE_EVENT.status_change, VisionEnums.PLAYER_STATUS.ALERTED, spottedEnemies.size());
            addEvent(source, object, ENGAGE_EVENT.sound, SoundMaster.SOUNDS.ALERT);
            logEvent(LogManager.LOGGING_DETAIL_LEVEL.FULL, source.getName() + " is alerted by an outline!");
        } else {
            if (isCombat()) {
                //what's to stop them from aggro?
                //what to do if 2nd encounter is aggro-ed? A lite version of combat intro... no mercy, but maybe
                //no 2nd reinf. BTW Reinforcements could be handle via these events?

                //text event or how to warn player? only if mutual?
                return;
            }
            alertedEnemies.add(source);
            if (!isMutual(ENGAGEMENT_LEVEL.SUSPECTING, source, object)) {
                addEvent(source, object, ENGAGE_EVENT.ai_status_change,
                        ENGAGEMENT_LEVEL.ALERTED);
            } else {
                addEvent(ENGAGE_EVENT.status_change, VisionEnums.PLAYER_STATUS.ALERTED, alertedEnemies.size());
                logEvent(LogManager.LOGGING_DETAIL_LEVEL.FULL, "An enemy has been alerted!");
            }
        }
    }

    private String getAggroWarning(BattleFieldObject object) {
        return "I really don't want to draw this " + object.getName() + " into the fight just yet...";
    }

    private boolean isMutual(ENGAGEMENT_LEVEL level, Unit source, BattleFieldObject object) {
        if (source.isMine()) {
            switch (level) {
                //should we introduce more for player?
                case UNSUSPECTING:
                    return getPlayerStatus().getType() == VisionEnums.PLAYER_STATUS.EXPLORATION_DETECTED;
                case SUSPECTING:
                    break;
                case ALERTED:
                    break;
            }
        } else {
            return source.getAI().getGroup().getEngagementLevel() == level;
        }
        return false;
    }

    private PlayerStatus getPlayerStatus() {
        return master.getPlayerStatus();
    }

    public void detected(Unit source, BattleFieldObject object) {
        if (source.isMine()) {
            processNow(new EngageEvent(source, object, GridViewAnimator.VIEW_ANIM.screen));
        } else {
            // if (isPrecombat())
            // addEvent(new EngageEvent(source, object, ENGAGE_EVENT.precombat));
            addEvent(new EngageEvent(source, object, ENGAGE_EVENT.engagement_change, ENGAGEMENT_LEVEL.ENGAGED));
        }
        if (isDetectionLogged(source, object)) {
            logReveal(source, object);
        }
        if (isDetectionSoundOn(source, object)) {
            addEvent(new EngageEvent(source, object, SoundMaster.SOUNDS.ALERT));
        }
    }

    public void lostSight(Unit source, BattleFieldObject object) {
        if (!source.isMine()) {
            return; //TODO anything?
        }
        if (isCombat()) {
            //punishing retreat
        } else {
            // processNow(new EngageEvent(source, object, GridViewAnimator.VIEW_ANIM.screen));//flash before hiding
        }
        if (isDetectionLogged(source, object))
            logHide(source, object);
    }

    private boolean isDetectionLogged(Unit source, BattleFieldObject object) {


        if (source != object)
            if (source.isMine())
                return source.isHostileTo(object.getOwner());
        return false;
    }

    public void logHide(Unit source, BattleFieldObject object) {
        LogManager.LOGGING_DETAIL_LEVEL level = LogManager.LOGGING_DETAIL_LEVEL.FULL;
        if (source.isPlayerCharacter()) {
            level = LogManager.LOGGING_DETAIL_LEVEL.ESSENTIAL;
        }
        logEvent(level, source + " loses sight of " + object.getName());
    }

    public void logReveal(Unit source, BattleFieldObject object) {
        LogManager.LOGGING_DETAIL_LEVEL level = LogManager.LOGGING_DETAIL_LEVEL.FULL;
        if (source.isPlayerCharacter()) {
            level = LogManager.LOGGING_DETAIL_LEVEL.ESSENTIAL;
        }
        logEvent(level, source + " spots " + object.getName());
    }

    private void logEvent(LogManager.LOGGING_DETAIL_LEVEL level, String s) {
        EngageEvent event = new EngageEvent(ENGAGE_EVENT.log, level);
        event.logMsg = s;
        addEvent(event);
    }

    public void addEvent(Object... args) {
        addEvent(new EngageEvent(args));
    }

    public void addEvent(Unit source, BattleFieldObject target, Object... args) {
        addEvent(new EngageEvent(source, target, args));
    }

    public void addEvent(EngageEvent event) {
        event.delay = RandomWizard.getRandomFloatBetween(0.5f, 1f); //TODO specific
        if (isLogged()) {
            log(1, "Event added:" + event);
        }
        eventQueue.add(event);
    }

    public void processNow(EngageEvent engageEvent) {
        if (isLogged()) {
            log(1, "Process immediately:" + engageEvent);
        }
        processor.process(engageEvent);
    }

    public static boolean isLogged() {
        return true;
    }

    public void clearQueue() {
        eventQueue.clear();
        //check if some need to be processed immediately?
        //double combat-start from different groups? impossible?
    }
}
