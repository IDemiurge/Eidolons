package main.libgdx.anims.particles;

import com.badlogic.gdx.math.Vector2;
import main.content.CONTENT_CONSTS2.SFX;

import java.util.List;

/**
 * Created by JustMe on 1/8/2017.
 */
public class ParticleManager {
    private static final SFX FOG_SFX = SFX.SMOKE_TEST;
    private static boolean ambienceOn = true;
    public boolean debugMode;
    EmitterMap emitterMap;
    private static boolean ambienceMoveOn;


    public ParticleManager() {
        emitterMap = new EmitterMap();
    }

    public static boolean isAmbienceOn() {
        return ambienceOn;
    }

    public static void setAmbienceOn(boolean ambienceOn) {
        ParticleManager.ambienceOn = ambienceOn;
    }

    public static Ambience addFogOn(Vector2 at, List<Ambience> fogList) {
        Ambience fog = new Ambience(FOG_SFX){
            @Override
            protected boolean isCullingOn() {
                return false;
            }
        };
        fog.setPosition(
         at.x,
         at.y);
        fog.added();
        fog.setVisible(true);
        fog.getEffect().start();
        if (fogList != null)
            fogList.add(fog);
        return fog;
    }

    public static boolean isAmbienceMoveOn() {
        return ambienceMoveOn;
    }

    public static void setAmbienceMoveOn(boolean ambienceMoveOn) {
        ParticleManager.ambienceMoveOn = ambienceMoveOn;
    }

    public EmitterMap getEmitterMap() {
        return emitterMap;
    }
}
