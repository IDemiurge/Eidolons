package libgdx.anims;

import eidolons.content.consts.VisualEnums;
import eidolons.game.core.Eidolons;
import libgdx.anims.text.FloatingText;
import libgdx.gui.generic.GroupX;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;
import libgdx.anims.text.FloatingTextMaster;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/27/2018.
 */
public class FloatingTextLayer extends GroupX {
    DequeImpl<FloatingText> queued = new DequeImpl<>();
    DequeImpl<FloatingText> displaying = new DequeImpl<>();
    private static Float durationMod;

    public FloatingTextLayer() {
    }

    public void bindEvents() {
        GuiEventManager.bind(GuiEventType.CREATE_FLOATING_TEXT, p -> {
            List list = (List) p.get();
            VisualEnums.TEXT_CASES aCASE= (VisualEnums.TEXT_CASES) list.get(0);
            String text=list.get(1).toString();
            Entity entity= (Entity) list.get(2);
            FloatingTextMaster.getInstance().createFloatingText(aCASE, text, entity);
        });
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
        for (FloatingText sub : new ArrayList<>(displaying)) {
            if (sub.getActions().size <= 0 || sub.getColor().a <= 0 || sub.getParent() == null || !sub.isVisible())
                displaying.remove(sub);
        }

    }

    private void show(FloatingText text) {
        float offsetX;
        float offsetY = 0;
        offsetX =
                FloatingTextMaster.displacementInvertFlag
                        ? -FloatingTextMaster.DEFAULT_DISPLACEMENT_X
                        : FloatingTextMaster.DEFAULT_DISPLACEMENT_X;

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

        text.added();

    }

    public static Float getDurationMod() {
        if (durationMod == null) {
            durationMod = (float) OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.FLOAT_TEXT_DURATION_MOD) / 100;
        }
        return durationMod;
    }

    public static void setDurationMod(Float mod) {
        durationMod = mod;
    }
}
















