package eidolons.libgdx.particles.spell;

import com.badlogic.gdx.utils.Pool;
import eidolons.libgdx.particles.EMITTER_PRESET;
import eidolons.libgdx.particles.EmitterPools;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.Chronos;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 11/14/2018.
 */
public class SpellVfxPool extends EmitterPools{

    private static Map<String, Pool<SpellVfx>> actorPoolMap = new HashMap<>();

    public static SpellVfx getEmitterActor(String path) {
        if (CoreEngine.isJar())
            System.out.println("getEmitterActor " + path);


        final String finalPath = path.toLowerCase();
        Pool<SpellVfx> pool = actorPoolMap.get(finalPath);
        if (pool == null) {
            pool = new Pool<SpellVfx>() {
                @Override
                protected SpellVfx newObject() {
                    try {
                        return new SpellVfx(finalPath);
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
    public static List<SpellVfx> getEmitters(String data) {
        List<SpellVfx> list = new ArrayList<>();
        for (String path :
         ContainerUtils.openContainer(data)) {

            Chronos.mark("emitter " + path);
            SpellVfx emitter = null;
            EMITTER_PRESET sfx = new EnumMaster<EMITTER_PRESET>().
             retrieveEnumConst(EMITTER_PRESET.class, path);

            emitter =  getEmitterActor(path);
            if (emitter != null) {
                list.add(emitter
                );
            }
            Chronos.logTimeElapsedForMark("emitter " + path);
        }
        return list;
    }
}
