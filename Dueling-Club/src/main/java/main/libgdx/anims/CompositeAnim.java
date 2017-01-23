package main.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import main.ability.effects.Effect;
import main.data.XLinkedMap;
import main.entity.obj.top.DC_ActiveObj;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.phased.PhaseAnim;
import main.libgdx.anims.std.EffectAnimCreator;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.LogMaster;
import main.system.auxiliary.MapMaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 1/11/2017.
 */
public class CompositeAnim {

    Map<ANIM_PART, Anim> map = new XLinkedMap<>();
    Map<ANIM_PART, List<GuiEventType>> startEventMap;
    Map<ANIM_PART, List<GuiEventType>> eventMap;
    Map<ANIM_PART, List<CompositeAnim>> attached;
    ANIM_PART part;
    int index;
    PhaseAnim phaseAnim;
    private boolean finished;
    private boolean running;
    private Anim currentAnim;

    public CompositeAnim(Anim... anims) {
        this(new MapMaster<ANIM_PART, Anim>().constructMap(new LinkedList<>(Arrays.asList(ANIM_PART.values()).subList(0, anims.length)),
                new LinkedList<>(Arrays.asList(anims))));

    }

    public CompositeAnim(Map<ANIM_PART, Anim> map) {
        this.map = map;
        reset();
    }

    public CompositeAnim(DC_ActiveObj active) {
        this(new XLinkedMap<>());
    }

    public boolean draw(Batch batch) {
//        if (currentAnim != null)
//            try{
//            DC_Game.game.getAnimationManager().getAnimations().forEach(a -> {
//                PhaseAnim phaseAnim= a.getPhaseAnim();
////                main.system.auxiliary.LogMaster.log(1,"drawing " +a + " at " + currentAnim.getPosition());
//                phaseAnim.setPosition(currentAnim.getX(),currentAnim.getY() );
//                phaseAnim.draw(batch, 1f);
//            });        }catch(Exception e){                e.printStackTrace();            }

        boolean result = false;
        if (currentAnim != null)
            try {
                result = currentAnim.draw(batch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (!result) {
            index++;
            triggerFinishEvents();
            playAttached();
            if (map.size() <= index) {
                finished();
                return false;
            }
            initPartAnim();
        }
        drawAttached(batch);
        return true;
    }


    public void attach(CompositeAnim anim, ANIM_PART part) {
        new MapMaster<ANIM_PART, Anim>().addToListMap(attached, part, anim);
    }

    private void drawAttached(Batch batch) {
        List<CompositeAnim> list = attached.get(part);
        if (list == null) return;
        list.forEach(anim -> {
            anim.draw(batch);

        });
    }

    private void playAttached() {
        List<CompositeAnim> list = attached.get(part);
        if (list != null)
            list.forEach(anim -> {
                anim.start();

            });
    }

    public void finished() {
        index = 0;
        part = null;
        finished = true;
    }


    private void triggerStartEvents() {
        if (startEventMap.get(part) != null)
            startEventMap.get(part).forEach(e -> GuiEventManager.triggerQueued(e));
    }

    private void triggerFinishEvents() {
        if (eventMap.get(part) != null)
            eventMap.get(part).forEach(e -> GuiEventManager.triggerQueued(e));
    }

    public void add(ANIM_PART part, Anim anim) {
        map.put(part, anim);
        new MapMaster<ANIM_PART, Anim>().addToListMap(eventMap,
                part, anim.getEventsOnFinish());
        new MapMaster<ANIM_PART, Anim>().addToListMap(startEventMap,
                part, anim.getEventsOnStart());
    }

    public void reset() {
        map.values().forEach(anim -> {
            anim.reset();
        });
        startEventMap = new XLinkedMap<>();
        eventMap = new XLinkedMap<>();
        attached = new XLinkedMap<>();

        finished = false;
    }

    public void start() {
        index = 0;
        initPartAnim();
        if (map.isEmpty()) {
            return;
        }
        queueGraphicEvents();
        running = true;
        main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG, this + " started: "
        );

    }

    @Override
    public String toString() {

        return getClass().getSimpleName() + map;
    }

    private void queueGraphicEvents() {
        getStartEventMap().values().forEach(
                (List<GuiEventType> e) -> e.forEach(x -> GuiEventManager.queue(x)));
    }

    private void initPartAnim() {
        if (map.isEmpty())
            return;
        part = (ANIM_PART) MapMaster.get(map, index);
        currentAnim = map.get(part);
        currentAnim.start();
        triggerStartEvents();
    }

    public void addEffectAnim(CompositeAnim anim, Effect effect) {
        ANIM_PART partToAddAt = EffectAnimCreator.getPartToAttachTo(effect);

        attach(anim, partToAddAt);
        //anim group vs anim(Effects)
    }

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

    public Map<ANIM_PART, List<GuiEventType>> getStartEventMap() {
        return startEventMap;
    }

    public Map<ANIM_PART, List<GuiEventType>> getEventMap() {
        return eventMap;
    }

    public Map<ANIM_PART, List<CompositeAnim>> getAttached() {
        return attached;
    }

    public ANIM_PART getPart() {
        return part;
    }

    public int getIndex() {
        return index;
    }

    public Anim getCurrentAnim() {
        return currentAnim;
    }
}
