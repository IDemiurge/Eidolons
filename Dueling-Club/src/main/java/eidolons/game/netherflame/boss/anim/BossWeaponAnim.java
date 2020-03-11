package eidolons.game.netherflame.boss.anim;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.anims.anim3d.AnimMaster3d;
import eidolons.libgdx.anims.anim3d.Weapon3dAnim;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.texture.TexturePackerLaunch;
import main.entity.Ref;
import main.game.bf.Coordinates;

public class BossWeaponAnim extends Weapon3dAnim {
    public BossWeaponAnim(DC_ActiveObj active) {
        super(active);
    }

    @Override
    protected void resetSprites() {
        super.resetSprites();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public boolean draw(Batch batch) {
        sprite.draw(batch);
        return !sprite.isAnimationFinished();
    }

    @Override
    protected SpriteAnimation createSprite(AnimMaster3d.PROJECTION projection) {
        String spritePath = AnimMaster.getInstance().getBossAnimator().getRoot() +"attack/"+
                getActive().getName() + TexturePackerLaunch.ATLAS_EXTENSION;

        sprite =  SpriteAnimationFactory.getSpriteAnimation(spritePath);
        return sprite;
    }

    @Override
    public float getDefaultSpeed() {
        return super.getDefaultSpeed();
    }

    @Override
    public Coordinates getDestinationCoordinates() {
        return super.getOriginCoordinates();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public String getTexturePath() {
        return super.getTexturePath();
    }

    @Override
    protected boolean checkFlipHorizontally() {
        return super.checkFlipHorizontally();
    }

    @Override
    public Vector2 getOffsetOrigin() {
        return super.getOffsetOrigin();
    }

    @Override
    public AnimMaster3d.PROJECTION getProjection(Ref ref, DC_ActiveObj active) {
        return super.getProjection(ref, active);
    }
}
