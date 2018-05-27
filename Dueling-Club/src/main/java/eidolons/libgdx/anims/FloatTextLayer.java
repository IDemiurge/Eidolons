package eidolons.libgdx.anims;

import eidolons.libgdx.anims.text.FloatingText;
import eidolons.libgdx.gui.generic.GroupX;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;

import static eidolons.libgdx.anims.text.FloatingTextMaster.DEFAULT_DISPLACEMENT_X;
import static eidolons.libgdx.anims.text.FloatingTextMaster.displacementInvertFlag;

/**
 * Created by JustMe on 5/27/2018.
 */
public class FloatTextLayer extends GroupX {
    DequeImpl<FloatingText> queued = new DequeImpl<>();
    DequeImpl<FloatingText> displaying = new DequeImpl<>();

    public FloatTextLayer() {
        bindEvents();
    }

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.ADD_FLOATING_TEXT, p -> {
            FloatingText floatingText = (FloatingText) p.get();
            floatingText.init();
            addToQueue(floatingText);
        });
    }

    private void addToQueue(FloatingText floatingText) {
        queued.add(floatingText);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (FloatingText sub : queued) {
            show(sub);
        }
        for (FloatingText sub :     new ArrayList<>(displaying) ) {
            if (sub.getColor().a<=0)
                displaying.remove(sub);
        }

    }

    private void show(FloatingText text) {
        displaying.add(text);
        float offsetX=0;
        float offsetY=0;
        offsetX=
         displacementInvertFlag
          ? - DEFAULT_DISPLACEMENT_X
          : DEFAULT_DISPLACEMENT_X;

//        for (FloatingText text : displaying) {
//            float dst = new Vector2(text.getX(), text.getY())
//             .dst(new Vector2(sub.getX(), sub.getY()));
//
//            if (dst<50){
//                  offsetX=0;
//                  offsetY=0;
//            }
//        }
        text.setX(text.getX()+offsetX);
        text.setY(text.getY()+offsetY);
        queued.remove(text);
        float mod = 0.7f + displaying.size() * 0.1f;
        text.setDuration(text.getDuration()*mod);
        addActor(text);
    }
}
















