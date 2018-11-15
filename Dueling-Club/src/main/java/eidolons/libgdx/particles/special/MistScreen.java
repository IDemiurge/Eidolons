package eidolons.libgdx.particles.special;

import eidolons.libgdx.particles.ambi.Ambience;
import eidolons.libgdx.particles.EMITTER_PRESET;

/**
 * Created by JustMe on 7/20/2018.
 */
public class MistScreen {

    int width;
    int height;

    public MistScreen(int width, int height) {
        this.width = width;
        this.height = height;

         new Ambience(EMITTER_PRESET.MIST_TRUE);
    }
}
