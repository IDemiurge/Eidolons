package eidolons.libgdx.particles.special;

import eidolons.libgdx.particles.ambi.Ambience;
import main.content.enums.GenericEnums;

/**
 * Created by JustMe on 7/20/2018.
 */
public class MistScreen {

    int width;
    int height;

    public MistScreen(int width, int height) {
        this.width = width;
        this.height = height;

         new Ambience(GenericEnums.VFX.MIST_TRUE);
    }
}
