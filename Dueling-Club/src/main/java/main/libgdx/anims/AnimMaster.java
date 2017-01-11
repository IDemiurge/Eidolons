package main.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Stack;

/**
 * Created by JustMe on 1/9/2017.
 */
public class AnimMaster extends Actor {

    private   Stage stage;
    Stack<Anim> queue;
    Stack<Anim> runningAnims;
    AnimDrawer drawer;
    Anim leadAnimation; // wait for it to finish before popping more from the queue

//animations will use emitters, light, sprites, text and icons
    public AnimMaster(Stage stage) {
        drawer=new AnimDrawer(stage);
        this.stage=stage;
        GuiEventManager.bind(GuiEventType.ANIMATION_ADDED, p->{
            queue.add(  ((Anim) p.get()));
        });
//if (leadAnimation.isFinished()){
//
//}
        stage.addActor(this);
    }




    @Override
    public void draw(Batch batch, float parentAlpha) {

        runningAnims.forEach(anim->{
//            if (anim.isFinished())
         });

        runningAnims.forEach(anim->{
//            anim.draw(batch, );

        });
    }
}
