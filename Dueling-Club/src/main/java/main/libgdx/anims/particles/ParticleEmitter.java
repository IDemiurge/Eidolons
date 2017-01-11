package main.libgdx.anims.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.data.filesys.PathFinder;

/**
 * Created by JustMe on 1/10/2017.
 */
public class ParticleEmitter extends Actor implements ParticleActor {

    private final int defaultCapacity=12;
    private final int defaultMaxCapacity=24;
    protected     ParticleEffect effect;
    protected ParticleEffectPool pool;
    protected PARTICLE_EFFECTS fx;

    public ParticleEmitter(PARTICLE_EFFECTS fx) {
        this.fx = fx;
        effect = new ParticleEffect();
//        pool = new ParticleEffectPool(effect, defaultCapacity, defaultMaxCapacity);
//        pool.obtain() ; TODO
        effect = new ParticleEffect();
        effect.load(Gdx.files.internal(PathFinder.getParticlePresetPath() +
          fx.path),
         Gdx.files.internal(PathFinder.getParticlePresetPath()));
    }

    public void act(float delta) {
        super.act(delta);
//        effect.setPosition(x, y);
        effect.update(delta);
    }
    @Override
    public PARTICLE_EFFECTS getTemplate() {
        return fx;
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
