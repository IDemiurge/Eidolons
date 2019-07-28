package eidolons.libgdx.anims.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.OverlayView;
import eidolons.libgdx.gui.generic.GroupX;

import static com.badlogic.gdx.graphics.GL20.*;
import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL20.GL_NICEST;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.*;

public class SpriteX extends GroupX {

    SpriteAnimation sprite;
    Fluctuating f;

    float acceleration;
    float pauseAfterCycle;
    float speedRandomness;

    float offsetRangeX;
    float offsetRangeY;
    float scaleRange;
    private int fps;
    private boolean flipX;
    private boolean done;

    public SpriteX() {
    }

    public SpriteX(String path) {
        this(path, null, null);
    }

    public SpriteX(String path, SPRITE_TEMPLATE template, Fluctuating.ALPHA_TEMPLATE alphaTemplate) {
        if (alphaTemplate != null) {
            f = new Fluctuating(alphaTemplate);
        }
        if (template != null) {
            acceleration = template.acceleration;
            pauseAfterCycle = template.pauseAfterCycle;
            offsetRangeX = template.offsetRangeX;
            offsetRangeY = template.offsetRangeY;
            scaleRange = template.scaleRange;
            speedRandomness = template.speedRandomness;
        }
        if (path != null) {
            sprite = SpriteAnimationFactory.getSpriteAnimation(path, true, false);
            sprite.setCustomAct(true);
        }
    }

    public void setSprite(String path) {
        sprite = SpriteAnimationFactory.getSpriteAnimation(path, false, false);
    }

    public void setSprite(SpriteAnimation sprite) {
        this.sprite = sprite;
        if (sprite != null) {
            sprite.setCustomAct(true);
        }
    }

    public SpriteAnimation getSprite() {
        return sprite;
    }

    @Override
    public void act(float delta) {

        //TODO center and apply values
        if (sprite == null) {
            return;
        }
        sprite.act(delta);
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        Gdx.gl20.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        Gdx.gl20.glEnable(GL_BLEND);
//        Gdx.gl20.glEnable(GL_POINT_SMOOTH);
//        Gdx.gl20.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
//        Gdx.gl20.glEnable(GL_LINE_SMOOTH);
//        Gdx.gl20.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
//        Gdx.gl20.glEnable(GL_POLYGON_SMOOTH);
//        Gdx.gl20.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        super.draw(batch, parentAlpha);
        if (sprite == null) {
            return;
        }
        if (fps > 0) {
            sprite.setFps(fps);
        }
        sprite.setX(getX());
        sprite.setY(getY());
        if (getParent() instanceof BaseView) {
            switch (((BaseView) getParent()).getUserObject().getName()) {
                case "Eldritch Sphere":
                    getSprite().setOffsetX( getWidth()-128+32);
                    getSprite().setOffsetY( getHeight()-128);
                    break;
            }
            if (getParent() instanceof OverlayView) {

                sprite.centerOnParent(this);
            } else {
//                sprite.centerOnParent(getParent()); TODO
            }
        }

        sprite.setFlipX(flipX);
        sprite.setColor(getColor());
        done = !sprite.draw(batch);
        sprite.setFlipX(true);
//        debug();
//        Gdx.gl20.glDisable(GL_BLEND);
//        Gdx.gl20.glDisable(GL_LINE_SMOOTH);
//        Gdx.gl20.glDisable(GL_POINT_SMOOTH);
//        Gdx.gl20.glDisable(GL_POLYGON_SMOOTH);
    }

    public boolean draw(Batch batch) {
        draw(batch, 1f);
        return !done;
    }

    @Override
    public float getWidth() {
        return sprite.getWidth();
    }

    @Override
    public float getHeight() {
        return sprite.getHeight();
    }

    public void setBlending(SuperActor.BLENDING blending) {
        sprite.setBlending(blending);
    }

    @Override
    public void setRotation(float rotation) {
        sprite.setRotation(rotation);
    }


    @Override
    public void setOrigin(float originX, float originY) {
        sprite.setOrigin(originX, originY);
    }

    @Override
    public float getRotation() {
        return sprite.getRotation();
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
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
        this.fps = i;
        if (sprite != null) {
            sprite.setFps(i);
        }
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }


    public enum SPRITE_TEMPLATE {
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
