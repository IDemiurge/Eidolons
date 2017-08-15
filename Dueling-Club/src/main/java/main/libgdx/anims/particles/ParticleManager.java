package main.libgdx.anims.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;

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




}
