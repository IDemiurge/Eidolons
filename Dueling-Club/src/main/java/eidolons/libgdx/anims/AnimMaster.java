package eidolons.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.core.ActionInput;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.anims.AnimationConstructor.ANIM_PART;
import eidolons.libgdx.anims.controls.AnimController;
import eidolons.libgdx.anims.std.BuffAnim;
import eidolons.libgdx.anims.std.EventAnimCreator;
import eidolons.libgdx.anims.text.FloatingText;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;
import main.ability.effects.Effect;
import main.data.ConcurrentMap;
import main.entity.Ref;
import main.entity.obj.BuffObj;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.EventCallback;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 1/9/2017.
 */
public class AnimMaster extends Group {
// Animation does not support ZOOM!

    static private boolean off;
    private static AnimMaster instance;
    //    private   SpriteCache spriteCache;
    DequeImpl<CompositeAnim> leadQueue = new DequeImpl<>(); //if more Action Stacks have been created before leadAnimation is finished
    CompositeAnim leadAnimation; // wait for it to finish before popping more from the queue

    ConcurrentMap<BuffObj, BuffAnim> continuousAnims = new ConcurrentMap<>();
    DequeImpl<Animation> attachedAnims = new DequeImpl<>();
    private AnimController controller;
    private FloatingTextMaster floatingTextMaster;
    private AnimationConstructor constructor;
    private boolean continuousAnimsOn;
    private Integer showBuffAnimsOnNewRoundLength = 2;
    private Integer showBuffAnimsOnHoverLength = 3; //continuous
    private boolean drawing;
    private boolean drawingPlayer;
    private Boolean parallelDrawing;
    private Float animationSpeedFactor;

    //animations will use emitters, light, sprites, text and icons
    private AnimMaster() {
        instance = this;
//        spriteCache = new SpriteCache();
//        spriteCache.add();
        floatingTextMaster = new FloatingTextMaster();
        continuousAnimsOn =
         false;
//         FAST_DC.getGameLauncher().FAST_MODE ||
//          FAST_DC.getGameLauncher().SUPER_FAST_MODE;

        constructor = new AnimationConstructor();
        controller = new AnimController();
//        AnimMaster3d.init();
//        bindEvents(); now in GridPanel.bindEvents()

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
//        if (anim.getDestination().equals(anim.getOrigin()))
//        return true;
        return false;

    }

