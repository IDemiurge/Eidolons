package main.libgdx.anims;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import javafx.util.Pair;
import main.ability.effects.Effect;
import main.data.XLinkedMap;
import main.entity.active.DC_ActiveObj;
import main.game.battlefield.Coordinates;
import main.game.event.Event;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.phased.PhaseAnim;
import main.libgdx.anims.std.EffectAnimCreator;
import main.libgdx.anims.std.EventAnimCreator;
import main.libgdx.anims.text.FloatingTextMaster;
import main.system.EventCallback;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.data.MapMaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 1/11/2017.
 */
public class CompositeAnim implements Animation {

    Map<ANIM_PART, Anim> map = new XLinkedMap<>();
    Map<ANIM_PART, List<Pair<GuiEventType, EventCallbackParam>>> onStartEventMap;
    Map<ANIM_PART, List<Pair<GuiEventType, EventCallbackParam>>> onFinishEventMap;
    Map<ANIM_PART, List<Animation>> attached;
    Map<ANIM_PART, List<Animation>> timeAttachedAnims;
    ANIM_PART part;
    int index;
    PhaseAnim phaseAnim;
    private boolean finished;
    private boolean running;
    private Anim currentAnim;
    private float time = 0;
    private List<Event> textEvents;

    public CompositeAnim(Anim... anims) {
        this(new MapMaster<ANIM_PART, Anim>().constructMap(new LinkedList<>(Arrays.asList(ANIM_PART.values()).subList(0, anims.length)),
         new LinkedList<>(Arrays.asList(anims))));

    }

    public CompositeAnim(Map<ANIM_PART, Anim> map) {
        this.map = map;
        reset();
        resetMaps();
    }

    public CompositeAnim(DC_ActiveObj active) {
        this(new XLinkedMap<>());
    }

