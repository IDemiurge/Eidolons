package libgdx.anims.std.custom;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.active.ActiveObj;
import eidolons.content.consts.VisualEnums.ANIM_PART;
import libgdx.anims.anim3d.Weapon3dAnim;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.particles.PhaseVfx;
import libgdx.particles.VfxContainer;
import libgdx.particles.spell.SpellVfx;
import libgdx.particles.spell.VfxShaper;
import eidolons.content.consts.Sprites;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.system.auxiliary.RandomWizard;
import main.system.launch.Flags;
import main.system.math.PositionMaster;

import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 11/28/2018.
 */
public class ForceAnim extends Weapon3dAnim {

    private final GenericEnums.DAMAGE_TYPE type;

    public static final List vfxList_death = Arrays.asList(GenericEnums.VFX.dark_blood,
            GenericEnums.VFX.missile_death,
            GenericEnums.VFX.weave_death
            //            GenericEnums.VFX.invert_breath,
    );
    public static final List vfxList_electric = Arrays.asList(GenericEnums.VFX.missile_arcane_intense,
            GenericEnums.VFX.missile_electric_intense,
            GenericEnums.VFX.missile_electric);
    public static final List vfxList_shadow = Arrays.asList(GenericEnums.VFX.dark_impact,
            GenericEnums.VFX.invert_missile,
            GenericEnums.VFX.weave_pale
            //            GenericEnums.VFX.invert_breath,
    );
    public static final List vfxList_chaos = Arrays.asList(//            GenericEnums.VFX.missile_chaos,
            //            GenericEnums.VFX.weave_chaos,
            GenericEnums.VFX.weave_nether
            //            GenericEnums.VFX.weave_chaos,
            //            GenericEnums.VFX.missile_arcane_pink,
            //            GenericEnums.VFX.spell_chaos_flames,
            //            GenericEnums.VFX.spell_demonfire,
    );


    public ForceAnim(ActiveObj active, ANIM_PART part) {
        super(active);
        type = active.getActiveWeapon().getDamageType();

    }

    private String getSpritePath() {
//TODO tester hack
        if (true)
            return Sprites.GHOST_FIST;

        if (type == GenericEnums.DAMAGE_TYPE.SHADOW || type == GenericEnums.DAMAGE_TYPE.DEATH) {
            if (!Flags.isSuperLite())
                return Sprites.REAPER_SCYTHE;
            else {
                return Sprites.GHOST_FIST;
            }
        }
        if (type == GenericEnums.DAMAGE_TYPE.CHAOS) {
            return Sprites.GHOST_FIST;
        }
        if (type == GenericEnums.DAMAGE_TYPE.LIGHTNING) {
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
    protected SpriteAnimation createSprite(VisualEnums.PROJECTION projection) {
        SpriteAnimation atlas = SpriteAnimationFactory.getSpriteAnimation(getSpritePath());

        Array<TextureAtlas.AtlasRegion> regions;
        if (getSpritePath().equalsIgnoreCase(Sprites.REAPER_SCYTHE)) {
            regions = SpriteAnimationFactory.getSpriteAnimation(getSpritePath()).getAtlas()
                    .findRegions("Reaper_Scythe_Scythe_Swing_" + projection.toString().toLowerCase());
        } else if (getSpritePath().equalsIgnoreCase(Sprites.GHOST_FIST)) {

            regions = SpriteAnimationFactory.getSpriteAnimation(getSpritePath()).getAtlas()
                    .findRegions("armored fist punch " + projection.toString().toLowerCase() +
                            "/armored fist punch " + projection.toString().toLowerCase());

        } else {
            regions = atlas.getRegions();
        }
        float dur = duration / regions.size;

        sprite = SpriteAnimationFactory.getSpriteAnimation(regions, dur, 1);
        sprite.setAlpha(0.7f);
        sprite.setBlending(GenericEnums.BLENDING.SCREEN);
        switch (projection) {
            case FROM:
                sprite.setFlipX(!PositionMaster.isAbove(getRef().getTargetObj(), getRef().getSourceObj()));
                break;
            case TO:
                sprite.setFlipX(PositionMaster.isAbove(getRef().getTargetObj(), getRef().getSourceObj()));
                break;
            case HOR:
                if (getSpritePath().equalsIgnoreCase(Sprites.GHOST_FIST)) {
                    sprite.setFlipX(PositionMaster.isToTheLeft(getRef().getTargetObj(), getRef().getSourceObj()));
                } else if (getSpritePath().equalsIgnoreCase(Sprites.REAPER_SCYTHE)) {
                    sprite.setFlipX(PositionMaster.isToTheLeft(getRef().getTargetObj(), getRef().getSourceObj()));
                } else
                    sprite.setFlipX(!PositionMaster.isToTheLeft(getRef().getTargetObj(), getRef().getSourceObj()));
                break;
        }
        return sprite;

    }

    protected boolean isInvertScreen() {
        //        getActive().getActiveWeapon().checkSingleProp()
        if (type != null) {
            switch (type) {
                case LIGHTNING:
                    return false;
            }
        }
        return !getActive().getOwnerUnit().isMine();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (type == GenericEnums.DAMAGE_TYPE.LIGHTNING) {
            sprite.setBlending(GenericEnums.BLENDING.SCREEN);
        }
        if (isVfxOn())
            for (SpellVfx spellVfx : emitterList) {
                //            spellVfx.getEffect().getEmitters().forEach(e -> e.setAdditive(false));
                spellVfx.getEffect().setAlpha(0.1f);

            }
        super.draw(batch, parentAlpha);
    }

    @Override
    protected void resetEmitters() {
        super.resetEmitters();
    }

    @Override
    public void updatePosition(float delta) {
        if (sprite != null)
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
        if (sprite != null) {
            sprite.setFps(15);
        }

        if (isVfxOn())
            try {
                String path = PathFinder.getVfxPath() + getVfxPath();

                main.system.auxiliary.log.LogMaster.devLog("force anim destination: " + destination);
                VfxContainer<PhaseVfx> shaped = VfxShaper.shape(path, VfxShaper.VFX_SHAPE.LINE, origin, destination);
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

    private boolean isVfxOn() {
        return !getActive().isMine();
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
        //        super.initSpeed();
        //        duration *= 1.75f;
        duration = 1;
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
