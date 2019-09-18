package eidolons.libgdx.anims.std.custom;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.anims.anim3d.AnimMaster3d;
import eidolons.libgdx.anims.anim3d.Weapon3dAnim;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.particles.PhaseVfx;
import eidolons.libgdx.particles.VfxContainer;
import eidolons.libgdx.particles.spell.VfxShaper;
import eidolons.libgdx.particles.spell.VfxShaper.VFX_SHAPE;
import eidolons.libgdx.texture.Sprites;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.system.auxiliary.RandomWizard;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;

import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 11/28/2018.
 */
public class ForceAnim extends Weapon3dAnim {

    private VfxContainer<PhaseVfx> shaped;
    private GenericEnums.DAMAGE_TYPE type;

    public static final List vfxList_death = Arrays.asList(new GenericEnums.VFX[]{
            GenericEnums.VFX.dark_blood,
            GenericEnums.VFX.missile_death,
            GenericEnums.VFX.invert_breath,
    });
    public static final List vfxList_electric = Arrays.asList(new GenericEnums.VFX[]{
            GenericEnums.VFX.missile_arcane_intense,
            GenericEnums.VFX.missile_electric_intense,
            GenericEnums.VFX.missile_electric,
    });
    public static final List vfxList_shadow = Arrays.asList(new GenericEnums.VFX[]{
            GenericEnums.VFX.dark_impact,
            GenericEnums.VFX.invert_missile,
            GenericEnums.VFX.invert_breath,
    });
    public static final List vfxList_chaos = Arrays.asList(new GenericEnums.VFX[]{
            GenericEnums.VFX.missile_chaos,
            GenericEnums.VFX.weave_chaos,
            GenericEnums.VFX.missile_arcane_pink,
//            GenericEnums.VFX.spell_chaos_flames,
//            GenericEnums.VFX.spell_demonfire,
    });


    public ForceAnim(DC_ActiveObj active, ANIM_PART part) {
        super(active);
        type = active.getActiveWeapon().getDamageType();

    }

    private String getSpritePath() {
        if (getActive().getOwnerUnit().isMine()) {
            if (!CoreEngine.isSuperLite())
            if (type== GenericEnums.DAMAGE_TYPE.SHADOW) {
                return "sprites/weapons3d/atlas/pole arm/scythes/reaper scythe.txt";
            }
//            if (type== GenericEnums.DAMAGE_TYPE.CHAOS) {
            return "sprites/weapons3d/atlas/screen/ghost/ghost fist.txt";
//            }
        }
        if (type== GenericEnums.DAMAGE_TYPE.LIGHTNING) {
            return Sprites.GATE_LIGHTNING;
        }
        return Sprites.BIG_CLAW_ATTACK;
//        switch (getActive().getOwnerUnit().getName()) {
//            case "Mistborn Leviathan":
//                return Sprites.BIG_CLAW_ATTACK;
//        }
//        return "sprites/weapons3d/atlas/screen/ghost/ghost fist.txt";
    }

    @Override
    protected SpriteAnimation createSprite(AnimMaster3d.PROJECTION projection) {
        SpriteAnimation atlas = SpriteAnimationFactory.getSpriteAnimation(getSpritePath());

        Array<TextureAtlas.AtlasRegion> regions = null;
        if (getActive().getOwnerUnit().isMine()) {
//            if (type== GenericEnums.DAMAGE_TYPE.SHADOW) {
//                regions = SpriteAnimationFactory.getSpriteAnimation(getSpritePath()).getAtlas()
//                        .findRegions("Reaper_Scythe_Scythe_Swing_" + projection.toString().toLowerCase());
//            } else
            regions = SpriteAnimationFactory.getSpriteAnimation(getSpritePath()).getAtlas()
                    .findRegions("armored fist punch " + projection.toString().toLowerCase() +
                            "/armored fist punch " + projection.toString().toLowerCase());

        } else {
            regions = atlas.getRegions();
        }
        float dur = duration / regions.size;

        sprite = SpriteAnimationFactory.getSpriteAnimation(regions, dur, 1);
        sprite.setBlending(GenericEnums.BLENDING.SCREEN);
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
    public void draw(Batch batch, float parentAlpha) {
        if (type== GenericEnums.DAMAGE_TYPE.LIGHTNING){
            sprite.setBlending(GenericEnums.BLENDING.SCREEN);
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    protected void resetEmitters() {
        super.resetEmitters();
    }

    @Override
    public void updatePosition(float delta) {
        try {
            sprite.setX(origin.x);
            sprite.setY(origin.y);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        super.updatePosition(delta);
    }

    @Override
    public void start() {
        try {
            super.start();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        sprite.setFps(1/(getDuration()/sprite.getRegions().size));

        try {
            String path = PathFinder.getVfxPath() + getVfxPath();

            main.system.auxiliary.log.LogMaster.dev("force anim destination: " + destination);
            shaped = VfxShaper.shape(path, VFX_SHAPE.LINE, origin, destination);
            emitterList.clear();
            emitterList.add(shaped);
            resetEmitters();
            startEmitters();
            if (shaped == null) {
                return;
            }
            for (PhaseVfx vfx : shaped.getNested()) {
                vfx.setTimeToNext(duration / 3 * 2);
                vfx.act(1.5f);
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }


    }

    private String getVfxPath() {
        List list = vfxList_shadow;
        switch (type) {
            case CHAOS:
                list = vfxList_chaos;
                break;
            case SHADOW:
                list = vfxList_shadow;
                break;
            case DEATH:
                list = vfxList_death;
                break;
            case LIGHTNING:
                list = vfxList_electric;
                break;
        }
        GenericEnums.VFX vfx = (GenericEnums.VFX) RandomWizard.getRandomListObject(list);
        return vfx.getPath();
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
//        duration *= 1.75f;
        if (destination != null && origin != null)
            super.initSpeedForDuration(duration);
        //where and how is the real speed used?
        if (getSpeedY() != null) {
            setSpeedY(getSpeedY() / 2);
        }
        if (getSpeedX() != null) {
            setSpeedY(getSpeedX() / 2);
        }
    }

    @Override
    public float getPixelsPerSecond() {
        return 70f;
    }
}
