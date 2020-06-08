package eidolons.libgdx.utils.mixer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.bf.SuperActor;

public class SpriteMask extends SuperActor {
    /*
    encapsulate drawing of something masked by something else ...
         */

    SpriteAnimation mask;
    float maskX;
    float maskY;
    float maskW;
    float maskH;
    private final boolean followCursor;
    private OrthographicCamera camera;

    public SpriteMask(SpriteAnimation mask, boolean followCursor,  OrthographicCamera camera) {
        this.mask = mask;
        maskW = mask.getWidth();
        maskH = mask.getHeight();
        this.followCursor = followCursor;
        this.camera = camera;
    }

    @Override
    public void act(float delta) {
        if (followCursor) {

            maskX =camera.position.x+ Gdx.input.getX();
            maskY =camera.position.y+ Gdx.input.getY();
        }
        super.act(delta);
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setMaskingOn(batch);
        batch.draw(mask.getCurrentFrame(), maskX, maskY, maskW, maskH);
        setMaskingOff(batch);
        super.draw(batch, parentAlpha);
        batch.flush();
    }

    private void setMaskingOff(Batch batch) {
        batch.flush();
        Gdx.gl.glColorMask(true, true, true, true);
        batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);

    }

    private void setMaskingOn(Batch batch) {
        batch.flush();
        Gdx.gl.glColorMask(false, false, false, true);
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
    }

}
