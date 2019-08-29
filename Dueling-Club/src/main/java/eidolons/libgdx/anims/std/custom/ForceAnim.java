package eidolons.libgdx.anims.std.custom;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.anim3d.AnimMaster3d;
import eidolons.libgdx.anims.anim3d.Weapon3dAnim;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.anims.std.EffectAnimCreator;
import eidolons.libgdx.anims.std.EventAnimCreator;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.particles.PhaseVfx;
import eidolons.libgdx.particles.VfxContainer;
import eidolons.libgdx.particles.spell.VfxShaper;
import eidolons.libgdx.particles.spell.VfxShaper.VFX_SHAPE;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.system.math.PositionMaster;

/**
 * Created by JustMe on 11/28/2018.
 */
public class ForceAnim extends Weapon3dAnim {

    private VfxContainer<PhaseVfx> shaped;
    private GenericEnums.DAMAGE_TYPE type;

    Weapon3dAnim weapon3dAnim;

    public ForceAnim(DC_ActiveObj active, ANIM_PART part) {
        super(active);
        type = active.getDamageType();

    }

    private String getSpritePath() {
        return "sprites/weapons3d/atlas/screen/ghost/ghost fist.txt";
    }

    @Override
    protected SpriteAnimation createSprite(AnimMaster3d.PROJECTION projection) {
        Array<TextureAtlas.AtlasRegion> regions = SpriteAnimationFactory.getSpriteAnimation(getSpritePath()).getAtlas()
                .findRegions("armored fist punch " + projection.toString().toLowerCase() +
                        "/armored fist punch " + projection.toString().toLowerCase());
        sprite = SpriteAnimationFactory.getSpriteAnimation(regions, 15f / 100, 1);
        sprite.setBlending(SuperActor.BLENDING.SCREEN);
        switch (projection) {
            case FROM:
                sprite.setFlipX(!PositionMaster.isAbove(getRef().getTargetObj(), getRef().getSourceObj()));
                break;
            case TO:
                sprite.setFlipX(PositionMaster.isAbove(getRef().getTargetObj(), getRef().getSourceObj()));
                break;
            case HOR:
                sprite.setFlipX(PositionMaster.isToTheLeft(getRef().getTargetObj(), getRef().getSourceObj()));
                break;
        }
        return sprite;

    }


    @Override
    protected void resetEmitters() {
        super.resetEmitters();
    }

    @Override
    public void start() {
        try {
            super.start();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        try {
            String path =// SpellVfxMaster.getRandomVfx
                    PhaseVfx.isRandom() ? PathFinder.getVfxAtlasPath() + "invert/" :
                            "breath";
            //         );  //damage/fire
//            if (type != null) {
//                path = EffectAnimCreator.getVfx(type);
//            }

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
