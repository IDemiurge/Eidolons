package eidolons.libgdx.anims.main;

import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.core.ActionInput;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.anims.*;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import eidolons.libgdx.anims.fullscreen.FullscreenAnims;
import eidolons.libgdx.anims.std.EventAnimCreator;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;
import main.ability.effects.Effect;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 1/9/2017.
  Animation does not support ZOOM!
 */
public class AnimMaster extends Group {

    static private boolean off;
    private static AnimMaster instance;
    private static Float animationSpeedFactor;

    private final FloatTextLayer floatTextLayer;
    private final EffectAnimMaster effectMaster;
    private final EventAnimMaster eventMaster;
    private final AnimDrawMaster drawer;
    private final ActionAnimMaster actionMaster;
    private final BuffAnimMaster buffMaster;
    private FullscreenAnims fullscreenAnims;

    //animations will use emitters, light, sprites, text and icons
    private AnimMaster() {
        instance = this;
        effectMaster = new EffectAnimMaster(this);
        eventMaster = new EventAnimMaster(this );
        buffMaster = new BuffAnimMaster( );
        actionMaster = new ActionAnimMaster(this );

        addActor(drawer = new AnimDrawMaster(this));
        addActor(floatTextLayer = new FloatTextLayer());

    }

    public static boolean isOn() {
        if (CoreEngine.isGraphicsOff())
            return false;
        return !off;
    }

    public static AnimMaster getInstance() {
        if (instance == null)
            instance = new AnimMaster();
        return instance;
    }

    public static boolean isSmoothStop(Anim anim) {
        if (anim.getOrigin().equals(anim.getDestination()))
            return true;
        return true;

    }

    public static boolean isAnimationOffFor(Obj sourceObj, BaseView baseView) {
        //        if (true )
        //            return false;
        if (!ExplorationMaster.isExplorationOn())
            return false;
        if (sourceObj.isMine()) {
            return false;
        }
        if (baseView != null)
            if (!baseView.isVisible())
                return true;
        if (sourceObj instanceof DC_Obj)
            if (!sourceObj.isMine())
                if (!VisionManager.checkVisible((DC_Obj) sourceObj, false))
                    return true;

        return false;
    }

    public static boolean isPreconstructEventAnims() {
        return false;
    }

    public static float getAnimationSpeedFactor() {
        if (animationSpeedFactor == null) {
            animationSpeedFactor = new Float(OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.SPEED)) / 100;
        }
        return animationSpeedFactor;
    }

    public static void setAnimationSpeedFactor(float speedFactor) {
        animationSpeedFactor = speedFactor;
    }

    public void setOff(boolean off) {
        AnimMaster.off = off;
    }


    public void bindEvents() {
        DC_SoundMaster.bindEvents();
        floatTextLayer.bindEvents();
        GuiEventManager.bind(GuiEventType.MOUSE_HOVER, p -> {
            if (!isOn()) {
                return;
            }
            if (!(p.get() instanceof Unit)) {
                return;
            }
            try {
              buffMaster.mouseHover((Unit) p.get());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        });

        GuiEventManager.bind(GuiEventType.ACTION_INTERRUPTED, p -> {
            if (!isOn()) {
                return;
            }
            drawer.interrupt();
        });
        GuiEventManager.bind(GuiEventType.ACTION_BEING_RESOLVED, p -> {
            //            CompositeAnim animation = constructor.getOrCreate((DC_ActiveObj) portrait.get());

        });

        GuiEventManager.bind(GuiEventType.PARRY, p -> {
            List args = (List) p.get();
            Ref context = (Ref) args.get(0);
            CompositeAnim animation = AnimConstructor.getParryAnim((DC_WeaponObj) args.get(1)
             , (DC_ActiveObj) args.get(2));
           drawer.add(animation);
//            if (getParallelDrawing()) {
//                animation.start(context);
//            }
        });
        GuiEventManager.bind(GuiEventType.ACTION_RESOLVES, p -> {

            if (!isOn()) {
                return;
            }
            ActionInput input = (ActionInput) p.get();
            try {
                actionMaster.initActionAnimation(input.getAction(), (AnimContext) input.getContext());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        });
        GuiEventManager.bind(GuiEventType.INGAME_EVENT_TRIGGERED, p -> {
            if (!isOn()) {
                return;
            }
            try {
                eventMaster.initEventAnimation((Event) p.get());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        });
        GuiEventManager.bind(GuiEventType.EFFECT_APPLIED, p -> {
             if (!isOn()) {
                 return;
             }
             try {
                 effectMaster.initEffectAnimation((Effect) p.get());
             } catch (Exception e) {
                 main.system.ExceptionMaster.printStackTrace(e);
             }

         }
        );


    }

    public boolean isDrawing() {
        return drawer.isDrawing();
    }

    public boolean isDrawingPlayer() {
        return drawer.isDrawingPlayer();
    }

    public FloatTextLayer getFloatTextLayer() {
        return floatTextLayer;
    }

    public EffectAnimMaster getEffectMaster() {
        return effectMaster;
    }

    public EventAnimMaster getEventMaster() {
        return eventMaster;
    }

    public AnimDrawMaster getDrawer() {
        return drawer;
    }

    public ActionAnimMaster getActionMaster() {
        return actionMaster;
    }

    public BuffAnimMaster getBuffMaster() {
        return buffMaster;
    }

    public static CompositeAnim getParentAnim(Ref ref) {
        if (ref.getActive() == null) {
            //TODO
        }
        if (ref.isTriggered()) {
            return getTriggerParentAnim(ref.getEvent());
        }
        return AnimConstructor.getCached(ref.getActive());
    }

    private static CompositeAnim getTriggerParentAnim(Event event) {
        CompositeAnim root = AnimConstructor.getCached(event.getRef().getActive());
        if (root == null) {
            main.system.auxiliary.log.LogMaster.log(LogMaster.ANIM_DEBUG, "getTriggerParentAnim failed for: " + event);
            return null;
        }
        ANIM_PART part = EventAnimCreator.getPartToAttachTo(event);
        List<Animation> list = new ArrayList<>();
        if (root.getAttached().get(part) != null) {
            list.addAll(root.getAttached().get(part));
        }
        if (root.getTimeAttached().get(part) != null) {
            list.addAll(root.getTimeAttached().get(part));
        }
        int i = 0;
        for (Animation e : list) {
            if (e instanceof CompositeAnim) {
                return (CompositeAnim) e;
            }
            CompositeAnim a = new CompositeAnim();
            a.add(
             part
             , (Anim) e);
            if (e.getDelay() == 0) {
                root.getAttached().get(part).set(i, a);
            } else {
                root.getTimeAttached().get(part).set(i, a);
            }
            return a;
        }
        return root;
    }

    public void add(CompositeAnim animation) {
        getDrawer().add(animation);
    }

    public boolean getParallelDrawing() {
        return getDrawer().getParallelDrawing();
    }

    public void setParallelDrawing(Boolean aBoolean) {
        getDrawer().setParallelDrawing(aBoolean);
    }
    public void addAttached(Animation a) {
        drawer.addAttached(a);
    }

}
