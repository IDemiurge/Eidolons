package main.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.ability.Ability;
import main.ability.effects.Effect;
import main.data.ConcurrentMap;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.BuffObj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.controls.AnimController;
import main.libgdx.anims.std.BuffAnim;
import main.libgdx.anims.std.EventAnimCreator;
import main.libgdx.anims.text.FloatingTextMaster;
import main.system.EventCallback;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by JustMe on 1/9/2017.
 */
public class AnimMaster extends Group {
// Animation does not support ZOOM!

    static private boolean on;
    private static AnimMaster instance;
    Stack<CompositeAnim> leadQueue = new Stack<>(); //if more Action Stacks have been created before leadAnimation is finished
    CompositeAnim leadAnimation; // wait for it to finish before popping more from the queue
    boolean parallelDrawing = false;
    ConcurrentMap<BuffObj, BuffAnim> continuousAnims = new ConcurrentMap<>();
    DequeImpl<Animation> attachedAnims = new DequeImpl<>();
    private AnimController controller;
    private FloatingTextMaster floatingTextMaster;
    private AnimationConstructor constructor;
    private boolean continuousAnimsOn;
    private Integer showBuffAnimsOnNewRoundLength = 2;
    private Integer showBuffAnimsOnHoverLength = 3; //continuous
    private boolean drawing;

    //animations will use emitters, light, sprites, text and icons
    public AnimMaster() {
        instance = this;
        floatingTextMaster = new FloatingTextMaster();
        continuousAnimsOn =
                false;
//         FAST_DC.getGameLauncher().FAST_MODE ||
//          FAST_DC.getGameLauncher().SUPER_FAST_MODE;
        on = true;
        constructor = new AnimationConstructor();
        controller = new AnimController();
        bindEvents();

    }

    public static boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public static AnimMaster getInstance() {
        return instance;
    }

    public static boolean isSmoothStop(Anim anim) {
//        if (anim.getDestination().equals(anim.getOrigin()))
//        return true;
        return false;

    }

    public boolean isDrawing() {
        return drawing;
    }

    private void bindEvents() {

        GuiEventManager.bind(GuiEventType.MOUSE_HOVER, p -> {
            if (!(p.get() instanceof Unit)) {
                return;
            }
            if (showBuffAnimsOnHoverLength == null) {
                return;
            }
            Unit unit = (Unit) p.get();
            unit.getBuffs().forEach(buff -> {
                BuffAnim anim = continuousAnims.get(buff);
                if (anim != null) {
                    anim.reset();
                    anim.start();
                    anim.setDuration(showBuffAnimsOnHoverLength);
                }
            });
        });

        GuiEventManager.bind(GuiEventType.ACTION_INTERRUPTED, p -> {
            if (!isOn()) {
                return;
            }
            leadAnimation.interrupt();
        });
        GuiEventManager.bind(GuiEventType.ACTION_BEING_RESOLVED, p -> {
//            CompositeAnim animation = constructor.getOrCreate((DC_ActiveObj) p.get());

        });

        GuiEventManager.bind(GuiEventType.ACTION_RESOLVES, p -> {

            if (!isOn()) {
                return;
            }

            CompositeAnim animation = constructor.getOrCreate((DC_ActiveObj) p.get());
            if (animation == null) {
                LogMaster.log(LogMaster.ANIM_DEBUG, "NULL ANIM FOR " + p.get());
                return;
            }
            animation.reset();
            if (leadAnimation == null) {
                leadAnimation = animation;
                leadAnimation.start();
            } else {
                leadQueue.add(animation);
                if (parallelDrawing) {
                    animation.start();
                }
                controller.store(animation);
            }
        });
        GuiEventManager.bind(GuiEventType.UPDATE_BUFFS, p -> {
            updateContinuousAnims();
        });
        GuiEventManager.bind(GuiEventType.ABILITY_RESOLVES, p -> {
            if (!isOn()) {
                return;
            }
            Ability ability = (Ability) p.get();
            //what about triggers?
//            getParentAnim(ability.getRef().getActive()).addAbilityAnims(ability);
        });
        GuiEventManager.bind(GuiEventType.INGAME_EVENT_TRIGGERED, p -> {
            if (!isOn()) {
                return;
            }
            Event event = (Event) p.get();
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
            parentAnim = getParentAnim(event.getRef());
            if (parentAnim != null) {
                LogMaster.log(LogMaster.ANIM_DEBUG, anim +
                        " event anim created for: " + parentAnim);
                parentAnim.addEventAnim(anim, event); //TODO}
            }
            if (!parentAnim.isRunning()) {// preCheck new TODO
                add(parentAnim);
            }
        });
        GuiEventManager.bind(GuiEventType.EFFECT_APPLIED, p -> {
                    if (!isOn()) {
                        return;
                    }
                    Effect effect = (Effect) p.get();
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
        );
    }

    private void updateContinuousAnims() {
        if (!continuousAnimsOn) {
            return;
        }
        final List<BuffObj> toRemove = new LinkedList<>();
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
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        });
    }

    private void add(CompositeAnim anim) {
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
        List<Animation> list = new LinkedList<>();
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
                WaitMaster.receiveInput(WAIT_OPERATIONS.ANIMATION_QUEUE_FINISHED, true);
            }
            return null;
        }
        leadAnimation = leadQueue.pop();
        return leadAnimation;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
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
            a.draw(batch);
        });

        if (leadAnimation == null) {
            leadAnimation = next();
        }
        if (leadAnimation == null) {
            return;
        }

        boolean result = false;
        drawing = true;
        try {
            result = leadAnimation.draw(batch);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!result) {
            startNext();
        }
        // not turned on
        if (parallelDrawing) {
            leadQueue.forEach(a -> {
                a.draw(batch);

            });

            leadQueue.removeIf((CompositeAnim anim) -> anim.isFinished());
        }
// ???
        if (getChildren().size > 0) {
            super.draw(batch, parentAlpha);
        }
    }

    public AnimationConstructor getConstructor() {
        return constructor;
    }

    public void onDone(Event event, EventCallback callback, EventCallbackParam param) {
        getParentAnim(event.getRef()).onDone(callback, param);
    }
}
