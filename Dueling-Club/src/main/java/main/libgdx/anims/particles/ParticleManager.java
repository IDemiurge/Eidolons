package main.libgdx.anims.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 1/8/2017.
 */
public class ParticleManager extends Group {
    private static boolean ambienceOn=true;
    public boolean debugMode;
    EmitterMap emitterMap;
    private Stage effects;


    public ParticleManager(Stage effects) {
        this.effects = effects;
        emitterMap = new EmitterMap(this);
//        GuiEventManager.bind(GraphicEvent.GRID_CREATED, portrait -> {
//         });
        GuiEventManager.bind(GuiEventType.UPDATE_EMITTERS, p -> {
            emitterMap.update();
            updateEmitters();
//
        });
    }

    public static boolean isAmbienceOn() {
        return ambienceOn;
    }

    public static void setAmbienceOn(boolean ambienceOn) {
        ParticleManager.ambienceOn = ambienceOn;
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
//            if (!emitterMap.fogMap.contains(actor))
//                actor.remove();
//            else if (effects.getActors().contains(actor, true))
//                effects.addActor(actor);
//        }
    }


}
