package main.libgdx.anims.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 1/8/2017.
 */
public class ParticleManager extends Actor {
    public boolean debugMode;
    EmitterMap emitterMap;
    private Stage effects;


    public ParticleManager(Stage effects) {
        this.effects = effects;
        emitterMap = new EmitterMap();
//        GuiEventManager.bind(GraphicEvent.GRID_CREATED, p -> {
//         });
        GuiEventManager.bind(GuiEventType.UPDATE_EMITTERS, p -> {
            emitterMap.update();
            updateEmitters();
//
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {


        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {

        super.act(delta);
    }

    private void updateEmitters() {
//        for (ParticleInterface actor : emitterMap.getEmitters()) {
//            if (!emitterMap.contains(actor))
//                actor.remove();
//            else if (effects.getActors().contains(actor, true))
//                effects.addActor(actor);
//        }
    }

}
