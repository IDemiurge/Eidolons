package libgdx.anims;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.content.consts.VisualEnums.ANIM_PART;
import libgdx.anims.main.AnimMaster;
import libgdx.anims.main.EffectAnimCreator;
import libgdx.anims.main.EventAnimCreator;
import libgdx.anims.std.DeathAnim;
import libgdx.anims.std.SpellAnim;
import libgdx.anims.text.FloatingTextMaster;
import main.ability.effects.Effect;
import main.data.XLinkedMap;
import main.entity.Entity;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.system.EventCallback;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 1/11/2017.
 */
public class CompositeAnim implements Animation {

    //ObjectMap
    Map<ANIM_PART, Animation> map = new XLinkedMap<>();
    Map<ANIM_PART, List<Pair<GuiEventType, EventCallbackParam>>> onStartEventMap;
    Map<ANIM_PART, List<Pair<GuiEventType, EventCallbackParam>>> onFinishEventMap;
    Map<ANIM_PART, List<Animation>> attached;
    Map<ANIM_PART, List<Animation>> timeAttachedAnims;
    private final List<Animation> parallelAnims = new ArrayList<>();
    ANIM_PART part;
    int index;
    private boolean finished;
    private boolean running;
    private Animation currentAnim;
    private float time = 0;
    private List<Event> textEvents;
    private Ref ref;
    private Animation continuous;
    private boolean hpUpdate = true;
    private boolean waitingForNext;
    private boolean parallel;
//    List<Anim> parallelAnims; what was the idea exactly?


    public CompositeAnim(Animation... anims) {
        this(new MapMaster<ANIM_PART, Animation>().constructMap(new ArrayList<>(Arrays.asList(VisualEnums.ANIM_PART.values()).subList(0, anims.length)),
                new ArrayList<>(Arrays.asList(anims))));

    }

    public CompositeAnim(Map<ANIM_PART, Animation> map) {
        this.map = map;
        resetMaps();
        reset();
    }

    public CompositeAnim(ActiveObj active) {
        this(new XLinkedMap<>());
    }

    public boolean tryDraw(Batch batch) {
        if (isFinished()) return false;
        if (!isRunning()) {
            if (isParallel()){
                start();
            } else
            return false;
        }
        try {
            return draw(batch);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            main.system.auxiliary.log.LogMaster.log(1, "Anim error for active: " + getActive());
            setRunning(false);
        }
        return false;
    }


    private void checkParallelAnims() {
        if (isParallel()) {
            int i = 0;
            for (ANIM_PART anim_part : map.keySet()) {
                if (i++ <= index)
                    continue;
                if (anim_part != VisualEnums.ANIM_PART.MISSILE) {
                    if (!parallelAnims.contains(map.get(anim_part))) {
                        parallelAnims.add(map.get(anim_part));
                    break;
                    }
                }

            }
        }
    }

    private boolean isParallel() {
        if (currentAnim instanceof SpellAnim) {
            if (part == VisualEnums.ANIM_PART.MISSILE) {
                return time <= 2;
            }
        }
        if (currentAnim instanceof SimpleAnim) {
            return ((SimpleAnim) currentAnim).isParallel();
        }
        return parallel;
    }

