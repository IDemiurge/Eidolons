package libgdx.anims.sprite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import eidolons.content.consts.VisualEnums;
import libgdx.bf.SuperActor;
import libgdx.bf.generic.Flippable;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.shaders.ShaderDrawer;
import libgdx.shaders.ShaderMaster;
import main.content.enums.GenericEnums;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.TimeMaster;


public class SpriteX extends SuperActor implements Flippable, Blended {

    public static final boolean TEST_MODE = true;
    SpriteAnimation sprite;

    float acceleration;
    float pauseAfterCycle;
    float pauseAfterCycleRandomness;
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

    public SpriteX(VisualEnums.SPRITE_TEMPLATE template, GenericEnums.ALPHA_TEMPLATE alphaTemplate, SpriteAnimation sprite
    ) {
        this(null, template, alphaTemplate, GenericEnums.BLENDING.NORMAL);
        this.sprite = sprite;
        initSprite();

    }

    public SpriteX(String path, GenericEnums.BLENDING blending, GenericEnums.ALPHA_TEMPLATE alpha) {
        this(path, null, alpha, blending);
    }

    public SpriteX(String path, VisualEnums.SPRITE_TEMPLATE template,
                   GenericEnums.ALPHA_TEMPLATE alphaTemplate, GenericEnums.BLENDING blending) {
        if (alphaTemplate != null) {
            setAlphaTemplate(alphaTemplate);
        }
        boolean reverse = false;
        if (template != null) {
            acceleration = template.acceleration;
            pauseAfterCycle = template.pauseAfterCycle;
            offsetRangeX = template.offsetRangeX;
            offsetRangeY = template.offsetRangeY;
            scaleRange = template.scaleRange;
            speedRandomness = template.speedRandomness;
            if (template.canBeReverse)
                reverse = RandomWizard.random();
        }
        if (path != null) {
            sprite = SpriteAnimationFactory.getSpriteAnimation(path, true, false);
            sprite.setBlending(blending);
            if (reverse)
                sprite.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
            initSprite();
        }
        if (template != null) {
            float s = RandomWizard.getRandomFloatBetween(1 - scaleRange, 1);
            setScale(s);
            if (template.fps > 0) {
                sprite.setFps(template.fps);
            }
        }
    }

    @Override
    public void fadeIn() {
//        System.out.println("Jeez in " + TimeMaster.getTimeStamp());
        super.fadeIn();
    }

    @Override
    public void fadeOut() {
//        System.out.println("Jeez out " + TimeMaster.getTimeStamp());
        super.fadeOut();
    }

    private void initSprite() {
        sprite.setCustomAct(true);
        sprite.setLoops(0);
        sprite.setLooping(true);
        setOrigin(Align.center);

        sprite.setPauseBetweenCycles(pauseAfterCycle);
        sprite.setPauseBetweenCyclesRandomness(pauseAfterCycleRandomness);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
//        if ()
//        return null;
        Actor hit = super.hit(x, y, touchable);
        if (hit==null)
            return null;
        return hit;
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
        if (isAlphaFluctuationOn()) {
            alphaFluctuation(this , delta);
            sprite.setAlpha(getColor().a);
        }
        setTransform(false);
        sprite.act(delta);
        super.act(delta);
    }

    @Override
    public boolean isAlphaFluctuationOn() {
        return alphaTemplate != null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (sprite == null) {
            return;
        }
        if (shader != null)
            if (parentAlpha != ShaderDrawer.SUPER_DRAW) {
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
        if (!isAlphaFluctuationOn())
            sprite.setAlpha(parentAlpha);

        done = !sprite.draw(batch);
        if (batch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) batch).resetBlending();
        }

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

    public void setPauseAfterCycleRandomness(float pauseAfterCycleRandomness) {
        this.pauseAfterCycleRandomness = pauseAfterCycleRandomness;
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
        if (i == 0) {
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

    @Override
    public GenericEnums.BLENDING getBlending() {
        return sprite.getBlending();
    }


}
