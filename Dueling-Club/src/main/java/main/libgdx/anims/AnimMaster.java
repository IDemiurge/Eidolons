package main.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.ability.Ability;
import main.ability.effects.Effect;
import main.entity.obj.ActiveObj;
import main.entity.obj.top.DC_ActiveObj;
import main.system.GraphicEvent;
import main.system.GuiEventManager;
import main.test.frontend.FAST_DC;

import java.util.List;
import java.util.Stack;

/**
 * Created by JustMe on 1/9/2017.
 */
public class AnimMaster extends Actor {

    Stack<CompositeAnim> leadQueue = new Stack<>(); //if more Action Stacks have been created before leadAnimation is finished
    CompositeAnim leadAnimation; // wait for it to finish before popping more from the queue
    AnimDrawer drawer;
    boolean parallelDrawing = true;
    private AnimationConstructor constructor;
    private Stage stage;
    private boolean on;

    //animations will use emitters, light, sprites, text and icons
    public AnimMaster(Stage stage) {
        on =
         FAST_DC.getGameLauncher().FAST_MODE ||  FAST_DC.getGameLauncher().SUPER_FAST_MODE;
        drawer = new AnimDrawer(stage);
        this.stage = stage;
        constructor = new AnimationConstructor();


        GuiEventManager.bind(GraphicEvent.ACTION_INTERRUPTED, p -> {
            if (!isOn()) return ;
            leadAnimation.interrupt();
        });
        GuiEventManager.bind(GraphicEvent.ACTION_BEING_RESOLVED, p -> {
            if (!isOn()) return ;
            CompositeAnim animation = constructor.getOrCreate((DC_ActiveObj) p.get());
            animation.getStartEventMap().values().forEach(
             (List<GraphicEvent> e) -> e.forEach(x -> GuiEventManager.queue(x))
            );

        });

        GuiEventManager.bind(GraphicEvent.ACTION_RESOLVES, p -> {

            if (!isOn()) return ;
            CompositeAnim animation = constructor.getOrCreate((DC_ActiveObj) p.get());

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
             (List<GraphicEvent> e) -> e.forEach(x -> GuiEventManager.queue(x))
            );

        });
//        GuiEventManager.bind(GuiEventType.ANIM_GROUP_COMPLETE, p -> {
//            leadAnimation.start();
//        });
        GuiEventManager.bind(GraphicEvent.ABILITY_RESOLVES, p -> {
            if (!isOn()) return ;
            Ability ability = (Ability) p.get();
            //what about triggers?
//            getParentAnim(ability.getRef().getActive()).addAbilityAnims(ability);
        });
        GuiEventManager.bind(GraphicEvent.EFFECT_APPLIED, p -> {
             if (!isOn()) return ;
             Effect effect = (Effect) p.get();
             CompositeAnim parentAnim = getParentAnim(effect.getRef().getActive());
             CompositeAnim anim = constructor.getEffectAnim(effect);

//                 getTrigger().getEvent()
// some active will be there anyway, so...
             //
             if (parentAnim != null)
                 parentAnim.addEffectAnim(anim, effect); //TODO
             else {
                 leadQueue.add(anim);// when to start()?
             }
         }
        );
        stage.addActor(this);
    }

    private void queueGraphicEvents() {

    }

    private CompositeAnim getParentAnim(ActiveObj active) {
        return constructor.getOrCreate(active);
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
        if (!isOn()) return ;
        if (leadAnimation == null) {
            leadAnimation = next();
        }
        if (leadAnimation == null) {
            return;
        }

        boolean result = leadAnimation.draw(batch);//drawer
        if (!result) {
            leadAnimation = next();
        }
        if (parallelDrawing) {
            leadQueue.forEach(a -> {
                a.draw(batch);

            });

            leadQueue.removeIf((CompositeAnim anim) -> anim.isFinished());
        }
    }

    public boolean isOn() {
        return on;

    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
