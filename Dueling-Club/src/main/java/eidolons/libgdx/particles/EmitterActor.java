package eidolons.libgdx.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.screens.CustomSpriteBatch;
import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;
import main.system.PathUtils;

/**
 * Created by JustMe on 1/10/2017.
 */
public class EmitterActor extends SuperActor {

    public String path;
    protected ParticleEffectX effect;
    protected GenericEnums.VFX sfx;
    protected Sprite sprite;
    protected boolean attached = true;
    protected boolean generated;
    protected Coordinates target;
    protected boolean test;
    protected float speed = 1;
    protected Float lastAlpha;
    protected boolean broken;
    boolean flipX;
    boolean flipY;

    private ShaderProgram shader;

    private boolean invert;
    public void init() {
        effect = EmitterPools.getEffect(path);
        if (PathUtils.getLastPathSegment(PathUtils.cropLastPathSegment(path)).contains("invert")) {
            {
                setInvert(true);
            }
//        shader
        }
    }
    public EmitterActor(GenericEnums.VFX fx) {
        this(fx.getPath());
        this.sfx = fx;
    }

    public EmitterActor(String path, boolean test) { //TODO refactor!
        this(path);
        this.test = test;
    }

    public EmitterActor(String path) {
        this.path = path;
        init();
    }

    public void updatePosition(float x, float y) {
//        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.VERBOSE_CHECK, this + " from " +
//                getX() +" " +getY() +" pos set to " + x + " " + y);
        setPosition(x, y);
    }

    public void offsetAlpha(float alpha) {
        if (alpha == 0)
            hide();
        if (lastAlpha != null)
            effect.getEmitters().forEach(e ->
                    e.getTransparency().scale(1 / lastAlpha));

        lastAlpha = alpha;
        effect.getEmitters().forEach(e -> //e.getTransparency().setScaling()
                e.getTransparency().scale(alpha));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (broken) {
            return;
        }
        if (effect instanceof DummyParticleEffectX) {
            broken = true;
        }
//        super.draw(batch, parentAlpha);
        effect.setPosition(getX(), getY());
        float delta = Gdx.graphics.getDeltaTime() * speed;

        boolean reset = false;
        if (shader != null) {
            batch.setShader(shader);
            reset = true;
        }
        if (isInvert()) {
            Gdx.gl.glBlendEquation(GL20.GL_FUNC_REVERSE_SUBTRACT);
            reset = true;

        }
//main.system.auxiliary.log.LogMaster.dev("vfx drawn " +path);
        effect.draw(batch, delta);
        if (reset) {
            if (batch instanceof CustomSpriteBatch) {
                ((CustomSpriteBatch) batch).resetBlending();
            }
        }
    }

    public ParticleEffectX getEffect() {
        return effect;
    }

    public void start() {
        effect.start();
    }

    public GenericEnums.VFX getTemplate() {
        return sfx;
    }

    public void hide() {
        effect.getEmitters().forEach(e ->
        {
            e.allowCompletion();
        });
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }

    @Override
    public String toString() {
        if (path != null)
            return "emitter: " + path;
        if (sfx != null)
            return "emitter: " + sfx.getPath();
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EmitterActor) {
            EmitterActor e = ((EmitterActor) obj);
            if (e.getPath().equals(getPath()) || e.getEffect().equals(getEffect())) {
                if (getX() == e.getX())
                    if (getY() == e.getY())
                        return true;
            }

        }
        return super.equals(obj);
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public boolean isInvert() {
        return invert;
    }

    @Override
    public boolean isIgnored() {
        return super.isIgnored();
    }

    public boolean isAttached() {
        return attached;
    }

    public void setAttached(boolean attached) {
        this.attached = attached;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public Coordinates getTarget() {
        return target;
    }

    public void setTarget(Coordinates target) {
        this.target = target;
    }

    public void reset() {
        getEffect().reset();

    }

    public String getPath() {
        return path;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isComplete() {
        for (ParticleEmitter emitter : getEffect().getEmitters()) {
            if (!emitter.isComplete())
                return false;
        }
        return true;
    }

    public void allowFinish() {
        getEffect().allowCompletion();
    }

    public void setInvert(boolean invert) {
        this.invert = invert;
    }
}
