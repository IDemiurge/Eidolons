package libgdx.anims.std;

import eidolons.entity.active.ActiveObj;
import libgdx.anims.ANIM_MODS.ANIM_MOD;
import libgdx.anims.AnimData;

import java.util.function.Supplier;

/**
 * Created by JustMe on 2/2/2017.
 */
public class CounterAnim extends ActionAnim {
    public CounterAnim(ActiveObj active, AnimData animData, Supplier<String> imagePath, ANIM_MOD[] anim_mods) {
        super(active, animData, imagePath, anim_mods);
    }
}
