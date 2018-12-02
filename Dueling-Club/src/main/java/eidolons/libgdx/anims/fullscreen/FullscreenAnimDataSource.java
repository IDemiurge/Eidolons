package eidolons.libgdx.anims.fullscreen;

import eidolons.libgdx.anims.fullscreen.FullscreenAnims.FULLSCREEN_ANIM;
import main.game.bf.directions.FACING_DIRECTION;

/**
 * Created by JustMe on 12/1/2018.
 */
public class FullscreenAnimDataSource {

    FULLSCREEN_ANIM type;
    float intensity;
    FACING_DIRECTION from;

    public FullscreenAnimDataSource(FULLSCREEN_ANIM type, float intensity, FACING_DIRECTION from) {
        this.type = type;
        this.intensity = intensity;
        this.from = from;
    }

    public FULLSCREEN_ANIM getType() {
        return type;
    }

    public float getIntensity() {
        return intensity;
    }

    public FACING_DIRECTION getFrom() {
        return from;
    }
}
