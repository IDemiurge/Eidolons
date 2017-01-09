package main.libgdx.anims.particles;

import com.badlogic.gdx.scenes.scene2d.Stage;
import main.libgdx.anims.particles.lighting.LightMap;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;

/**
 * Created by JustMe on 1/8/2017.
 */
public class ParticleManager {
    public   boolean debugMode;
    private Stage effects;
    LightMap lightMap;
    EmitterMap emitterMap;
    List<ParticleActor> actors;

    public ParticleManager(Stage effects) {
        this.effects = effects;

        GuiEventManager.bind(GuiEventType.GRID_CREATED, p -> {
            //TODO init emitterMap and lightMap
         });
        GuiEventManager.bind(GuiEventType.UPDATE_EMITTERS, p -> {
//            lightMap.updateMap();
//            emitterMap.update();
//            updateEmitters();
//
        });
    }

    private void updateEmitters() {
        for (ParticleActor actor : actors) {
            if (!emitterMap.contains(actor))
                actor.remove();
            else if (effects.getActors().contains(actor, true))
                effects.addActor(actor);
        }
    }

}
