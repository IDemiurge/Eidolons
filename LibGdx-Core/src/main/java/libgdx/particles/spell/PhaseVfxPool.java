package libgdx.particles.spell;

import com.badlogic.gdx.utils.Pool;
import libgdx.anims.AnimEnums.ANIM_PART;
import libgdx.particles.PhaseVfx;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 12/1/2018.
 */
public class PhaseVfxPool {

    private static final Map<String, Pool<PhaseVfx>> actorPoolMap = new HashMap<>();

    public static PhaseVfx getEmitterActor(String path, ANIM_PART... parts) {

        final String finalPath = path.toLowerCase();


        Pool<PhaseVfx> pool = actorPoolMap.get(finalPath);
        if (pool == null) {
            pool = new Pool<PhaseVfx>() {
                @Override
                protected PhaseVfx newObject() {
                    try {
                        return new PhaseVfx(finalPath,  parts);
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
}
