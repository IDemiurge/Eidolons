package main.libgdx.anims.particles;

import com.badlogic.gdx.utils.Pool;
import main.content.CONTENT_CONSTS2.SFX;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 1/16/2017.
 */
public class EmitterPools {

    private static Map<String, Pool<EmitterActor>> actorPoolMap = new HashMap<>();
    private static Map<String, Pool<ParticleEffect>> effectPoolMap = new HashMap<>();
    private static boolean poolingOn=true;


    public static EmitterActor getEmitterActor(SFX sfx) {
        return getEmitterActor(sfx.path);
    }

    public static EmitterActor getEmitterActor(String path) {
        if (!poolingOn)
            return new EmitterActor(path);
        final String finalPath = path.toLowerCase();
        Pool<EmitterActor> pool = actorPoolMap.get(finalPath);
        if (pool == null) {
            pool = new Pool<EmitterActor>() {
                @Override
                protected EmitterActor newObject() {
                    return new EmitterActor(finalPath);
                }
            };
            actorPoolMap.put(finalPath, pool);
        }
        return pool.obtain();
    }
    public static ParticleEffect getEffect(String path) {
        if (!poolingOn)
            return new ParticleEffect(path);
        final String finalPath = path.toLowerCase();
        Pool<ParticleEffect> pool = effectPoolMap.get(finalPath);
        if (pool == null) {
            pool = new Pool<ParticleEffect>() {
                @Override
                protected ParticleEffect newObject() {
                    return new ParticleEffect(finalPath);
                }
            };
            effectPoolMap.put(finalPath, pool);
        }
        return pool.obtain();
    }

    public static void freeEffect (ParticleEffect e) {
        Pool<ParticleEffect> pool = effectPoolMap.get(e.path.toLowerCase());
        if (pool == null)
            return;
        pool.free(e);

    }

    public static void freeActor(EmitterActor e) {
        Pool<EmitterActor> pool = actorPoolMap.get(e.path.toLowerCase());
        if (pool == null)
            return;
        pool.free(e);

    }
}
