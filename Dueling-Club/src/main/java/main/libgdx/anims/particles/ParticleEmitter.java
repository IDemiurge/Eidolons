package main.libgdx.anims.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.content.CONTENT_CONSTS2.SFX;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 1/10/2017.
 */
public class ParticleEmitter extends Actor implements ParticleInterface {

    private final int defaultCapacity = 12;
    private final int defaultMaxCapacity = 24;
    protected ParticleEffect effect;
    protected ParticleEffectPool pool;
    protected SFX fx;
    boolean flipX;
    boolean flipY;
    private boolean bound = true;
    private Sprite sprite;


    public ParticleEmitter(SFX fx) {
        this(fx.path);
        this.fx = fx;
//        effect.setFlip(flipX, flipY);
//        effect.getEmitters().get(0).setSprite();
    }

    public ParticleEmitter(String path) {
        effect = new ParticleEffect();
//        pool = new ParticleEffectPool(effect, defaultCapacity, defaultMaxCapacity);
//        pool.obtain() ; TODO
        effect = new ParticleEffect();
        effect.load(Gdx.files.internal(
                StringMaster.addMissingPathSegments(
                        path,
                        PathFinder.getParticlePresetPath())),
                Gdx.files.internal(PathFinder.getParticleImagePath()));

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
        if (bound)
            setPosition(x, y);
        else {
            //its own vector
        }
    }

    @Override
    public SFX getTemplate() {
        return fx;
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

}