    public static boolean isAnimationOffFor(Obj sourceObj, BaseView baseView) {
//        if (true )
//            return false;
        if (!ExplorationMaster.isExplorationOn())
            return false;
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

    public void setOff(boolean off) {
        AnimMaster.off = off;
    }

    public Boolean getParallelDrawing() {
//        if (parallelDrawing == null)
//            parallelDrawing = OptionsMaster.getAnimOptions().getBooleanValue(ANIMATION_OPTION.PARALLEL_DRAWING);
//        return parallelDrawing;
        return true;
    }

    public void setParallelDrawing(Boolean parallelDrawing) {
        this.parallelDrawing = parallelDrawing;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public void bindEvents() {
        DC_SoundMaster.bindEvents();
        GuiEventManager.bind(GuiEventType.ADD_FLOATING_TEXT, p -> {
            FloatingText floatingText = (FloatingText) p.get();
//            if (!floatingText.isInitialized())

            floatingText.init();
            addActor(floatingText);

        });
        GuiEventManager.bind(GuiEventType.MOUSE_HOVER, p -> {
            if (!isOn()) {
                return;
            }
            if (!(p.get() instanceof Unit)) {
                return;
            }
            if (showBuffAnimsOnHoverLength == null) {
                return;
            }
            try {
                mouseHover((Unit) p.get());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        });

        GuiEventManager.bind(GuiEventType.ACTION_INTERRUPTED, p -> {
            if (!isOn()) {
                return;
            }
            leadAnimation.interrupt();
        });
        GuiEventManager.bind(GuiEventType.ACTION_BEING_RESOLVED, p -> {
//            CompositeAnim animation = constructor.getOrCreate((DC_ActiveObj) portrait.get());

        });

        GuiEventManager.bind(GuiEventType.ACTION_RESOLVES, p -> {

            if (!isOn()) {
                return;
            }
            ActionInput input = (ActionInput) p.get();
            try {
                initActionAnimation(input.getAction(), (AnimContext) input.getContext());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        });
//        GuiEventManager.bind(GuiEventType.UPDATE_BUFFS, p -> {
//            updateContinuousAnims();
//        });
//        GuiEventManager.bind(GuiEventType.ABILITY_RESOLVES, p -> {
//            if (!isOn()) {
//                return;
//            }
//            Ability ability = (Ability) p.get();
        //what about triggers?
//            getParentAnim(ability.getRef().getActive()).addAbilityAnims(ability);
//        });
        GuiEventManager.bind(GuiEventType.INGAME_EVENT_TRIGGERED, p -> {
            if (!isOn()) {
                return;
            }
            try {
                initEventAnimation((Event) p.get());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        });
        GuiEventManager.bind(GuiEventType.EFFECT_APPLIED, p -> {
             if (!isOn()) {
                 return;
             }
             try {
                 initEffectAnimation((Effect) p.get());
             } catch (Exception e) {
                 main.system.ExceptionMaster.printStackTrace(e);
             }

         }
        );


    }

    private void mouseHover(Unit unit) {
        unit.getBuffs().forEach(buff -> {
            BuffAnim anim = continuousAnims.get(buff);
            if (anim != null) {
                anim.reset();
                anim.start();
                anim.setDuration(showBuffAnimsOnHoverLength);
            }
        });
    }

    private void initActionAnimation(DC_ActiveObj activeObj, AnimContext context) {
        if (isAnimationOffFor(activeObj.getOwnerObj(), null)) {
            return;
        }
        boolean attachToNext = context.isAttachToNext();
        CompositeAnim animation = constructor.getOrCreate(activeObj);
        if (animation == null) {
            LogMaster.log(LogMaster.ANIM_DEBUG, "NULL ANIM FOR " + activeObj);
            return;
        }
        if (animation.isEventAnim())
            return;
        if (animation.isRunning())
            return;

//        animation.reset();
        animation.setWaitingForNext(attachToNext);
//        if (leadAnimation == null && !attachToNext) {
//            leadAnimation = animation;
//            leadAnimation.start(context);
//        } else {
        animation.setRef(context);
        add(animation);
        if (getParallelDrawing()) {
            animation.start(context);
        }
//            controller.store(animation);
//        }
    }

    private void initEventAnimation(Event event) {
        if (event.getRef().isDebug()) {
            return;
        }

        if (event.getType() == STANDARD_EVENT_TYPE.NEW_ROUND) {
            if (showBuffAnimsOnNewRoundLength != null) {
                continuousAnims.values().forEach(anim -> {
                    if (anim.getBuff().isVisible()) {
                        anim.reset();
                        anim.start();
                        anim.setDuration(showBuffAnimsOnNewRoundLength);
                    }
                });
            }
        }
        CompositeAnim parentAnim = null;
        if (floatingTextMaster.isEventDisplayable(event)) {
            parentAnim = getParentAnim(event.getRef());
            if (parentAnim != null) {
                parentAnim.addTextEvent(event);
            }
        }
        Anim anim = EventAnimCreator.getAnim(event);
        if (anim == null) {
            return;
        }
        parentAnim = getEventAttachAnim(event, anim);
        if (parentAnim != null) {

            LogMaster.log(LogMaster.ANIM_DEBUG, anim +
             " event anim created for: " + parentAnim);

            if (parentAnim.getMap().isEmpty())
                parentAnim.add(ANIM_PART.AFTEREFFECT, anim);
            else
                parentAnim.addEventAnim(anim, event); //TODO}
        }
        if (parentAnim.getMap().isEmpty()) {

        }
        if (parentAnim != leadAnimation)
            if (!parentAnim.isFinished())
                if (!parentAnim.isRunning()) {// preCheck new TODO
                    add(parentAnim);
                }
        parentAnim.setRef(event.getRef());
    }

    private CompositeAnim getEventAttachAnim(Event event, Anim anim) {
        DC_ActiveObj active = (DC_ActiveObj) event.getRef().getActive();
        if (active != null) {
//            if (event.getType() instanceof STANDARD_EVENT_TYPE) {
////                switch (((STANDARD_EVENT_TYPE) event.getType())) {
//                if (event.getType() == DeathAnim.EVENT_TYPE) {
//                    if (active.getRef().getTargetObj() != active.getOwnerObj())
//                    if (active.getChecker().isPotentiallyHostile()) {
            return getParentAnim(active.getRef());
//                    }
//                }
//            }
//
        }
//        if (leadAnimation!=null )
//            if (leadAnimation.getActive()!=active)
//        return leadAnimation;

        return new CompositeAnim();
    }

    private void initEffectAnimation(Effect effect) {
        Animation anim = constructor.getEffectAnim(effect);
        if (anim == null) {
            return;
        }
        CompositeAnim parentAnim = getParentAnim(effect.getRef());
        if (parentAnim != null) {
            LogMaster.log(LogMaster.ANIM_DEBUG, anim + " created for: " + parentAnim);
            parentAnim.addEffectAnim(anim, effect); //TODO}
        } else {
//                        add(anim);// when to start()?
        }
    }

    private void updateContinuousAnims() {
        if (!continuousAnimsOn) {
            return;
        }
        final List<BuffObj> toRemove = new ArrayList<>();
        continuousAnims.keySet().forEach(buff -> {
            if (buff.isDead()) {
                continuousAnims.get(buff).finished();
                toRemove.add(buff);
            }
        });
        toRemove.forEach(buff -> {
            continuousAnims.remove(buff);
        });
        DC_Game.game.getUnits().forEach(unit -> {
            unit.getBuffs().forEach(buff -> {
                if (!continuousAnims.containsKey(buff)) //TODO or full reset always?
                {
                    if (buff.isVisible()) {
                        BuffAnim anim = constructor.getBuffAnim(buff);
                        //TODO cache!
                        if (anim != null) {
                            continuousAnims.put(buff, anim);
                            try {
                                anim.start();
                            } catch (Exception e) {
                                main.system.ExceptionMaster.printStackTrace(e);
                            }
                        }
                    }
                }
            });
        });
    }

    private void add(CompositeAnim anim) {
        main.system.auxiliary.log.LogMaster.log(1, "ANIMATION ADDED   " + anim);
        if (anim == leadAnimation) {
            return;
        }
        if (leadQueue.contains(anim)) {
            return;
        }
        CompositeAnim firstAnim = leadQueue.peekFirst();
        if (firstAnim == null)
            leadQueue.add(anim);
        else if (firstAnim.isWaitingForNext()) {
            firstAnim.setWaitingForNext(false);
            leadQueue.addFirst(anim);
        } else
            leadQueue.add(anim);
    }

    public void addAttached(Animation anim) {
        attachedAnims.add(anim);
    }

    private void startNext() {
        leadAnimation = next();
        if (leadAnimation != null) {
            leadAnimation.start();
        }
    }

    public CompositeAnim getParentAnim(Ref ref) {
        if (ref.getActive() == null) {
            //TODO
        }
        if (ref.isTriggered()) {
            return getTriggerParentAnim(ref.getEvent());
        }
        return constructor.getOrCreate(ref.getActive());
    }

    private CompositeAnim getTriggerParentAnim(Event event) {
        CompositeAnim root = constructor.getOrCreate(event.getRef().getActive());
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

    private CompositeAnim next() {
        if (leadQueue.isEmpty()) {
            leadAnimation = null;
            if (drawing) {
                drawing = false;
                main.system.auxiliary.log.LogMaster.log(1, "ANIMATION_QUEUE_FINISHED");
                WaitMaster.receiveInput(WAIT_OPERATIONS.ANIMATION_QUEUE_FINISHED, true);
//                GuiEventManager.trigger(GuiEventType.ANIMATION_QUEUE_FINISHED);
            }
            drawingPlayer = false;
            return null;
        }

        //TODO Stack: counter atk will animated first - last in first out :(
        CompositeAnim firstAnim = leadQueue.peekFirst();
        if (firstAnim != null)
            if (firstAnim.isWaitingForNext()) {
                return null;
            }
        leadAnimation = leadQueue.removeFirst();

        main.system.auxiliary.log.LogMaster.log(1, "next animation: " + leadAnimation +
         "; " +
         leadQueue.size() +
         " in Queue= " + leadQueue);
//        leadAnimation.resetRef();
        return leadAnimation;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawAnims(batch, parentAlpha);
    }

    public void drawAnims(Batch batch, float parentAlpha) {
        if (!isOn()) {
            return;
        }
        continuousAnims.values().forEach(a -> {
            if (!a.getBuff().isDead()) {
                if (a.isRunning()) {
                    a.draw(batch);
                }
            }
        });
        attachedAnims.removeIf((Animation a) -> !a.isRunning());
        attachedAnims.forEach(a -> {
            a.tryDraw(batch);
        });

        if (leadAnimation == null) {
            startNext();
        }

        if (leadAnimation != null) {
            drawing = true;
            boolean result = tryDrawAnimation(batch, leadAnimation);

            if (!result) {
                startNext();
            }
        }
        // not turned on
        if (getParallelDrawing()) {
            leadQueue.forEach(a -> {
                tryDrawAnimation(batch, a);
            });

            leadQueue.removeIf((CompositeAnim anim) -> anim.isFinished());
        }
// ???
        if (getChildren().size > 0) {
            super.draw(batch, parentAlpha);
        }
    }

    private boolean tryDrawAnimation(Batch batch, CompositeAnim anim) {
        boolean result = false;
        if (ExplorationMaster.isExplorationOn())
            if (anim.getActive_() != null)
                if (anim.getActive_().getOwnerObj().isMine())
                    if (anim.getActive_().getOwnerObj().isMainHero())
                        drawingPlayer = true;

        try {
            result = anim.tryDraw(batch);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        if (ExplorationMaster.isExplorationOn())
            if (anim.getActive_() != null)
                if (anim.getActive_().getOwnerObj().isMine())
                    if (anim.getActive_().getOwnerObj().isMainHero())
                        drawingPlayer = result;

        return result;
    }

    public AnimationConstructor getConstructor() {
        return constructor;
    }

    public void onDone(Event event, EventCallback callback, EventCallbackParam param) {
        getParentAnim(event.getRef()).onDone(callback, param);
    }

    public boolean isDrawingPlayer() {
        return drawingPlayer;
    }

    public void setDrawingPlayer(boolean drawingPlayer) {
        this.drawingPlayer = drawingPlayer;
    }

    public float getAnimationSpeedFactor() {
        if (animationSpeedFactor == null) {
            animationSpeedFactor = new Float(OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.SPEED)) / 100;
        }
        return animationSpeedFactor;
    }

    public void setAnimationSpeedFactor(float animationSpeedFactor) {
        this.animationSpeedFactor = animationSpeedFactor;
    }

    public void cleanUp() {
        if (leadAnimation != null)
            leadAnimation.finished();
        leadQueue.forEach(a -> {
            a.finished();
        });
        leadAnimation = null;
        leadQueue.clear();
    }
}
