package main.libgdx.anims;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import main.ability.effects.Effect;
import main.data.XLinkedMap;
import main.entity.obj.top.DC_ActiveObj;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.phased.PhaseAnim;
import main.libgdx.anims.std.EffectAnimCreator;
import main.system.AnimEventMaster;
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
public class CompositeAnim implements Animation{

    Map<ANIM_PART, Anim> map = new XLinkedMap<>();
    Map<ANIM_PART, List<GuiEventType>> startEventMap;
    Map<ANIM_PART, List<GuiEventType>> eventMap;
    Map<ANIM_PART, List<Animation>> attached;
    Map<ANIM_PART,List<Animation>> timeAttachedAnims  ;
    ANIM_PART part;
    int index;
    PhaseAnim phaseAnim;
    private boolean finished;
    private boolean running;
    private Anim currentAnim;
    private float time = 0;

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
//            try{ //TODO attached phaseAnims?
//            DC_Game.game.getAnimationManager().getAnimations().forEach(a -> {
//                PhaseAnim phaseAnim= a.getPhaseAnim();
////                main.system.auxiliary.LogMaster.log(1,"drawing " +a + " at " + currentAnim.getPosition());
//                phaseAnim.setPosition(currentAnim.getX(),currentAnim.getY() );
//                phaseAnim.draw(batch, 1f);
//            });        }catch(Exception e){                e.printStackTrace();            }
        time += Gdx.graphics.getDeltaTime();
        boolean result = false;
        if (currentAnim != null)
            try {
                result = currentAnim.draw(batch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        checkTimeAttachedAnims();
        if (!result) {
            time = 0;
            index++;
            triggerFinishEvents();
            playAttached();
            if (map.size() <= index) {
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



    private void checkTimeAttachedAnims() {
        if (timeAttachedAnims.get(part)==null )return ;
    timeAttachedAnims.get(part).  forEach(a -> {
        if (!a.isRunning())
            if (a.getDelay() <= time) {
                a.start();
                AnimMaster.getInstance().addTimeAttached(a);
            }
        });
        timeAttachedAnims.get(part).removeIf(a-> a .isRunning());
    }


    public void attachDelayed(Animation anim, ANIM_PART part) {

        MapMaster.addToListMap(timeAttachedAnims, part, anim);
    }
    public void attach(Animation anim, ANIM_PART part) {
        MapMaster.addToListMap(attached, part, anim);
    }

    private void drawAttached(Batch batch) {
        List<Animation> list = attached.get(part);
        if (list == null) return;
        list.forEach(anim -> {
            if (!anim.isRunning())
                anim.start();
            anim.draw(batch);

        });
    }

    private void playAttached() {
        List<Animation> list = attached.get(part);
        if (list != null)
            list.forEach(anim -> {
                anim.start();

            });
    }

    public void finished() {
        index = 0;
        part = null;
        finished = true;
        running=false;
    }


    private void triggerStartEvents() {
        if (startEventMap.get(part) != null)
            startEventMap.get(part).forEach(e -> AnimEventMaster.triggerQueued(e));
    }

    private void triggerFinishEvents() {
        if (eventMap.get(part) != null)
            eventMap.get(part).forEach(e -> AnimEventMaster.triggerQueued(e));
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
        timeAttachedAnims = new XLinkedMap<>();

        finished = false;
    }

    public void start() {
        time = 0;
        index = 0;
        initPartAnim();
if (currentAnim==null )
    return ;

        currentAnim.start();
        triggerStartEvents();

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
         (List<GuiEventType> e) -> e.forEach(x -> AnimEventMaster.queue(x)));
    }

    private void initPartAnim() {
        if (map.isEmpty())
            return;
        part = (ANIM_PART) MapMaster.get(map, index);
        currentAnim = map.get(part);
    }

    public void addEffectAnim(Animation anim, Effect effect) {
        ANIM_PART partToAddAt =anim.getPart();
        if (part==null )
            partToAddAt=EffectAnimCreator.getPartToAttachTo(effect);
        float delay = EffectAnimCreator.getEffectAnimDelay(effect, anim, partToAddAt);
        if (delay!=0){
            anim.setDelay(delay);
            attachDelayed(anim, partToAddAt);
        }else
        attach(anim, partToAddAt);
        //anim group vs anim(Effects)
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
//                timeAttachedAnims.add(anim);
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

    public Map<ANIM_PART, List<GuiEventType>> getStartEventMap() {
        return startEventMap;
    }

    public Map<ANIM_PART, List<GuiEventType>> getEventMap() {
        return eventMap;
    }

    public Map<ANIM_PART, List<Animation>> getAttached() {
        return attached;
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
        if (currentAnim==null )
        initPartAnim();
        return currentAnim;
    }
    @Override
    public void setDelay(float delay) {
          getCurrentAnim().setDelay(delay);
    }

    @Override
    public float getDelay() {

        return getCurrentAnim().getDelay();
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