    public boolean draw(Batch batch) {
//        if (currentAnim != null)
//            try{ //TODO attached phaseAnims?
//            DC_Game.game.getAnimationManager().getAnimations().forEach(a -> {
//                PhaseAnim phaseAnim= a.getPhaseAnim();
////                main.system.auxiliary.LogMaster.log(1,"drawing " +a + " at " + currentAnim.getPosition());
//                phaseAnim.setPosition(currentAnim.getX(),currentAnim.getY() );
//                phaseAnim.draw(batch, 1f);
//            });        }catch(Exception e){                e.printStackTrace();            }
        time += Gdx.graphics.getDeltaTime();
        boolean result = false;
        if (currentAnim != null) {
            try {
                result = currentAnim.draw(batch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        checkTimeAttachedAnims();
        if (!result) {
            time = 0;
            index++;
            triggerFinishEvents();
            playAttached();
            if (map.size() <= index) {
                checkAfterEffects();
                finished();
                return false;
            }
            initPartAnim();
            currentAnim.start();
            triggerStartEvents();
        }
        drawAttached(batch);
        return true;
    }

    private void checkAfterEffects() {
        if (!map.containsKey(ANIM_PART.AFTEREFFECT)) {

            if (timeAttachedAnims.get(ANIM_PART.AFTEREFFECT) != null) {
                timeAttachedAnims.get(ANIM_PART.AFTEREFFECT).forEach(a -> {
                    a.start();
//                    a.setDelayNotCounted(true); TODO
                    AnimMaster.getInstance().addAttached(a);
                });
            }
            if (attached.get(ANIM_PART.AFTEREFFECT) != null) {
                attached.get(ANIM_PART.AFTEREFFECT).forEach(a -> {
                    a.start();
                    AnimMaster.getInstance().addAttached(a);
                });
            }
            part = ANIM_PART.AFTEREFFECT; //TODO rework this!
            triggerFinishEvents();
        }

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
        timeAttachedAnims.get(part).removeIf(a -> a.isRunning());
    }


    private void drawAttached(Batch batch) {
        List<Animation> list = attached.get(part);
        if (list == null) {
            return;
        }
        list.forEach(anim -> {
            if (!anim.isRunning()) {
                anim.start();
            }
            anim.draw(batch);

        });
    }

    private void playAttached() {
        List<Animation> list = attached.get(part);
        if (list != null) {
            list.forEach(anim -> {
                anim.start();

            });
        }
    }

    public void finished() {
        index = 0;
        part = null;
        finished = true;
        running = false;

        resetMaps();
    }

    private void resetMaps() {
        onStartEventMap = new XLinkedMap<>();
        onFinishEventMap = new XLinkedMap<>();
        attached = new XLinkedMap<>();
        timeAttachedAnims = new XLinkedMap<>();

    }


    private void triggerStartEvents() {
        if (onStartEventMap.get(part) != null) {
            onStartEventMap.get(part).forEach(e -> {
                GuiEventManager.trigger(e.getKey(), e.getValue());
            });
        }
    }

    private void triggerFinishEvents() {
        if (onFinishEventMap.get(part) != null) {
            onFinishEventMap.get(part).forEach(e -> {
                GuiEventManager.trigger(e.getKey(), e.getValue());
            });
        }
    }

    public void add(ANIM_PART part, Anim anim) {
        map.put(part, anim);
        addEvents(part, anim);

    }

    private void addEvents(ANIM_PART part, Anim anim) {
        MapMaster.addToListMap(onFinishEventMap,
         part, anim.getEventsOnFinish());
        MapMaster.addToListMap(onStartEventMap,
         part, anim.getEventsOnStart());
    }

    public void reset() {
        map.values().forEach(anim -> {
            anim.reset();
        });
        finished = false;
    }

    public void start() {
        time = 0;
        index = 0;
        initPartAnim();
        if (currentAnim == null) {
            return;
        }

        currentAnim.start();
        triggerStartEvents();

        if (map.isEmpty()) {
            return;
        }
        queueGraphicEvents();
        queueTextEvents();
        running = true;
        LogMaster.log(LogMaster.ANIM_DEBUG, this + " started: "
        );

    }

    @Override
    public String toString() {

        return getClass().getSimpleName() + map;
    }

    private void queueGraphicEvents() {
//        getOnStartEventMap().values().forEach(
//         (List<GuiEventType> e) -> e.forEach(x -> AnimEventMaster.queue(x)));
    }

    private void initPartAnim() {
        if (map.isEmpty()) {
            return;
        }
        part = (ANIM_PART) MapMaster.get(map, index);
        currentAnim = map.get(part);
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
        if (part == null) {
            partToAddAt = EventAnimCreator.getPartToAttachTo(event);
        }
        float delay = EventAnimCreator.getEventAnimDelay(event, anim, partToAddAt);
        attach(anim, partToAddAt, delay);

    }

    private void attach(Animation anim, ANIM_PART partToAddAt, float delay) {
        if (delay != 0) {
            anim.setDelay(delay);
            attachDelayed(anim, partToAddAt);
        } else {
            attach(anim, partToAddAt);
        }
        if (anim instanceof Anim) {
            addEvents(partToAddAt, (Anim) anim);
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
//                Anim anim = AnimMaster.getInstance().getConstructor().
//                 getPartAnim((DC_ActiveObj) currentAnim.getActive(), part);
//
//                anim.setDelay(delay);
//                attachedAnims.add(anim);
//            }
//        }
//    }
    public void interrupt() {
        //at what part?
//        setInterruptPart(p);
//        setInterruptMethod(method); //fade, flash, drop, skip
    }

    public boolean isFinished() {
        return finished;
    }

    public Map<ANIM_PART, Anim> getMap() {
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

    public Anim getCurrentAnim() {
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
        if (map.isEmpty()) return;
        Anim lastAnim = (Anim) map.values().toArray()[map.size() - 1];
        lastAnim.onDone(callback, param);
    }

    @Override
    public boolean isRunning() {
        return running;
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
        if (textEvents == null) textEvents = new LinkedList<>();
        return textEvents;
    }

    public void setForcedDestination(Coordinates forcedDestination) {
        map.values().forEach(anim -> {
            if (anim.getPart() != ANIM_PART.CAST)
                if (anim.getPart() != ANIM_PART.PRECAST)
                    if (anim.getPart() != ANIM_PART.RESOLVE)
                        anim.setForcedDestination(forcedDestination);
        });
    }
}
