package eidolons.libgdx.anims.particles;

import eidolons.libgdx.anims.particles.Emitter.EMITTER_VALS_SCALED;

/**
 * Created by JustMe on 3/16/2018.
 * <p>
 * <p>
 * Fullscreen emitter transform
 * <p>
 * emitter mods
 * <p>
 * move emitters (add gdx behavior?) steerableEmitter...
 */
public class EmitterMaster {

    public void applyMod(String filter, String path, EMITTER_VALS_SCALED val, float mod) {

    }

    public void applyMod(ParticleEffect effect) {

//        EmitterPresetMaster.getInstance().getModifiedEmitter()

    }

    public void createSfxAtlas() {

    }

    public enum SFX_TEMPLATE {
        CENTER, SWIRL, FADE, FLOW,
        MISSILE, WHIRL,
    }
}
