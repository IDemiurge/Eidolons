package main.libgdx.anims.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.content.CONTENT_CONSTS2.SFX;
import main.data.filesys.PathFinder;
import main.game.battlefield.Coordinates;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 1/10/2017.
 */
public class EmitterActor extends Actor implements ParticleInterface {

    private final int defaultCapacity = 12;
    private final int defaultMaxCapacity = 24;
    public String path;
    protected ParticleEffect effect;
    protected ParticleEffectPool pool;
    protected SFX sfx;
    boolean flipX;
    boolean flipY;
    private Sprite sprite;
    private boolean attached=true;
    private boolean generated;
    private Coordinates target;


    public EmitterActor(SFX fx) {
        this(fx.path);
        this.sfx = fx;
//        effect.setFlip(flipX, flipY);
//        effect.getEmitters().get(0).setSprite();
    }

    public EmitterActor(String path) {
        this.path=path;
        effect = new ParticleEffect();
//        pool = new ParticleEffectPool(effect, defaultCapacity, defaultMaxCapacity);
//        pool.obtain() ; TODO
        effect = new ParticleEffect();
        String imagePath = PathFinder.getParticleImagePath();

        try {
            effect.load(Gdx.files.internal(
             StringMaster.addMissingPathSegments(
              path, PathFinder.getParticlePresetPath())),
             Gdx.files.internal(imagePath));

        } catch (Exception e) {
            String suffix = StringMaster.replaceFirst(path, PathFinder.getParticlePresetPath(), "");
            suffix = StringMaster.cropLastPathSegment(suffix);
            imagePath += suffix;
        try {
            effect.load(Gdx.files.internal(
             StringMaster.addMissingPathSegments(
              path, PathFinder.getParticlePresetPath())),
             Gdx.files.internal(imagePath));

        } catch (Exception e0) {
            imagePath += "particles\\";
            try {
                effect.load(Gdx.files.internal(
                 StringMaster.addMissingPathSegments(
                  path, PathFinder.getParticlePresetPath())),
                 Gdx.files.internal(imagePath));
            } catch (Exception e1) {
                main.system.auxiliary.LogMaster.log(1, imagePath + " - NO IMAGE FOUND FOR SFX: " + path);
                e.printStackTrace();
            }
        }

        }

//        m_effect = new ParticleEffect();
//        m_effect.load(Gdx.files.internal("particle/effects/lightning.p"), this.getAtlas());
//
//        m_effect.loadEmitters(Gdx.files.internal("particle/effects/lightning.p"));
//        m_effect.loadEmitterImages(this.getAtlas());
    }

    public void act(float delta) {
        super.act(delta);
//        effect.setPosition(x, y);
        effect.update(delta);

    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }

    public void updatePosition(float x, float y) {
        setPosition(x, y);
    }

    @Override
    public SFX getTemplate() {
        return sfx;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        effect.getEmitters().first().setPosition(getX(), getY());
//        sprite = effect.getEmitters().first().getSprite();
//        sprite.setRotation(new Random().nextInt(360));
        effect.draw(batch);

    }

    @Override
    public ParticleEffect getEffect() {
        return effect;
    }

    @Override
    public boolean isContinuous() {
        return false;
    }

    @Override
    public boolean isRunning() {
        return false;
    }


    @Override
    public void start() {
        effect.start();
    }

    public SFX getSfx() {
        return sfx;
    }

    public void setSfx(SFX sfx) {
        this.sfx = sfx;
    }

    public boolean isAttached() {
        return attached;
    }

    public void setAttached(boolean attached) {
        this.attached = attached;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setTarget(Coordinates target) {
        this.target = target;
    }

    public Coordinates getTarget() {
        return target;
    }
}
