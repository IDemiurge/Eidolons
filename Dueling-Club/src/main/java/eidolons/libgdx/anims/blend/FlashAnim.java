package eidolons.libgdx.anims.blend;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.std.ActionAnim;

/**
 * Created by JustMe on 5/3/2018.
 *
 * on source or target?
 *
 *
 */
public class FlashAnim extends ActionAnim {
    public FlashAnim(DC_ActiveObj active, AnimData animData ) {
        super(active, animData );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        BlendMaster.setBlend(BLENDING.SCREEN, batch);
//        super.draw(batch, parentAlpha);
//        BlendMaster.reset(batch);
    }
}
