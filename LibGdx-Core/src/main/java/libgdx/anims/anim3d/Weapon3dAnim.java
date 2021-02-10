package libgdx.anims.anim3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.utils.Array;
import eidolons.entity.active.DC_ActiveObj;
import libgdx.GdxImageMaster;
import libgdx.anims.AnimData;
import libgdx.anims.actions.ActionMaster;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.anims.std.ActionAnim;
import libgdx.assets.AnimMaster3d;
import libgdx.assets.AssetEnums;
import libgdx.assets.Atlases;
import main.content.enums.GenericEnums;
import main.entity.Ref;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.launch.Flags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 9/6/2017.
 */
public class Weapon3dAnim extends ActionAnim {
    protected SpriteAnimation sprite;
    protected Map<AssetEnums.PROJECTION, SpriteAnimation> projectionsMap = new HashMap<>();

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
        AlphaAction alphaAction = (AlphaAction) ActionMaster.getAction(AlphaAction.class);
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

    private boolean isValid() {
        if (sprite != null)
            return sprite.getRegions().size > 0;
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
        sprite.setSpeed(speedMod);
        sprite.setScale(getSpriteScale());
        sprite.setFlipX(checkFlipHorizontally());
        if (isScreen()){
            sprite.setBlending(GenericEnums.BLENDING.SCREEN);
        }
        if (isInvertScreen()){
            sprite.setBlending(GenericEnums.BLENDING.INVERT_SCREEN);
        }
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

    protected boolean isInvertScreen() {
//        getActive().getActiveWeapon().checkSingleProp()
//        return !getActive().getOwnerUnit().isMine();
        return false;
    }

    private boolean isScreen() {
        return false;
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
        Array<AtlasRegion> newRegions = Atlases.getRegions(
         AssetEnums.WEAPON_ANIM_CASE.NORMAL, subactions.get(RandomWizard.getRandomIndex(subactions))
         , getProjection(ref,getActive()).bool);

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
        boolean flipHor;
        if (getProjection(ref,getActive()) == AssetEnums.PROJECTION.HOR) {
            flipHor = getActive().getOwnerUnit().getFacing() == FACING_DIRECTION.WEST;
            // PositionMaster.isToTheLeft(activeObj.getOwnerUnit(), targetObj);
        } else {
            flipHor = (getProjection(ref,getActive()) == AssetEnums.PROJECTION.TO) != offhand;
//            if (RandomWizard.chance(33))
//                flipHor = !flipHor; TODO anim Review - is it viable?
        }
        return flipHor;
    }

    protected SpriteAnimation get3dSprite() {
        AssetEnums.PROJECTION projection = getProjection(ref,getActive());

        sprite = projectionsMap.get(projection);
        if (sprite != null) {
            return sprite;
        }

        sprite = createSprite(projection);
        if (sprite.getRegions().size == 0) {
            return null;
        }
        projectionsMap.put(projection, sprite);
        return sprite;
    }

    protected SpriteAnimation createSprite(AssetEnums.PROJECTION projection) {
        return Atlases.getSpriteForAction(getDuration(),
                getActive(), getCase(), projection);
    }

    @Override
    public Vector2 getOffsetOrigin() {
        switch (getProjection(ref,getActive())) {
            case AssetEnums.PROJECTION.FROM:
                return new Vector2(0, 32);
            case AssetEnums.PROJECTION.TO:
                return new Vector2(0, -32);
            case AssetEnums.PROJECTION.HOR:
                return new Vector2(32, 0);
        }
        return super.getOffsetOrigin();
    }

    protected AssetEnums.WEAPON_ANIM_CASE getCase() {
        //        return WEAPON_ANIM_CASE. BLOCKED;

        if (getActive().isFailedLast())
            return AssetEnums.WEAPON_ANIM_CASE.MISS;
        //        return WEAPON_ANIM_CASE.PARRY; counter?


        return AssetEnums.WEAPON_ANIM_CASE.NORMAL;
    }

    public AssetEnums.PROJECTION getProjection( ) {
        return getProjection(getRef(), getActive());
    }
    public AssetEnums.PROJECTION getProjection(Ref ref, DC_ActiveObj active) {
        return AnimMaster3d.getProjection(ref, active);
    }

    @Override
    public void start(Ref ref) {
        if (isOn())
            return;
        initDuration();
        super.start(ref);
    }

    private boolean isOn() {
        return !Flags.isLiteLaunch();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        if (batch instanceof CustomSpriteBatch) {
//           post=  ((CustomSpriteBatch) batch);
//        }
        if (Flags.isFootageMode())
            return;
        super.draw(batch, parentAlpha);
    }

}
