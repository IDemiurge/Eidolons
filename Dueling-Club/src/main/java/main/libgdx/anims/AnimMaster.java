package main.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.ability.Ability;
import main.ability.effects.Effect;
import main.entity.obj.ActiveObj;
import main.entity.obj.top.DC_ActiveObj;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Stack;

/**
 * Created by JustMe on 1/9/2017.
 */
public class AnimMaster extends Actor {

    Stack<CompositeAnim> leadQueue = new Stack<>(); //if more Action Stacks have been created before leadAnimation is finished
    CompositeAnim leadAnimation; // wait for it to finish before popping more from the queue
    AnimDrawer drawer;
    private AnimationConstructor constructor;
    private Stage stage;

    //animations will use emitters, light, sprites, text and icons
    public AnimMaster(Stage stage) {
        drawer = new AnimDrawer(stage);
        this.stage = stage;
        constructor = new AnimationConstructor();


        GuiEventManager.bind(GuiEventType.ACTION_INTERRUPTED, p -> {
            leadAnimation.interrupt();
        });

        GuiEventManager.bind(GuiEventType.ACTION_RESOLVES, p -> {
            if (leadAnimation == null) {
                leadAnimation =
                 constructor.construct((DC_ActiveObj) p.get());
                leadAnimation.start();
            } else
                leadQueue.add(leadAnimation);
        });
//        GuiEventManager.bind(GuiEventType.ANIM_GROUP_COMPLETE, p -> {
//            leadAnimation.start();
//        });
        GuiEventManager.bind(GuiEventType.ABILITY_RESOLVES, p -> {
            Ability ability = (Ability) p.get();
            //what about triggers?
//            getParentAnim(ability.getRef().getActive()).addAbilityAnims(ability);
        });
        GuiEventManager.bind(GuiEventType.EFFECT_APPLIED, p -> {
             Effect effect = (Effect) p.get();
             CompositeAnim anim = getParentAnim(effect.getRef().getActive());
             if (anim != null)
                 anim.addEffectAnim(null, effect); //TODO
             else {


             }
         }
        );
        stage.addActor(this);
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
    }

}
