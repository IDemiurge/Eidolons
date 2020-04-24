package eidolons.libgdx.anims.sprite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.shaders.ShaderMaster;
import main.content.enums.GenericEnums;


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
    private boolean flipY;
    private boolean done;
    private ShaderProgram shader;

    public SpriteX() {
    }

    public SpriteX(String path) {
        this(path, null, null);
    }

    public SpriteX(String path, SPRITE_TEMPLATE template, GenericEnums.ALPHA_TEMPLATE alphaTemplate) {
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
            sprite.setLoops(0);
            sprite.setLooping(true);
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
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
        if (sprite == null) {
            return;
        }
        if (shader != null)
        if (parentAlpha!= ShaderDrawer.SUPER_DRAW)
        {
            ShaderDrawer.drawWithCustomShader(this, batch,
                    shader, true);
            return;
        }

        super.draw(batch, parentAlpha);

        if (fps > 0) {
            sprite.setFps(fps);
        }
        sprite.setX(getX());
        sprite.setY(getY());
        sprite.setRotation(getRotation());

        sprite.setFlipX(flipX);
        sprite.setFlipY(flipY);
        sprite.setColor(getColor());
        sprite.setAlpha(parentAlpha);
        done = !sprite.draw(batch);
//        sprite.setFlipX(true);
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
        if (sprite == null) {
            return 0;
        }
        return sprite.getWidth();
    }

    @Override
    public float getHeight() {
        return sprite.getHeight();
    }

    public void setBlending(GenericEnums.BLENDING blending) {
        sprite.setBlending(blending);
    }

    @Override
    public void setRotation(float rotation) {
        super.setRotation(rotation);
    }

    @Override
    public void rotateBy(float amountInDegrees) {
        super.rotateBy(amountInDegrees);
    }

    @Override
    public void setOrigin(float originX, float originY) {
        sprite.setOrigin(originX, originY);
    }

    @Override
    public float getRotation() {
        return super.getRotation();
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
        if (i==0){
            return;
        }
        this.fps = i;
        if (sprite != null) {
            sprite.setFps(i);
        }
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }
    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }

    public void setShader(ShaderMaster.SHADER shader) {
        this.shader = ShaderMaster.getShader(shader);
    }

    public void setOnCycle(Runnable o) {
        getSprite().setOnCycle(o);
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
