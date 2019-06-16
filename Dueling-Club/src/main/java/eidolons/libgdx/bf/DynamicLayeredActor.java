package eidolons.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.shaders.GrayscaleShader;
import main.data.filesys.PathFinder;
import main.system.auxiliary.RandomWizard;

/**
 * Created by JustMe on 5/8/2018.
 */
public class DynamicLayeredActor extends LayeredActor {


    protected ACTOR_STATUS status;
    protected ShaderProgram shader;
    protected ACTOR_STATUS defaultStatus;
    SpriteActor sprite;

    public DynamicLayeredActor(String rootPath) {
        super(rootPath);
    }

    public DynamicLayeredActor(String rootPath, String overlayPath, String underlayPath) {
        super(rootPath, overlayPath, underlayPath);

        initSprite();
    }

    private void initSprite() {
        addActor(sprite = new SpriteActor(getSpritePathRoot()));
    }

    protected String getSpritePathRoot() {
        return PathFinder.getHqPath() +
                "trees/sprites/";
    }

    //TODO when to call  this?
    protected void playStateAnim() {
        boolean alt = RandomWizard.chance(getAltChance(status));
        sprite.play(getAnimForStatus(status, alt));

    }

    protected int getAltChance(ACTOR_STATUS status) {
        return 0;
    }

    protected SpriteActor.SPRITE_ACTOR_ANIMATION getAnimForStatus(ACTOR_STATUS status, boolean alt) {
        switch (status) {
            case HOVER:
                //TODO on status changed?
                if (alt)
                    return SpriteActor.SPRITE_ACTOR_ANIMATION.FLASH;
                return SpriteActor.SPRITE_ACTOR_ANIMATION.SCUD_OVER;
            case NORMAL:
                if (alt)
                    return SpriteActor.SPRITE_ACTOR_ANIMATION.SCUD_OVER;
                return SpriteActor.SPRITE_ACTOR_ANIMATION.FADE_IN_OUT;
            case DISABLED:
                return SpriteActor.SPRITE_ACTOR_ANIMATION.FADE_IN_OUT;
            case ACTIVE:
                if (alt)
                    return SpriteActor.SPRITE_ACTOR_ANIMATION.FLASH;
                return SpriteActor.SPRITE_ACTOR_ANIMATION.FADE_IN_OUT;
        }
        return null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        sprite.setZIndex(Integer.MAX_VALUE);
        sprite.setX(getSpriteOffsetX());
        sprite.setY(getSpriteOffsetY());
    }

    protected float getSpriteOffsetY() {
        return (sprite. getHeight()-getHeight())/2; //getHeight()/4;
    }

    protected float getSpriteOffsetX() {
        return (sprite.getWidth()-getWidth())/2; //getWidth()/2;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ShaderProgram bufferedShader = null;
        if (shader != null) {
            bufferedShader = batch.getShader();
            batch.setShader(shader);
        }
        super.draw(batch, parentAlpha);

        if (shader != null) {
            batch.setShader(bufferedShader);
        }
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public void setShader(ShaderProgram shader) {
        this.shader = shader;
    }

    public void setStatus(ACTOR_STATUS status) {
        this.status = status;
        if (status != ACTOR_STATUS.HOVER) {
            defaultStatus = status;
        }
//       TODO  setOverlayImage(getImageVariant(" " + status));
//        setUnderlayImage(getImageVariant(" " + status));

        if (status == ACTOR_STATUS.NORMAL) {
            setShader(getShader());
        } else {
            if (status== ACTOR_STATUS.DISABLED) {
                setShader(DarkShader.getDarkShader());
            } else
            if (status== ACTOR_STATUS.ACTIVE) {
//TODO
            } else
            setShader(null);
        }
    }

    public void clearImage() {
        image.setEmpty();
    }

    public void disable() {
        setStatus(ACTOR_STATUS.DISABLED);
        setShader(DarkShader.getDarkShader());
    }

    public void block() {
        setStatus(ACTOR_STATUS.DISABLED);
        setShader(GrayscaleShader.getGrayscaleShader());
    }

    public void available() {
        setStatus(ACTOR_STATUS.ACTIVE);
    }

    public void enable() {
        if (status==ACTOR_STATUS.NORMAL)
            return;
        setStatus(ACTOR_STATUS.NORMAL);
        //TODO igg demo fix
//        playStateAnim();
    }

    public enum ACTOR_STATUS {
        HOVER, NORMAL, DISABLED, ACTIVE,
    }
}
