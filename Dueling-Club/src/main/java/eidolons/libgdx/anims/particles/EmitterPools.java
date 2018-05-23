package eidolons.libgdx.anims.particles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.Pool;
import eidolons.libgdx.anims.Assets;
import main.content.CONTENT_CONSTS2.EMITTER_PRESET;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 1/16/2017.
 */
public class EmitterPools {

    private static Map<String, Pool<EmitterActor>> actorPoolMap = new HashMap<>();
    private static Map<String, Pool<ParticleEffectX>> effectPoolMap = new HashMap<>();
    private static boolean effectPoolingOn = true;
    private static boolean actorPoolingOn = true; //TODO emitters are not reset properly!

    public static EmitterActor getEmitterActor(EMITTER_PRESET sfx) {
        return getEmitterActor(sfx.path);
    }

    public static EmitterActor getEmitterActor(String path) {
        if (CoreEngine.isJar())
            System.out.println("getEmitterActor " + path);
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
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                    return null;
                }
            };
            actorPoolMap.put(finalPath, pool);
        }
        return pool.obtain();
    }

    public static ParticleEffectX getEffect(String path) {
//        if (CoreEngine.isJar())
//            System.out.println("getEffect " + path);
        if (!effectPoolingOn) {
            return new ParticleEffectX(path);
        }
        final String finalPath = path.toLowerCase();
        Pool<ParticleEffectX> pool = effectPoolMap.get(finalPath);
        if (pool == null) {
            pool = new Pool<ParticleEffectX>() {
                @Override
                protected ParticleEffectX newObject() {
//                  TODO   Assets.get().getManager().load();
                    return new ParticleEffectX(finalPath);
                }
            };
            effectPoolMap.put(finalPath, pool);
        }
        return pool.obtain();
    }

    public static void freeEffect(ParticleEffectX e) {
        Pool<ParticleEffectX> pool = effectPoolMap.get(e.path.toLowerCase());
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
        List<EmitterActor> list = new ArrayList<>();
        for (String path :
         StringMaster.openContainer(data)) {

            Chronos.mark("emitter " + path);
            EmitterActor emitter = null;
            EMITTER_PRESET sfx = new EnumMaster<EMITTER_PRESET>().
             retrieveEnumConst(EMITTER_PRESET.class, path);
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

    public static void init(AssetManager manager) {
//      TODO   manager.load("", );
    }

    public static void preloadDefaultEmitters() {
        for (EMITTER_PRESET sub : EMITTER_PRESET.values()) {
            if (sub.isPreloaded())
                Assets.get().getManager().load(sub.path, ParticleEffect.class);
        }
    }
}
