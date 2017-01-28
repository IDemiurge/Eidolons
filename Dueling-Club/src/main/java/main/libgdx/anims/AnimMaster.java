package main.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.ability.Ability;
import main.ability.effects.Effect;
import main.data.ConcurrentMap;
import main.entity.Ref;
import main.entity.obj.BuffObj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.DC_Game;
import main.libgdx.anims.std.BuffAnim;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.LogMaster;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by JustMe on 1/9/2017.
 */
public class AnimMaster extends Actor {

    static private boolean on;
    Stack<CompositeAnim> leadQueue = new Stack<>(); //if more Action Stacks have been created before leadAnimation is finished
    CompositeAnim leadAnimation; // wait for it to finish before popping more from the queue
    AnimDrawer drawer;
    boolean parallelDrawing = false;
    ConcurrentMap<BuffObj, BuffAnim> continuousAnims = new ConcurrentMap<>();
    private AnimationConstructor constructor;
    private boolean continuousAnimsOn;

    //animations will use emitters, light, sprites, text and icons
    public AnimMaster(Stage stage) {
        continuousAnimsOn =false;
//                FAST_DC.getGameLauncher().FAST_MODE ||
//                        FAST_DC.getGameLauncher().SUPER_FAST_MODE;
        on = true;
        drawer = new AnimDrawer(stage);
        constructor = new AnimationConstructor();


        GuiEventManager.bind(GuiEventType.ACTION_INTERRUPTED, p -> {
            if (!isOn()) return;
            leadAnimation.interrupt();
        });
        GuiEventManager.bind(GuiEventType.ACTION_BEING_RESOLVED, p -> {
            if (!isOn()) return;
            CompositeAnim animation = constructor.getOrCreate((DC_ActiveObj) p.get());
            animation.getStartEventMap().values().forEach(
                    (List<GuiEventType> e) -> e.forEach(x -> GuiEventManager.queue(x))
            );

        });

        GuiEventManager.bind(GuiEventType.ACTION_RESOLVES, p -> {

            if (!isOn()) return;
            CompositeAnim animation = constructor.getOrCreate((DC_ActiveObj) p.get());
            animation.reset();
            if (leadAnimation == null) {
                leadAnimation = animation;
                leadAnimation.start();
            } else {
                leadQueue.add(animation);
                if (parallelDrawing) {
                    animation.start();
                }
            }
            animation.getEventMap().values().forEach(
                    (List<GuiEventType> e) -> e.forEach(x -> GuiEventManager.queue(x))
            );

        });
//        GuiEventManager.bind(GuiEventType.ANIM_GROUP_COMPLETE, p -> {
//            leadAnimation.start();
//        });
        GuiEventManager.bind(GuiEventType.UPDATE_BUFFS, p -> {
            updateContinuousAnims();
        });
        GuiEventManager.bind(GuiEventType.ABILITY_RESOLVES, p -> {
            if (!isOn()) return;
            Ability ability = (Ability) p.get();
            //what about triggers?
//            getParentAnim(ability.getRef().getActive()).addAbilityAnims(ability);
        });
        GuiEventManager.bind(GuiEventType.EFFECT_APPLIED, p -> {
                    if (!isOn()) return;
                    Effect effect = (Effect) p.get();
                    CompositeAnim anim = constructor.getEffectAnim(effect);
                    if (anim == null) return;
                    CompositeAnim parentAnim = getParentAnim(effect.getRef());

//                 getTrigger().getEvent()
// some active will be there anyway, so...

                    if (parentAnim != null) {
                        main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG, anim + " created for: " + parentAnim);
                        parentAnim.addEffectAnim(anim, effect); //TODO}
                    } else {

                        add(anim);// when to start()?
                    }
                }
        );
        stage.addActor(this);
    }

    public static boolean isOn() {
        return on;

    }

    public void setOn(boolean on) {
        this.on = on;
    }

    private void updateContinuousAnims() {
        if (!continuousAnimsOn)
            return;
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
                    if (buff.isVisible()) {
                        BuffAnim anim = constructor.getBuffAnim(buff);
                        //TODO cache!
                        if (anim != null) {
                            continuousAnims.put(buff, anim);
                            anim.start();
                        }
                    }
            });
        });
    }

    private void add(CompositeAnim anim) {
        leadQueue.add(anim);
    }

    private void startNext() {
        leadAnimation = next();
        if (leadAnimation != null)
            leadAnimation.start();
    }

    private CompositeAnim getParentAnim(Ref ref) {
        if (ref.getActive() == null) {
            //TODO
        }
        return constructor.getOrCreate(ref.getActive());
    }

    private CompositeAnim next() {
        if (leadQueue.isEmpty()) {
            leadAnimation = null;
            return null;
        }
        leadAnimation = leadQueue.pop();
        return leadAnimation;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isOn()) return;

        continuousAnims.values().forEach(a -> {
            if (!a.getBuff().isDead())
                a.draw(batch);
        });

        if (leadAnimation == null) {
            leadAnimation = next();
        }
        if (leadAnimation == null) {
            return;
        }

        boolean result = leadAnimation.draw(batch);//drawer
        if (!result) {
            startNext();
        }
        if (parallelDrawing) {
            leadQueue.forEach(a -> {
                a.draw(batch);

            });

            leadQueue.removeIf((CompositeAnim anim) -> anim.isFinished());
        }
    }
}
