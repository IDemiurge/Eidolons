package main.libgdx.anims.std;

import main.entity.obj.top.DC_ActiveObj;
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
import main.libgdx.anims.AnimData;

import java.util.function.Supplier;

/**
 * Created by JustMe on 2/2/2017.
 */
public class CounterAnim extends ActionAnim {
    public CounterAnim(DC_ActiveObj active, AnimData animData, Supplier<String> imagePath, ANIM_MOD[] anim_mods) {
        super(active, animData, imagePath, anim_mods);
    }
}
