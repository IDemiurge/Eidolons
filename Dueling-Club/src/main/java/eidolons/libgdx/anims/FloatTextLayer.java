package eidolons.libgdx.anims;

import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.text.FloatingText;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;
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
    private static Float durationMod;

    public FloatTextLayer() {
        bindEvents();
    }

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.ADD_FLOATING_TEXT, p -> {
            FloatingText floatingText = (FloatingText) p.get();
            if (queued.contains(floatingText))
                return;
            if (displaying.contains(floatingText))
                return;
            floatingText.setDuration(floatingText.getDuration() * getDurationMod());
            floatingText.init();
            addToQueue(floatingText);
        });
    }

    private void addToQueue(FloatingText floatingText) {
        queued.add(floatingText);
    }

    @Override
    public void act(float delta) {
        if (Eidolons.getGame().isPaused())
            return;
        super.act(delta);
        for (FloatingText sub : new ArrayList<>(queued)) {
            show(sub);
        }
        for (FloatingText sub :     new ArrayList<>(displaying) ) {
            if (sub.getActions().size<=0 ||sub.getColor().a<=0 || sub.getParent()==null || !sub.isVisible())
                displaying.remove(sub);
        }

    }

    private void show(FloatingText text) {
        float offsetX = 0;
        float offsetY = 0;
        offsetX =
         displacementInvertFlag
          ? -DEFAULT_DISPLACEMENT_X
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
        text.setX(text.getX() + offsetX);
        text.setY(text.getY() + offsetY);
//        float mod = 0.7f + displaying.size() * 0.05f;
        addActor(text);
        queued.remove(text);
        displaying.add(text);

    }

    public static Float getDurationMod() {
        if (durationMod == null) {
            durationMod=new Float(
             OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.FLOAT_TEXT_DURATION_MOD))/100;
        }
        return durationMod;
    }

    public static void setDurationMod(Float mod) {
        durationMod = mod;
    }
}
















