package eidolons.libgdx.particles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.Pool;
import eidolons.libgdx.anims.Assets;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.system.launch.CoreEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 1/16/2017.
 */
public class EmitterPools {

    private static Map<String, Pool<EmitterActor>> actorPoolMap = new HashMap<>();
    private static Map<String, Pool<ParticleEffectX>> effectPoolMap = new HashMap<>();
    private static boolean effectPoolingOn = true;
    private static boolean actorPoolingOn = true; //TODO emitters are not reset properly!
    private static boolean logging = true; //TODO emitters are not reset properly!
    private static int emittersCounter=0;

    public static EmitterActor getEmitterActor(GenericEnums.VFX sfx) {
        return getEmitterActor(sfx.getPath());
    }

    public static EmitterActor getDummy(String path) {
        main.system.auxiliary.log.LogMaster.important("Replacing vfx with dummy: " +path);
        return getEmitterActor(
                PathFinder.getVfxAtlasPath()+
                GenericEnums.VFX.DUMMY.path, true);
    }

    public static ParticleEffectX getDummyFx(String path) {
        main.system.auxiliary.log.LogMaster.important("Replacing vfx with dummy: " +path);
        return new DummyParticleEffectX(
                PathFinder.getVfxAtlasPath()+
                        GenericEnums.VFX.DUMMY.path );
    }
    public static EmitterActor getEmitterActor(String path ) {
        return getEmitterActor(path, false);
    }
        public static EmitterActor getEmitterActor(String path, boolean dummy) {
        if (!dummy){
        if (CoreEngine.isVfxOff()) {
            if (!checkVfx(path)) {
                return getDummy(path);
            }
            }
        }
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
                        if (dummy) {
                            return new DummyEmitterActor(finalPath);
                        }
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

    private static boolean checkVfx(String path) {
        if (path.contains("fire")){
            return true;
        }
        if (path.contains("flame")){
            return true;
        }
        return false;
    }

    public static ParticleEffectX getEffect(String path) {
//        if (CoreEngine.isJar())
//            System.out.println("getEffect " + path);

        if (CoreEngine.isVfxOff()){
            if (!checkVfx(path)) {
                return getDummyFx(path);
            }
        }

        if (!effectPoolingOn) {
            return new ParticleEffectX(path);
        }
        final String finalPath = path.toLowerCase();
        Pool<ParticleEffectX> pool = effectPoolMap.get(finalPath);
        if (pool == null) {
            pool = new Pool<ParticleEffectX>() {
                @Override
                protected ParticleEffectX newObject() {
//                  TODO   Assets.getVar().getManager().load();
                    emittersCounter++;
                    if (logging) {
                        main.system.auxiliary.log.LogMaster.important(emittersCounter + "th emitter: " +path);
                    }
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

    public static void init(AssetManager manager) {
//      TODO   manager.load("", );
    }

    public static void preloadDefaultEmitters() {
        if (ParticleEffectX.isEmitterAtlasesOn())
            return;
        for (GenericEnums.VFX sub : GenericEnums.VFX.values()) {
            if (sub.isPreloaded()) {
                Assets.get().getManager().load(sub.getPath(), ParticleEffect.class);
                getEmitterActor(sub);
            }
        }
    }

    public static boolean isPreloaded() {
        return true;
    }
}
