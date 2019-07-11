package eidolons.libgdx.anims.sprite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.OverlayView;
import eidolons.libgdx.gui.generic.GroupX;

public class SpriteX extends GroupX {

    SpriteAnimation sprite;
    Fluctuating f;

    float acceleration;
    float pauseAfterCycle;
    float speedRandomness;

    float offsetRangeX;
    float offsetRangeY;
    float scaleRange;

    public SpriteX(String path ) {
        this(path, null, null);
    }
    public SpriteX(String path,SPRITE_TEMPLATE template, Fluctuating.ALPHA_TEMPLATE alphaTemplate) {
        if (alphaTemplate != null) {
            f = new Fluctuating(alphaTemplate);
        }
        if (template != null) {
            acceleration  = template.acceleration;
            pauseAfterCycle  = template.pauseAfterCycle;
            offsetRangeX  = template.offsetRangeX;
            offsetRangeY  = template.offsetRangeY;
            scaleRange  = template.scaleRange;
            speedRandomness  = template.speedRandomness;
        }
        sprite = SpriteAnimationFactory.getSpriteAnimation(path);
    }

    @Override
    public void act(float delta) {

        //TODO center and apply values
        if (getParent() instanceof OverlayView) {
            sprite.centerOnParent(this);
        } else
            sprite.centerOnParent(getParent());
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        sprite.draw(batch);
    }

    @Override
    public float getWidth() {
        return super.getWidth();
    }

    @Override
    public float getHeight() {
        return super.getHeight();
    }

    public void setBlending(SuperActor.BLENDING blending) {
        sprite.setBlending(blending);
    }

    @Override
    public void setRotation(float rotation) {
        sprite.setRotation(rotation);
    }

    @Override
    public void setColor(Color color) {
        sprite.setColor(color);
    }

    @Override
    public void setScale(float scale) {
        sprite.setScale(scale);
    }

    public void setSpeed(float speed) {
        sprite.setSpeed(speed);
    }

    public void setFps(int i) {
        sprite.setFps(i);
    }

    public enum SPRITE_TEMPLATE{
//        WITCH_FLAME,

        ;

        SPRITE_TEMPLATE(float pauseAfterCycle, float speedRandomness, float acceleration, float offsetRangeX, float offsetRangeY, float scaleRange) {
            this.pauseAfterCycle = pauseAfterCycle;
            this.speedRandomness = speedRandomness;
            this.acceleration = acceleration;
            this.offsetRangeX = offsetRangeX;
            this.offsetRangeY = offsetRangeY;
            this.scaleRange = scaleRange;
        }

        float pauseAfterCycle;

        float speedRandomness;

        float acceleration;

        float offsetRangeX;
        float offsetRangeY;
        float scaleRange;
    }
}
