package eidolons.libgdx.anims.std.custom;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import eidolons.libgdx.particles.PhaseVfx;
import eidolons.libgdx.particles.VfxContainer;
import eidolons.libgdx.particles.spell.VfxShaper;
import eidolons.libgdx.particles.spell.VfxShaper.VFX_SHAPE;
import main.data.filesys.PathFinder;

/**
 * Created by JustMe on 11/28/2018.
 */
public class ForceAnim extends Anim {

    private VfxContainer<PhaseVfx> shaped;

    public ForceAnim(DC_ActiveObj active, ANIM_PART part) {
        super(active, createForceParams(active)

        );
    }

    private static AnimData createForceParams(DC_ActiveObj active) {
        AnimData animData = new AnimData();
        return animData;
    }

    @Override
    protected void resetEmitters() {
        super.resetEmitters();
    }

    @Override
    public void start() {
        super.start();
        String path =// SpellVfxMaster.getRandomVfx
         PhaseVfx.isRandom() ? PathFinder.getVfxAtlasPath() + "spell/" :
          "nether";
        //         );  //damage/fire
        shaped = VfxShaper.shape(path, VFX_SHAPE.LINE, origin, destination);

        emitterList.clear();
        emitterList.add(shaped);
        resetEmitters();
        startEmitters();

        for (PhaseVfx vfx : shaped.getNested()) {
            vfx.setTimeToNext(duration / 3 * 2);
            vfx.act(1.5f);
        }
    }

    @Override
    public void finished() {
        super.finished();
        //        for (PhaseVfx vfx : shaped.getNested()) {
        //            vfx.setOnetime(true);
        //        }
    }

    @Override
    protected void initSpeed() {
        super.initSpeed();
        duration *= 1.75f;
        if (destination != null && origin != null)
            super.initSpeedForDuration(duration);
        //where and how is the real speed used?
    }

    @Override
    public float getPixelsPerSecond() {
        return 100;
    }
}
