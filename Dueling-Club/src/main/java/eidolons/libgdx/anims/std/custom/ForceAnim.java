package eidolons.libgdx.anims.std.custom;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import eidolons.libgdx.anims.std.EffectAnimCreator;
import eidolons.libgdx.anims.std.EventAnimCreator;
import eidolons.libgdx.particles.PhaseVfx;
import eidolons.libgdx.particles.VfxContainer;
import eidolons.libgdx.particles.spell.VfxShaper;
import eidolons.libgdx.particles.spell.VfxShaper.VFX_SHAPE;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;

/**
 * Created by JustMe on 11/28/2018.
 */
public class ForceAnim extends Anim {

    private VfxContainer<PhaseVfx> shaped;
    private GenericEnums.DAMAGE_TYPE type;

    public ForceAnim(DC_ActiveObj active, ANIM_PART part) {
        super(active, createForceParams(active));
        type = active.getDamageType();
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
        try {
            String path =// SpellVfxMaster.getRandomVfx
                    PhaseVfx.isRandom() ? PathFinder.getVfxAtlasPath() + "spell/" :
                            "nether";
            //         );  //damage/fire
            if (type != null) {
                path =EffectAnimCreator.getVfx(type);
            }

            shaped = VfxShaper.shape(path, VFX_SHAPE.LINE, origin, destination);
            emitterList.clear();
            emitterList.add(shaped);
            resetEmitters();
            startEmitters();

            for (PhaseVfx vfx : shaped.getNested()) {
                vfx.setTimeToNext(duration / 3 * 2);
                vfx.act(1.5f);
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
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
        return 100f;
    }
}
