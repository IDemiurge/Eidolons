package eidolons.libgdx.anims.anim3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.utils.Array;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.AnimMaster3d;
import eidolons.libgdx.anims.AnimMaster3d.PROJECTION;
import eidolons.libgdx.anims.AnimMaster3d.WEAPON_ANIM_CASE;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.anims.std.ActionAnim;
import main.entity.Ref;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 9/6/2017.
 */
public class Weapon3dAnim extends ActionAnim {
    protected SpriteAnimation sprite;
    protected Map<PROJECTION, SpriteAnimation> projectionsMap = new HashMap<>();

    public Weapon3dAnim(DC_ActiveObj active) {
        super(active, init3dAnimData(active));
    }

    public static AnimData init3dAnimData(DC_ActiveObj active) {
        //duration, speed,
//        ANIM_VALUES.SCALE;
//        ANIM_VALUES.PARTICLE_EFFECTS;
        return new AnimData();
    }

    @Override
    protected Action getAction() {
        setColor(new Color(1, 1, 1, 1));
        AlphaAction alphaAction = (AlphaAction) ActorMaster.getAction(AlphaAction.class);
        alphaAction.setDuration(getDuration() / 2);
        alphaAction.setAlpha(0);
        DelayAction delayed = new DelayAction(getDuration() / 2);
        delayed.setAction(alphaAction);
        return delayed;
    }


    @Override
    public float getDefaultSpeed() {
        return AnimMaster3d.getWeaponActionSpeed(getActive());

    }

    @Override
    protected Texture getTexture() {
        if (isValid())
            return null;
        return super.getTexture();
    }

    private boolean isValid() {
        if (sprite != null)
            if (sprite.getRegions().size > 0)
                return true;
        return false;
    }

    public String getTexturePath() {
        return GdxImageMaster.getAttackActionPath(getActive(), getActive().getActiveWeapon());
    }

    @Override
    protected void resetSprites() {
//        if (sprite != null)
//        sprite.dispose(); //or cache per projection!
        sprite = get3dSprite();
        if (sprite == null)
            return;
        sprite.setScale(getSpriteScale());
        sprite.setFlipX(checkFlipHorizontally());

        if (!isRandomized()) {
            getSprites().clear();
            sprites.add(sprite);
            return;
        }
        SpriteAnimation randomized = getRandomizedSprite(sprite);
        if (randomized.getRegions().size == 0)
            return;
        getSprites().clear();
        getSprites().add(randomized);
    }

    protected String getDefaultTexturePath() {
        return StringMaster.getAppendedImageFile(
         GdxImageMaster.
          getAttackActionPath(getActive()), " 64");
    }

    private boolean isRandomized() {
//        return getActive().isMelee();
        return false;
    }

    private SpriteAnimation getRandomizedSprite(SpriteAnimation sprite) {
        Array<AtlasRegion> regions = sprite.getRegions();
        //backward?
        List<DC_ActiveObj> subactions = new ArrayList<>(getActive().getParentAction().getSubActions());
        subactions.remove(getActive());
        subactions.removeIf(a ->
         a.isThrow() ||
          a.getActiveWeapon() != getActive().getActiveWeapon());
        Array<AtlasRegion> newRegions = AnimMaster3d.getRegions(
         WEAPON_ANIM_CASE.NORMAL, subactions.get(RandomWizard.getRandomListIndex(subactions))
         , getProjection().bool);

        newRegions.removeRange(0, newRegions.size / 2);

        regions.addAll(newRegions);
        float frameduration = getDuration() / regions.size;
        return SpriteAnimationFactory.getSpriteAnimation(regions, frameduration, 1);
    }

    protected float getSpriteScale() {
        if (getActive().getActiveWeapon().isNatural()) {
            float code = getActive().getActiveWeapon().getMaterial().getCode();
            if (code == -1)
                code = 3.5f;
            code += 4;
            return code / 10;
        }

        return 1f;
    }

    protected boolean checkFlipHorizontally() {
        boolean offhand = getActive().isOffhand();
        boolean flipHor = false;
        if (getProjection() == PROJECTION.HOR) {
            flipHor = getActive().getOwnerObj().getFacing() == main.game.bf.directions.FACING_DIRECTION.WEST;// PositionMaster.isToTheLeft(activeObj.getOwnerObj(), targetObj);
        } else {
            flipHor = (getProjection() == PROJECTION.TO) != offhand;
        }
        return flipHor;
    }

    protected SpriteAnimation get3dSprite() {
        PROJECTION projection = getProjection();

        sprite = projectionsMap.get(projection);
        if (sprite != null) {
            return sprite;
        }
        try {
            sprite = AnimMaster3d.getSpriteForAction(getDuration(),
             getActive(), getCase(), projection);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        projectionsMap.put(projection, sprite);
        return sprite;
    }

    @Override
    public Vector2 getOffsetOrigin() {
        switch (getProjection()) {
            case FROM:
                return new Vector2(0, 32);
            case TO:
                return new Vector2(0, -32);
            case HOR:
                return new Vector2(32, 0);
        }
        return super.getOffsetOrigin();
    }

    protected WEAPON_ANIM_CASE getCase() {
//        return WEAPON_ANIM_CASE. BLOCKED;

        if (getActive().isFailedLast())
            return WEAPON_ANIM_CASE.MISS;
//        return WEAPON_ANIM_CASE.PARRY; counter?


        return WEAPON_ANIM_CASE.NORMAL;
    }

    private PROJECTION getProjectionByFacing(FACING_DIRECTION facing) {
        if (!facing.isVertical())
            return PROJECTION.HOR;
        return facing == main.game.bf.directions.FACING_DIRECTION.NORTH ? PROJECTION.TO : PROJECTION.FROM;
    }

    protected PROJECTION getProjection() {
        if (getRef().getTargetObj() == null)
            return getProjectionByFacing(getActive().getOwnerObj().getFacing());
        Boolean b =
         PositionMaster.isAboveOr(getRef().getSourceObj(), ref.getTargetObj());
        if (getActive().getOwnerObj().getCoordinates().equals(ref.getTargetObj().getCoordinates()))
            b = getActive().getOwnerObj().isMine();
        PROJECTION projection = PROJECTION.HOR;
        if (b != null)
            projection = b ? PROJECTION.FROM : PROJECTION.TO;
        return projection;
    }


    @Override
    public void start(Ref ref) {
        initDuration();
        super.start(ref);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

}
