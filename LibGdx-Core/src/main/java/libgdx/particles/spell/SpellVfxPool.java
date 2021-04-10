package libgdx.particles.spell;

import com.badlogic.gdx.utils.Pool;
import libgdx.particles.EmitterPools;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.log.Chronos;
import main.system.launch.Flags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 11/14/2018.
 */
public class SpellVfxPool extends EmitterPools {

    private static final Map<String, Pool<SpellVfx>> actorPoolMap = new HashMap<>();

    public static SpellVfx getEmitterActor(String path) {
        if (Flags.isJar())
            System.out.println("getEmitterActor " + path);

        if (SpellVfxMaster.isRandomVfx()) {
            path = SpellVfxMaster.getRandomVfx(path);
        }

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

    public static List<SpellVfx> getEmitters(String data, int prePool) {
        List<SpellVfx> list = new ArrayList<>();
        for (String path :
                ContainerUtils.openContainer(data)) {
            if (!path.contains("vfx")) {
                if (!path.contains("atlas")) {
                    path = PathFinder.getVfxAtlasPath() + path;
                } else
                    path = PathFinder.getVfxPath() + path;
            }
            Chronos.mark("emitter " + path);
            SpellVfx emitter = null;

            String speed = VariableManager.getVar(path);
            path = VariableManager.removeVarPart(path);

            for (int i = 0; i < prePool; i++) {
                emitter =   getEmitterActor(path);
            }
            if (NumberUtils.isNumber(speed, false)) {
                emitter.setSpeed(Float.parseFloat(speed) / 100);
            }
            if (emitter != null) {
                list.add(emitter);
            }
            Chronos.logTimeElapsedForMark("emitter " + path);
        }
        return list;
    }
}
