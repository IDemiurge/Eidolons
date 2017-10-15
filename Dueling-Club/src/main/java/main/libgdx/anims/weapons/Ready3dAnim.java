package main.libgdx.anims.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import main.entity.active.DC_ActiveObj;
import main.game.bf.Coordinates;
import main.libgdx.GdxMaster;
import main.libgdx.anims.AnimMaster3d;
import main.libgdx.anims.AnimMaster3d.PROJECTION;
import main.libgdx.anims.AnimMaster3d.WEAPON_ANIM_CASE;
import main.libgdx.anims.sprite.SpriteAnimation;

/**
 * Created by JustMe on 9/18/2017.
 Cyclic
 created on hover
 plays atk anim once

 continuous - add to AnimMaster?
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
        PROJECTION projection= getProjection();
        sprite = projectionsMap.get(projection );
        if (sprite  != null) {
            return sprite;
        }
        sprite =  AnimMaster3d.getSpriteForAction(getDuration(),
         getActive(),  ref.getTargetObj(),   WEAPON_ANIM_CASE.READY,  projection );
        projectionsMap.put(projection, sprite);
        return sprite;
    }

    @Override
    protected Texture getTexture() {
        return null ;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void updatePosition(float delta) {
        Vector3 pos = GdxMaster.getCursorPosition() ;
        origin.set(pos.x, pos.y);
        defaultPosition.set(pos.x, pos.y);
        setPosition(pos.x,pos.y);
        super.updatePosition(delta);
//        setX(Gdx.graphics.);
    }

    @Override
    public float getOffsetX() {
        switch(getProjection()){
            case FROM:
                return 32;
            case TO:
                return 32;
            case HOR:
                return 55;
        }
        return  0;
    }

    @Override
    public float getOffsetY() {
        switch(getProjection()){
            case FROM:
                return 52;
            case TO:
                return -52;
            case HOR:
                return 35;
        }
        return  0;
    }

    @Override
    public Vector2 getOffsetOrigin() {
        switch(getProjection()){
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
        return  WEAPON_ANIM_CASE.READY;
    }
}