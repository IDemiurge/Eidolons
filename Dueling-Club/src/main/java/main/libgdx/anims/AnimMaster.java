package main.libgdx.anims;

import com.badlogic.gdx.scenes.scene2d.Stage;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Stack;

/**
 * Created by JustMe on 1/9/2017.
 */
public class AnimMaster {

    private   Stage stage;
    Stack<AnimActor> queue;
    Stack<AnimActor> runningAnims;
//animations will use emitters, light, sprites, text and icons
    public AnimMaster(Stage stage) {
//        ParticleEffectPool pool= new ParticleEffectPool();
        this.stage=stage;
        GuiEventManager.bind(GuiEventType.ANIMATION_ADDED, p->{
            queue.add( new AnimActor((Animation) p.get()));
        });
    }

    public void   finished(AnimActor animActor){
        animActor.remove();

    }
        public void   run(AnimActor animActor){
//        anim = get(0);

stage.addActor(animActor);


    }
}
