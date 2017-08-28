package main.libgdx.anims.particles;

import com.badlogic.gdx.utils.Pool;
import main.content.CONTENT_CONSTS2.SFX;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.launch.CoreEngine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 1/16/2017.
 */
public class EmitterPools {

    private static Map<String, Pool<EmitterActor>> actorPoolMap = new HashMap<>();
    private static Map<String, Pool<ParticleEffect>> effectPoolMap = new HashMap<>();
    private static boolean effectPoolingOn = false;
    private static boolean actorPoolingOn = false; //TODO emitters are not reset properly!

    public static EmitterActor getEmitterActor(SFX sfx) {
        return getEmitterActor(sfx.path);
    }

    public static EmitterActor getEmitterActor(String path) {
      if (CoreEngine.isJar())
          System.out.println("getEmitterActor "+path);
        if (!actorPoolingOn) {
            return new EmitterActor(path);

        }
        final String finalPath = path.toLowerCase();
        Pool<EmitterActor> pool = actorPoolMap.get(finalPath);
        if (pool == null) {
            pool = new Pool<EmitterActor>() {
                @Override
                protected EmitterActor newObject() {
                    try {
                        return new EmitterActor(finalPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            actorPoolMap.put(finalPath, pool);
        }
        return pool.obtain();
    }

    public static ParticleEffect getEffect(String path) {
        if (CoreEngine.isJar())
            System.out.println("getEffect "+path);
        if (!effectPoolingOn) {
            return new ParticleEffect(path);
        }
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

    public static void freeEffect(ParticleEffect e) {
        Pool<ParticleEffect> pool = effectPoolMap.get(e.path.toLowerCase());
        if (pool == null) {
            return;
        }
        pool.free(e);

    }

    public static void freeActor(EmitterActor e) {
        Pool<EmitterActor> pool = actorPoolMap.get(e.path.toLowerCase());
        if (pool == null) {
            return;
        }
        pool.free(e);

    }

    public static List<EmitterActor> getEmitters(String data) {
        List<EmitterActor> list = new LinkedList<>();
        for (String path :
         StringMaster.openContainer(data)) {

            Chronos.mark("emitter " + path);
            EmitterActor emitter = null;
            SFX sfx = new EnumMaster<SFX>().
             retrieveEnumConst(SFX.class, path);
            if (sfx == null) {
                emitter = EmitterPools.getEmitterActor(path);
            } else {
                emitter = EmitterPools.getEmitterActor(sfx);
            }
            if (emitter != null) {
                list.add(emitter
                );
            }
            Chronos.logTimeElapsedForMark("emitter " + path);
        }
        return list;
    }
}