    public boolean draw(Batch batch) {

        time += Gdx.graphics.getDeltaTime();
        boolean result = false;
        if (currentAnim != null) {
            try {
                result = currentAnim.tryDraw(batch);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (!parallelAnims.isEmpty()) {
            for (Animation parallelAnim :     new ArrayList<>(parallelAnims))  {
                if (!parallelAnim.tryDraw(batch)) {
//                    parallelAnim.finished();
                    parallelAnims.remove(parallelAnim);
                    main.system.auxiliary.log.LogMaster.devLog("Finished parallel: " +parallelAnim);
                }

            }
        }

        checkTimeAttachedAnims();
        checkParallelAnims();
        if (!result) {
            time = 0;
            index++;
            triggerFinishEvents();
            playAttached();

            if (map.size() <= index) {
                if (!checkAfterEffects()) {
                    finished();
                    if (currentAnim instanceof DeathAnim) {
                        map.remove(VisualEnums.ANIM_PART.AFTEREFFECT);
                    }
                    return false;
                }
            }
            if (currentAnim instanceof DeathAnim) {
                map.remove(VisualEnums.ANIM_PART.AFTEREFFECT);
            } //TODO EA hack
            initPartAnim();
            if (currentAnim == null)
                return false;
            currentAnim.start(getRef());
            triggerStartEvents();
        }
        drawAttached(batch);

//     TODO anim Review
        //      if (parallelAnims.isEmpty()){
//            for (Anim sub : new ArrayList<>(parallelAnims)) {
//                if (!sub.draw(batch)) {
//                    parallelAnims.remove(sub);
//                }
//            }
//        }
        return true;
    }


    @Override
    public void start(Ref ref) {
        if (isRunning())
            return;
        main.system.auxiliary.log.LogMaster.log(LogMaster.ANIM_DEBUG, this + " started ");
        setRef(ref);
        start();
    }

    private boolean checkAfterEffects() {
        if (attached.containsKey(VisualEnums.ANIM_PART.AFTEREFFECT))
//        if (OptionsMaster.getAnimOptions().getBooleanValue(ANIMATION_OPTION. GENERATE_AFTER_EFFECTS))
        {
            if (timeAttachedAnims.get(VisualEnums.ANIM_PART.AFTEREFFECT) != null) {
                timeAttachedAnims.get(VisualEnums.ANIM_PART.AFTEREFFECT).forEach(a -> {
                    a.start(getRef());
//                    a.setDelayNotCounted(true); TODO
                    AnimMaster.getInstance().addAttached(a);
                });
            }
//            if (attached.getVar(ANIM_PART.AFTEREFFECT) != null) {
//                attached.getVar(ANIM_PART.AFTEREFFECT).forEach(a -> {
//                    a.start(getRef());
//                    AnimMaster.getInstance().addAttached(a);
//                });
//            }
            if (attached.get(VisualEnums.ANIM_PART.AFTEREFFECT).isEmpty()) {
                return false;
            }
            for (Animation sub : new ArrayList<>(attached.get(VisualEnums.ANIM_PART.AFTEREFFECT))) {
                map.put(VisualEnums.ANIM_PART.AFTEREFFECT, sub);

            }
            if (map.containsKey(VisualEnums.ANIM_PART.AFTEREFFECT)) {
                index--;
            }
            attached.remove(VisualEnums.ANIM_PART.AFTEREFFECT);
//            part = ANIM_PART.AFTEREFFECT; //TODO rework this!
//            triggerFinishEvents();
            return true;
        }

        return false;
    }


    private void checkTimeAttachedAnims() {
        if (timeAttachedAnims.get(part) == null) {
            return;
        }
        timeAttachedAnims.get(part).forEach(a -> {
            if (!a.isRunning()) {
                if (a.getDelay() <= time) {
                    a.start();
                    AnimMaster.getInstance().addAttached(a);
                }
            }
        });
        timeAttachedAnims.get(part).removeIf(Animation::isRunning);
    }


    private void drawAttached(Batch batch) {
        List<Animation> list = attached.get(part);
        if (list == null) {
            return;
        }
        for (Animation anim : new ArrayList<>(list)) {
            if (!anim.isRunning()) {
                anim.start(getRef());
            }
            if (!anim.tryDraw(batch)) {
                list.remove(anim);
            }
        }
    }

    private void playAttached() {
        List<Animation> list = attached.get( part);
//        List<Animation> list = attached.getVar(part == null ? ANIM_PART.AFTEREFFECT : part);
        if (list != null) {
            list.forEach(anim -> {
                anim.start(getRef());

            });
        }
    }

    public void finished() {
        index = 0;
        part = null;
        finished = true;
        setRunning(false);
        resetMaps();
        if (getRef() == null) {
            return ;
        }
        if (hpUpdate) {
            if (getRef().getTargetObj() instanceof BattleFieldObject)
                GuiEventManager.trigger(GuiEventType.HP_BAR_UPDATE, getRef().getTargetObj());
            else if (getRef().getSourceObj() instanceof BattleFieldObject)
                GuiEventManager.trigger(GuiEventType.HP_BAR_UPDATE, getRef().getSourceObj());
        }
        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.ANIMATION_FINISHED, this);
    }

    private void resetMaps() {
        onStartEventMap = new XLinkedMap<>();
        onFinishEventMap = new XLinkedMap<>();
        attached = new XLinkedMap<>();
        timeAttachedAnims = new XLinkedMap<>();
        textEvents = new ArrayList<>();
    }

    private void triggerStartEvents() {
        if (currentAnim != null)
            if (currentAnim.getEventsOnStart() != null) {
                currentAnim.getEventsOnStart().forEach(e -> {
                    GuiEventManager.trigger(e.getKey(), e.getValue());
                });
            }
    }

    private void triggerFinishEvents() {
        if (currentAnim != null) {
            if (currentAnim.getEventsOnFinish() != null) {
                currentAnim.getEventsOnFinish().forEach(e -> {
                    GuiEventManager.trigger(e.getKey(), e.getValue());
                });
            }
        }
    }

    public void add(ANIM_PART part, Animation anim) {
        map.put(part, anim);
        addEvents(part, anim);

    }

    private void addEvents(ANIM_PART part, Animation anim) {
        MapMaster.addToListMap(onStartEventMap,
                part, anim.getEventsOnStart());
        MapMaster.addToListMap(onFinishEventMap,
                part, anim.getEventsOnFinish());
    }

    public void reset() {
        onStartEventMap.clear();
        onFinishEventMap.clear();
        map.values().forEach(anim -> {
            anim.reset();
            addEvents(anim.getPart(), anim);
        });
        finished = false;
        setWaitingForNext(false);
    }

    public Ref getRef() {
        return ref;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
    }

    public void start() {
        parallelAnims.clear();
        hpUpdate = true;
        time = 0;
        index = 0;
        initPartAnim();
        if (currentAnim == null) {
            return;
        }

        try {
            if (getRef() != null) {
                currentAnim.start(getRef());
            } else {
                currentAnim.start();
                setRef(currentAnim.getRef());
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return;
        }

        triggerStartEvents();

        if (map.isEmpty()) {
            return;
        }
        queueGraphicEvents();
        queueTextEvents();
        running = true;
       LogMaster.log(LogMaster.ANIM_DEBUG, this + " started: "        );


    }

    @Override
    public String toString() {

        return getClass().getSimpleName() + " for " + getActive() + ": " + map
                + "; attached: " + attached;
    }

    private void queueGraphicEvents() {
//        getOnStartEventMap().values().forEach(
//         (List<GuiEventType> e) -> e.forEach(x -> AnimEventMaster.queue(x)));
    }

    private void initPartAnim() {
        if (map.isEmpty()) {
            if (attached.isEmpty())
                return;
        }
        if (!parallelAnims.isEmpty()) {
            currentAnim = parallelAnims.remove(0);

        } else if (map.isEmpty()) {
            part = (ANIM_PART) MapMaster.get(attached, index);
            if (attached.get(part).isEmpty())
                return;
            currentAnim = attached.get(part).remove(0);
        } else {
            part = (ANIM_PART) MapMaster.get(map, index);
            currentAnim = map.get(part);
        }
        currentAnim.setParentAnim(this);
    }

    public void addEffectAnim(Animation anim, Effect effect) {
        ANIM_PART partToAddAt = anim.getPart();
        if (part == null) {
            partToAddAt = EffectAnimCreator.getPartToAttachTo(effect);
        }
        if (effect.getRef().isTriggered()) {
            partToAddAt = EventAnimCreator.getPartToAttachTo(effect.getRef().getEvent());
        }
        float delay = EffectAnimCreator.getEffectAnimDelay(effect, anim, partToAddAt);
        attach(anim, partToAddAt, delay);
        //anim group vs anim(Effects)
    }

    public void addEventAnim(Anim anim, Event event) {
        ANIM_PART partToAddAt = anim.getPart();
        if (partToAddAt == null) {
            partToAddAt = EventAnimCreator.getPartToAttachTo(event);
        }
        float delay = EventAnimCreator.getEventAnimDelay(event, anim, partToAddAt);
        attach(anim, partToAddAt, delay);

    }

    private void attach(Animation anim, ANIM_PART partToAddAt, float delay) { //if (partToAddAt==null ) partToAddAt=anim.getPart();
        if (delay != 0) {
            anim.setDelay(delay);
            attachDelayed(anim, partToAddAt);
        } else {
            attach(anim, partToAddAt);
        }
        if (anim instanceof Anim) {
            addEvents(partToAddAt, anim);
        }
        if (anim instanceof Anim) {
            anim.setParentAnim(this);
        }

    }

    public void attachDelayed(Animation anim, ANIM_PART part) {

        MapMaster.addToListMap(timeAttachedAnims, part, anim);
    }

    public void attach(Animation anim, ANIM_PART part) {
        MapMaster.addToListMap(attached, part, anim);
    }

    //    private void initTimeAttachedAnims() {
//        //TODO OR PLAY IMPACT-PART IN PARALLEL AND MULTIPLIED BY TARGETS
//        if (part == ANIM_PART.MAIN) {
//            List<Obj> targets = currentAnim.getRef().getGroup().getObjects();
//            for (Obj obj : targets) {
//                Float distance =
//                 GridMaster.getDistance(obj.getCoordinates(), currentAnim.getOriginCoordinates());
//                float delay = distance / currentAnim.getPixelsPerSecond();
//
//                Anim anim = AnimConstructor.
//                 getPartAnim((DC_ActiveObj) currentAnim.getActive(), part);
//
//                anim.setDelay(delay);
//                attachedAnims.add(anim);
//            }
//        }
//    }
    public void interrupt() {
        //at what part?
//        setInterruptPart(portrait);
//        setInterruptMethod(method); //fade, flash, drop, skip
    }

    public boolean isFinished() {
        return finished;
    }

    public Map<ANIM_PART, Animation> getMap() {
        return map;
    }

    public Map<ANIM_PART, List<Pair<GuiEventType, EventCallbackParam>>> getOnStartEventMap() {
        return onStartEventMap;
    }

    public Map<ANIM_PART, List<Pair<GuiEventType, EventCallbackParam>>> getOnFinishEventMap() {
        return onFinishEventMap;
    }

    public Map<ANIM_PART, List<Animation>> getAttached() {
        return attached;
    }

    public Map<ANIM_PART, List<Animation>> getTimeAttached() {
        return timeAttachedAnims;
    }

    public ANIM_PART getPart() {
        return part;
    }

    @Override
    public float getTime() {
        return time;
    }

    public int getIndex() {
        return index;
    }

    public Animation getCurrentAnim() {
        if (currentAnim == null) {
            initPartAnim();
        }
        return currentAnim;
    }

    @Override
    public float getDelay() {

        return getCurrentAnim().getDelay();
    }

    @Override
    public void setDelay(float delay) {
        getCurrentAnim().setDelay(delay);
    }

    @Override
    public void onDone(EventCallback callback, EventCallbackParam param) {
        if (map.isEmpty()) {
            return;
        }
        Anim lastAnim = (Anim) map.values().toArray()[map.size() - 1];
        lastAnim.onDone(callback, param);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void addTextEvent(Event event) {
        getTextEvents().add(event);
    }

    public void queueTextEvents() {
        getTextEvents().forEach(event ->
                FloatingTextMaster.getInstance().addFloatingTextForEventAnim(event, this));
//        AnimMaster;
    }

    public List<Event> getTextEvents() {
        if (textEvents == null) {
            textEvents = new ArrayList<>();
        }
        return textEvents;
    }

    public void setForcedDestination(Coordinates forcedDestination) {
        map.values().forEach(anim -> {
            if (anim.getPart() != VisualEnums.ANIM_PART.CAST) {
                if (anim.getPart() != VisualEnums.ANIM_PART.PRECAST) {
                    if (anim.getPart() != VisualEnums.ANIM_PART.RESOLVE) {
                        anim.setForcedDestination(forcedDestination);
                    }
                }
            }
        });
    }

    @Override
    public void setParentAnim(CompositeAnim compositeAnim) {

    }

    public ActiveObj getActive_() {
        return (ActiveObj) getActive();
    }

    public Entity getActive() {
        if (getCurrentAnim() == null)
            return null;
        return getCurrentAnim().getActive();
    }

    @Override
    public boolean isDone() {
        return finished;
    }

    @Override
    public void setDone(boolean b) {
        finished = b;
    }

    public Animation getContinuous() {
        return continuous;
    }

    public void setContinuous(Animation continuous) {
        this.continuous = continuous;
    }

    public boolean isEventAnim() {
        if (map.isEmpty())
            return !attached.isEmpty();
        return false;
    }

    public boolean isHpUpdate() {
        return hpUpdate;
    }

    public void setHpUpdate(boolean hpUpdate) {
        this.hpUpdate = hpUpdate;
    }

    public boolean isWaitingForNext() {
        return waitingForNext;
    }

    public void setWaitingForNext(boolean waitingForNext) {
        this.waitingForNext = waitingForNext;
    }
}
