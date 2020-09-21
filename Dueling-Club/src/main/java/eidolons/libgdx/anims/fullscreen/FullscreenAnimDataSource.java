package eidolons.libgdx.anims.fullscreen;

import eidolons.libgdx.anims.fullscreen.FullscreenAnims.FULLSCREEN_ANIM;
import eidolons.libgdx.bf.datasource.SpriteData;
import main.content.enums.GenericEnums;
import main.game.bf.directions.FACING_DIRECTION;

/**
 * Created by JustMe on 12/1/2018.
 */
public class FullscreenAnimDataSource {

    FULLSCREEN_ANIM type;
    float intensity;
    FACING_DIRECTION from;
    private GenericEnums.BLENDING blending = GenericEnums.BLENDING.SCREEN;
    public boolean flipX;
    public boolean flipY;
    private int loops;
    private SpriteData spriteData;

    public FullscreenAnimDataSource(FULLSCREEN_ANIM type, float intensity, FACING_DIRECTION from, GenericEnums.BLENDING blending) {
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

    public GenericEnums.BLENDING getBlending() {
        return blending;
    }

    public void setBlending(GenericEnums.BLENDING blending) {
        this.blending = blending;
    }

    public int getLoops() {
        return loops;
    }

    public void setLoops(int loops) {
        this.loops = loops;
    }

    public void setIntensity(Float valueOf) {
        intensity = valueOf;
    }

    public SpriteData getSpriteData() {
        return spriteData;
    }

    public void setSpriteData(SpriteData spriteData) {
        this.spriteData = spriteData;
    }
}
