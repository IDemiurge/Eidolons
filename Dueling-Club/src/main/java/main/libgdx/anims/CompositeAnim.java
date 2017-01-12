package main.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
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

    Map<ANIM_PART, Anim> map;
    Map<ANIM_PART, GuiEventType> eventMap;
    Map<ANIM_PART, List<CompositeAnim>> attached;
    ANIM_PART part;
    int index;
    private boolean finished;

    public CompositeAnim(Anim... anims) {
        this(new MapMaster<ANIM_PART, Anim>().constructMap(new LinkedList<>(Arrays.asList(ANIM_PART.values()).subList(0, anims.length)),
         new LinkedList<>(Arrays.asList(anims))));
    }

    public CompositeAnim(Map<ANIM_PART, Anim> map) {

    }

    public CompositeAnim(DC_ActiveObj active) {

    }

    public boolean draw(Batch batch) {

        boolean result = map.get(part).draw(batch);
        if (!result) {
            index++;
            triggerGraphicEvents();
            if (map.size() >= index) {
                finished();
                return false;
            }
            part = (ANIM_PART) map.keySet().toArray()[index];
            playAttached();

        }

        return true;
    }

    private void playAttached() {
        List<CompositeAnim> list = attached.get(part);
//        AnimMaster.add(list);

        list.forEach(anim->{
//            anim.start();

        });
    }

    public void finished() {
        index = 0;
        part = null;
        finished = true;
    }

    private void triggerGraphicEvents() {
        switch (part){
            // remove unit, set position, set facing etc
        }
    }



    public void add(ANIM_PART part, Anim anim) {
        new MapMaster<ANIM_PART, Anim>().addToListMap(attached, part, anim);

    }
}
