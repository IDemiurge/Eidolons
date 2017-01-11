package main.libgdx.anims.particles;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;

/**
 * Created by JustMe on 1/8/2017.
 */
public class ParticleAnimation implements ParticleActor{
    @Override
    public PARTICLE_EFFECTS getTemplate() {
        return null;
    }

    @Override
    public ParticleEffect getEffect() {
        return null;
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

    }
    /*
    states: start, run, done
    params: length, delay,
    templates
    args: SFX...effects

     */
}
