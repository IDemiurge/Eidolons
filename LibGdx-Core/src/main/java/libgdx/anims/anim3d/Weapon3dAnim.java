package libgdx.anims.anim3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.utils.Array;
import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.libgdx.GdxStringUtils;
import eidolons.entity.active.ActiveObj;
import libgdx.anims.AnimData;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.anims.std.ActionAnim;
import libgdx.assets.AnimMaster3d;
import libgdx.assets.Atlases;
import main.content.enums.GenericEnums;
import main.entity.Ref;
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
    protected Map<VisualEnums.PROJECTION, SpriteAnimation> projectionsMap = new HashMap<>();

    public Weapon3dAnim(ActiveObj active) {
        super(active, init3dAnimData(active));
    }

    public static AnimData init3dAnimData(ActiveObj active) {
        //duration, speed,
        //        ANIM_VALUES.SCALE;
        //        ANIM_VALUES.PARTICLE_EFFECTS;
        return new AnimData();
    }

    @Override
    protected Action getAction() {
        setColor(new Color(1, 1, 1, 1));
        AlphaAction alphaAction = (AlphaAction) ActionMasterGdx.getAction(AlphaAction.class);
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
        return GdxStringUtils.getAttackActionPath(getActive(), getActive().getActiveWeapon());
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
        if (isScreen()) {
            sprite.setBlending(GenericEnums.BLENDING.SCREEN);
        }
        if (isInvertScreen()) {
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
                GdxStringUtils.
                        getAttackActionPath(getActive()), " 64");
    }

    private boolean isRandomized() {
        //        return getActive().isMelee();
        return false;
    }

    private SpriteAnimation getRandomizedSprite(SpriteAnimation sprite) {
        Array<AtlasRegion> regions = sprite.getRegions();
        //backward?
        //TODO NF Rules revamp
        List<ActiveObj> subactions = new ArrayList<>();
        subactions.remove(getActive());
        subactions.removeIf(a ->
                a.isThrow() ||
                        a.getActiveWeapon() != getActive().getActiveWeapon());
        Array<AtlasRegion> newRegions = Atlases.getRegions(
                VisualEnums.WEAPON_ANIM_CASE.NORMAL, subactions.get(RandomWizard.getRandomIndex(subactions))
                , getProjection(ref, getActive()).bool);

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
        boolean flipHor=false;
        if (getProjection(ref, getActive()) == VisualEnums.PROJECTION.HOR) {
            // flipHor = getActive().getOwnerUnit().getFacing() == FACING_DIRECTION.WEST;
            // PositionMaster.isToTheLeft(activeObj.getOwnerUnit(), targetObj);
        } else {
            flipHor = (getProjection(ref, getActive()) == VisualEnums.PROJECTION.TO) != offhand;
            //            if (RandomWizard.chance(33))
            //                flipHor = !flipHor; TODO anim Review - is it viable?
        }
        return flipHor;
    }

    protected SpriteAnimation get3dSprite() {
        VisualEnums.PROJECTION projection = getProjection(ref, getActive());

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

    protected SpriteAnimation createSprite(VisualEnums.PROJECTION projection) {
        return Atlases.getSpriteForAction(getDuration(),
                getActive(), getCase(), projection);
    }

    @Override
    public Vector2 getOffsetOrigin() {
        switch (getProjection(ref, getActive())) {
            case FROM:
                return new Vector2(0, 32);
            case TO:
                return new Vector2(0, -32);
            case HOR:
                return new Vector2(32, 0);
        }
        return super.getOffsetOrigin();
    }

    protected VisualEnums.WEAPON_ANIM_CASE getCase() {
        //        return WEAPON_ANIM_CASE. BLOCKED;

        if (getActive().isFailedLast())
            return VisualEnums.WEAPON_ANIM_CASE.MISS;
        //        return WEAPON_ANIM_CASE.PARRY; counter?


        return VisualEnums.WEAPON_ANIM_CASE.NORMAL;
    }

    public VisualEnums.PROJECTION getProjection() {
        return getProjection(getRef(), getActive());
    }

    public VisualEnums.PROJECTION getProjection(Ref ref, ActiveObj active) {
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
