package main.libgdx.anims.particles;

/**
 * Created by JustMe on 1/8/2017.
 */
public class ParticleManager   {
    private static boolean ambienceOn=true;
    public boolean debugMode;
    EmitterMap emitterMap;


    public ParticleManager( ) {
        emitterMap = new EmitterMap( );
    }

    public static boolean isAmbienceOn() {
        return ambienceOn;
    }

    public static void setAmbienceOn(boolean ambienceOn) {
        ParticleManager.ambienceOn = ambienceOn;
    }

    public EmitterMap getEmitterMap() {
        return emitterMap;
    }





}
