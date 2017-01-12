package main.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import main.ability.effects.Effect;
import main.data.XLinkedMap;
import main.entity.obj.top.DC_ActiveObj;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.system.GuiEventType;
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
    Map<ANIM_PART, GuiEventType> eventMap;
    Map<ANIM_PART, List<CompositeAnim>> attached;
    ANIM_PART part;
    int index;
    private boolean finished;
    private Anim currentAnim;

    public CompositeAnim(Anim... anims) {
        this(new MapMaster<ANIM_PART, Anim>().constructMap(new LinkedList<>(Arrays.asList(ANIM_PART.values()).subList(0, anims.length)),
         new LinkedList<>(Arrays.asList(anims))));
    }

    public CompositeAnim(Map<ANIM_PART, Anim> map) {

    }

    public CompositeAnim(DC_ActiveObj active) {

    }

    public boolean draw(Batch batch) {

        boolean result = false;
        if (currentAnim != null)
            try {
                result = currentAnim.draw(batch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (!result) {
            index++;
                triggerGraphicEvents();
            if (map.size() <= index) {
                finished();
                return false;
            }
            initPartAnim();
//            playAttached();

        }

        return true;
    }


    private void attach(CompositeAnim anim, ANIM_PART part) {
        new MapMaster<ANIM_PART, Anim>().addToListMap(attached, part, anim);
    }

    private void playAttached() {
        List<CompositeAnim> list = attached.get(part);
//        AnimMaster.add(list);

        list.forEach(anim -> {
//            anim.start();

        });
    }

    public void finished() {
        index = 0;
        part = null;
        finished = true;
    }

    private void triggerGraphicEvents() {
//        eventMap.get()
        if (part!=null )
        switch (part) {
            // remove unit, set position, set facing etc

        }
    }

    public void add(ANIM_PART part, Anim anim) {
        map.put(part, anim);

    }

    public void start() {
        if (map.isEmpty()) {
            return;
        }
        index=0;
       initPartAnim();
//        running=true;
    }

    private void initPartAnim() {
        part = (ANIM_PART) MapMaster.get(map, index);
        currentAnim=map.get(part);
        currentAnim.start();
    }

    public void addEffectAnim(CompositeAnim anim, Effect effect) {
        ANIM_PART partToAddAt = null;
        ;
        attach(anim, partToAddAt);
        //anim group vs anim(Effects)
    }

    public void interrupt() {
        //at what part?
//        setInterruptPart(p);
//        setInterruptMethod(method); //fade, flash, drop, skip
    }
}
