package eidolons.libgdx.anims.fullscreen;

import eidolons.libgdx.anims.fullscreen.FullscreenAnims.FULLSCREEN_ANIM;
import eidolons.libgdx.bf.SuperActor;
import main.game.bf.directions.FACING_DIRECTION;

/**
 * Created by JustMe on 12/1/2018.
 */
public class FullscreenAnimDataSource {

    FULLSCREEN_ANIM type;
    float intensity;
    FACING_DIRECTION from;
    private SuperActor.BLENDING blending;

    public FullscreenAnimDataSource(FULLSCREEN_ANIM type, float intensity, FACING_DIRECTION from, SuperActor.BLENDING blending) {
        this.type = type;
        this.intensity = intensity;
        this.from = from;
        this.blending = blending;
    }

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

    public SuperActor.BLENDING getBlending() {
        return blending;
    }

    public void setBlending(SuperActor.BLENDING blending) {
        this.blending = blending;
    }
}
