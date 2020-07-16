package eidolons.libgdx.anims.anim3d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.assets.AnimMaster3d.PROJECTION;
import eidolons.libgdx.assets.AnimMaster3d.WEAPON_ANIM_CASE;
import eidolons.libgdx.assets.Atlases;
import main.game.bf.Coordinates;

/**
 * Created by JustMe on 9/18/2017.
 * Cyclic
 * created on hover
 * plays atk anim once
 * <p>
 * continuous - add to AnimMaster?
 */
public class Ready3dAnim extends Weapon3dAnim {

    public Ready3dAnim(DC_ActiveObj active) {
        super(active);
    }

    @Override
    public Coordinates getDestinationCoordinates() {
        return super.getOriginCoordinates();
    }

    @Override
    protected SpriteAnimation get3dSprite() {
        PROJECTION projection = getProjection(getRef(), getActive());
        main.system.auxiliary.log.LogMaster.log(1, this + " projection : " + projection);
        sprite = projectionsMap.get(projection);
        if (sprite == null)
            sprite = Atlases.getSpriteForAction(getDuration(),
             getActive(),  WEAPON_ANIM_CASE.READY, projection);

        if (sprite == null)
            main.system.auxiliary.log.LogMaster.log(1, this + " null sprite  ");
        else
            main.system.auxiliary.log.LogMaster.log(1, this + " sprite : " + sprite);

        projectionsMap.put(projection, sprite);
        return sprite;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void updatePosition(float delta) {
        Vector3 pos = GdxMaster.getCursorPosition();
        origin.set(pos.x, pos.y);
        defaultPosition.set(pos.x, pos.y);
        setPosition(pos.x, pos.y);
        super.updatePosition(delta);
//        setX(Gdx.graphics.);
    }

    @Override
    public float getOffsetX() {
        switch (getProjection()) {
            case FROM:
            case TO:
                return 32;
            case HOR:
                return 55;
        }
        return 0;
    }

    @Override
    public float getOffsetY() {
        switch (getProjection()) {
            case FROM:
                return 52;
            case TO:
                return -52;
            case HOR:
                return 35;
        }
        return 0;
    }

    @Override
    public Vector2 getOffsetOrigin() {
        switch (getProjection()) {
            case FROM:
                return new Vector2(32, 52);
            case TO:
                return new Vector2(32, -52);
            case HOR:
                return new Vector2(52, 32);
        }
        return super.getOffsetOrigin();
    }

    protected boolean isContinuous() {
        return true;
    }

    @Override
    protected WEAPON_ANIM_CASE getCase() {
        return WEAPON_ANIM_CASE.READY;
    }
}
