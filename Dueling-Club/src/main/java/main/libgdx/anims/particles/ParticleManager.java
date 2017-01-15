package main.libgdx.anims.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.system.GuiEventManager;
import main.system.GraphicEvent;

/**
 * Created by JustMe on 1/8/2017.
 */
public class ParticleManager extends Actor{
    public   boolean debugMode;
    private Stage effects;
    EmitterMap emitterMap;


    public ParticleManager(Stage effects) {
        this.effects = effects;
        emitterMap= new EmitterMap();
        GuiEventManager.bind(GraphicEvent.GRID_CREATED, p -> {
         });
        GuiEventManager.bind(GraphicEvent.UPDATE_EMITTERS, p -> {
            emitterMap.update();
            updateEmitters();
//
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ParticleEffect particleEffect;
        emitterMap.updateAnimFx();
            for (ParticleActor actor : emitterMap.getEmitters()) {
            particleEffect=    actor.getEffect();
            particleEffect.update(parentAlpha);
            particleEffect.draw(batch, parentAlpha);

            if (particleEffect.isComplete()) {
            if (actor.isContinuous())
                particleEffect.reset();
            else particleEffect.dispose();
            }
        }


        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        for (ParticleActor actor : emitterMap.getEmitters()) {

        }

            super.act(delta);
    }

    private void updateEmitters() {
//        for (ParticleActor actor : emitterMap.getEmitters()) {
//            if (!emitterMap.contains(actor))
//                actor.remove();
//            else if (effects.getActors().contains(actor, true))
//                effects.addActor(actor);
//        }
    }

}
