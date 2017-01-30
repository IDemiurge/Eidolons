package main.libgdx.anims.particles;

import com.badlogic.gdx.utils.Pool;
import main.content.CONTENT_CONSTS2.SFX;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 1/16/2017.
 */
public class EmitterPools {

    private static Map<String, Pool<EmitterActor>> poolMap = new HashMap<>();


    public static EmitterActor getEmitterActor(SFX sfx) {
        return getEmitterActor(sfx.path);
    }

    public static EmitterActor getEmitterActor(String path) {
        final String finalPath = path.toLowerCase();
        Pool<EmitterActor> pool = poolMap.get(finalPath);
        if (pool == null) {
            pool = new Pool<EmitterActor>() {
                @Override
                protected EmitterActor newObject() {
                    return new EmitterActor(finalPath);
                }
            };
            poolMap.put(finalPath, pool);
        }
        return pool.obtain();
    }

    public static void freeEmitter(EmitterActor e) {
        Pool<EmitterActor> pool = poolMap.get(e.path.toLowerCase());
        if (pool == null)
            return;
        pool.free(e);

    }
}
