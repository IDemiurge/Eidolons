package main.libgdx.anims.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.content.CONTENT_CONSTS2.SFX;
import main.data.filesys.PathFinder;

/**
 * Created by JustMe on 1/10/2017.
 */
public class ParticleEmitter extends Actor implements ParticleActor {

    private final int defaultCapacity=12;
    private final int defaultMaxCapacity=24;
    protected     ParticleEffect effect;
    protected ParticleEffectPool pool;
    protected SFX fx;

    public ParticleEmitter(SFX fx) {
        this.fx = fx;
        effect = new ParticleEffect();
//        pool = new ParticleEffectPool(effect, defaultCapacity, defaultMaxCapacity);
//        pool.obtain() ; TODO
        effect = new ParticleEffect();
        effect.load(Gdx.files.internal(PathFinder.getParticlePresetPath() +
          fx.path),
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
    @Override
    public SFX getTemplate() {
        return fx;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        effect.getEmitters().first().setPosition(getX(), getY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

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
    public void updatePosition(int d) {

    }

    @Override
    public void start() {
        effect.start();
    }
}
