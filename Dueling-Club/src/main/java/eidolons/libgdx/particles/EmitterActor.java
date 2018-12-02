package eidolons.libgdx.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.particles.util.EmitterPresetMaster;
import main.game.bf.Coordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 1/10/2017.
 */
public class EmitterActor extends SuperActor {

    static public boolean spriteEmitterTest = false;
    private static List<String> brokenPaths = new ArrayList<>();
    protected final int defaultCapacity = 12;
    protected final int defaultMaxCapacity = 24;
    public String path;
    protected ParticleEffectX effect;
    protected ParticleEffectPool pool;
    protected VFX sfx;
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
    private String imgPath;

    public EmitterActor(VFX fx) {
        this(fx.getPath());
        this.sfx = fx;
    }

    public EmitterActor(String path, boolean test) { //TODO refactor!
        this(path);
        this.test = test;
    }

    public EmitterActor(String path) {
        //        path =PathFinder.getVfxPath() + "templates/sprite test";
        this.path = path;
        effect = EmitterPools.getEffect(path);
        //TODO not very safe...
        //        if (EmitterMaster.getAtlasType(path)!= VFX_ATLAS.UNIT) {
        //            effect.getEmitters().forEach(emitter -> emitter.setAdditive(true));
        //        }
        imgPath = EmitterPresetMaster.getInstance().getImagePath(path);
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

    @Override
    public boolean isIgnored() {
        return super.isIgnored();
    }

    public void act(float delta) {
        super.act(delta);
        effect.setPosition(getX(), getY());
        //        effect.update(delta); TODO now drawing with alpha!

    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }

    public void updatePosition(float x, float y) {
        main.system.auxiliary.log.LogMaster.log(1, this + " from " +
         getX() +
         " " +
         getY() +
         " pos set to " + x + " " + y);
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

    public VFX getTemplate() {
        return sfx;
    }

    public void hide() {
        effect.getEmitters().forEach(e ->
        {
            e.allowCompletion();
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (broken) {
            return;
        }
        super.draw(batch, parentAlpha);
        effect.setPosition(getX(), getY());
        float delta = Gdx.graphics.getDeltaTime() * speed;
        if (ParticleEffectX.TEST_MODE)
            try {
                effect.draw(batch, delta);
            } catch (IllegalStateException e) {
                imgPath = EmitterPresetMaster.getInstance().getImagePath(path);
                brokenPaths.add(imgPath);
                main.system.auxiliary.log.LogMaster.log(1, "VFX imgPath is broken! " + imgPath);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                main.system.auxiliary.log.LogMaster.log(1, " EMITTER FAILED: "
                 + path);
                broken = true;
            }
        else
            effect.draw(batch, delta);
    }

    public ParticleEffectX getEffect() {
        return effect;
    }

    public void start() {
        effect.start();
    }

    public VFX getSfx() {
        return sfx;
    }

    public void setSfx(VFX sfx) {
        this.sfx = sfx;
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
}
