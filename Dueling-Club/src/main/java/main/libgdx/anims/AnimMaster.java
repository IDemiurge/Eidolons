package main.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.Stack;

/**
 * Created by JustMe on 1/9/2017.
 */
public class AnimMaster extends Actor {

    private   Stage stage;
    Stack<CompositeAnim>  leadQueue; //if more Action Stacks have been created before leadAnimation is finished
    CompositeAnim leadAnimation; // wait for it to finish before popping more from the queue

    AnimDrawer drawer;
//animations will use emitters, light, sprites, text and icons
    public AnimMaster(Stage stage) {
        drawer=new AnimDrawer(stage);
        this.stage=stage;


//        GuiEventManager.bind(GuiEventType.ACTION_INTERRUPTED, p->{
//leadAnimation.interrupt();
//         });
//            GuiEventManager.bind(GuiEventType.ANIM_CAST, p->{
//            CompositeAnim composite =AnimationConstructor. map.get(p.get());
//             composite.get(ANIM_PART.CAST);
//
//         });
//
//        GuiEventManager.bind(GuiEventType.EFFECT_APPLIED, p->{
//            Effect effect = (Effect) p.get();
//          anim =   getRootAnim(effect.getRef().getActive());
//          if (anim!=null )anim.addEffectAnim(effect);
//          else {
//
//              new EffectAnim(effect);
//              //anim group vs anim(Effects)
//          }
//         }
//    );
//        GuiEventManager.bind(GuiEventType.ACTION_RESOLVES, p->{
//            leadAnimation = new ActionAnim((Entity) p.get());
//            leadQueue.add(leadAnimation);
//        });
//        GuiEventManager.bind(GuiEventType.ANIM_GROUP_COMPLETE, p->{
//            map.get((Entity) p.get());
//
//            leadQueue.add(leadAnimation);
//        });
//            GuiEventManager.bind(GuiEventType.ABILITY_RESOLVES, p->{
//           Ability ability = (Ability) p.get();
//           //what about triggers?
//           getRootAnim(ability.getRef().getActive()).addAbilityAnims(ability);
//           //or maybe on effect?
//            // default binding would be useful
////            queue.add(  ((Anim) p.get()));
//        });

//        stage.addActor(this);
    }




        private CompositeAnim loadNext() {
    //       leadAnimation= leadQueue.pop();
            return null;
        }


    @Override
    public void draw(Batch batch, float parentAlpha) {

        boolean result = leadAnimation.draw(batch);//drawer
        if (!result)
    leadAnimation=loadNext();
    }

}
