package eidolons.libgdx.particles;

import com.badlogic.gdx.utils.Pool;

public class VfxPool extends Pool<ParticleEffectX> {
    private   ParticleEffectX effect;
    private final String path;
    private int emittersCounter;
    private final boolean logging = false;

    public VfxPool(String path, int initialCapacity, int max) {
        super(initialCapacity, max);
        try {
            this.effect = new ParticleEffectX(path);
            effect.setPool(this);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        this.path = (path);
    }

    protected ParticleEffectX newObject() {
        return new ParticleEffectX(effect);
    }

    @Override
    public ParticleEffectX obtain() {
        emittersCounter++;
        if (logging) {
            main.system.auxiliary.log.LogMaster.important(emittersCounter + "th emitter: " + path);
        }
        return super.obtain();
    }

    public void free(ParticleEffectX effect) {
        super.free(effect);
        effect.reset(false); // copy parameters exactly to avoid introducing error
        // if (effect.xSizeScale != this.effect.xSizeScale || effect.ySizeScale != this.effect.ySizeScale || effect.motionScale != this.effect.motionScale){
        //     Array<ParticleEmitter> emitters = effect.getEmitters();
        //     Array<ParticleEmitter> templateEmitters = this.effect.getEmitters();
        //     for (int i=0; i<emitters.size; i++){
        //         ParticleEmitter emitter = emitters.get(i);
        //         ParticleEmitter templateEmitter = templateEmitters.get(i);
        //         emitter.matchSize(templateEmitter);
        //         emitter.matchMotion(templateEmitter);
        //     }
        //     effect.xSizeScale = this.effect.xSizeScale;
        //     effect.ySizeScale = this.effect.ySizeScale;
        //     effect.motionScale = this.effect.motionScale;
        // }
    }

}
