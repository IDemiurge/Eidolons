package main.libgdx.anims;

import com.badlogic.gdx.graphics.Color;
import main.content.CONTENT_CONSTS2.SFX;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.VALUE;
import main.data.filesys.PathFinder;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.system.auxiliary.StringMaster;
import main.system.net.data.DataUnit;

import java.awt.*;

/**
 * Created by JustMe on 1/12/2017.
 */
public class AnimData extends DataUnit<ANIM_VALUES> {


    float duration;
    float spriteDuration;
    float emitterDuration;
    String spriteImagePaths;
    SFX[] emitters; //other params?
    Color[] emitterColors;
    Point[] emitterOffsets;
    int[] emitterScales;
    int lightEmission;
    Color lightColor;
    public AnimData(String... data) {

    }

    @Override
    public void setValue(ANIM_VALUES name, String value) {
        if (!StringMaster.isEmpty(value)) switch (name) {
            case SPRITES:
                value =
                        StringMaster.addMissingPathSegments(value, PathFinder.getSpritesPath());
                break;
            case PARTICLE_EFFECTS:
                value = StringMaster.addMissingPathSegments(value, PathFinder.getSfxPath());
                break;

        }
        super.setValue(name, value);
    }

    public void add(VALUE val, String value) {
        if (val instanceof PARAMS) {
            setParam((PARAMS) val, value);
        }
        if (val instanceof PROPS) {
            setProp((PROPS) val, value);
        }
    }

    private void setParam(PARAMS val, String value) {
        switch (val) {
            case ANIM_SPEED:
                setValue(ANIM_VALUES.MISSILE_SPEED, value);
                break;
            case ANIM_FRAME_DURATION:
                setValue(ANIM_VALUES.FRAME_DURATION, value);
                break;
        }
    }

    private void setProp(PROPS val, String value) {
        //contains? 
        switch (val) {
            case ANIM_SPRITE_CAST:
            case ANIM_SPRITE_RESOLVE:
            case ANIM_SPRITE_MAIN:
            case ANIM_SPRITE_IMPACT:
            case ANIM_SPRITE_AFTEREFFECT:
                setValue(ANIM_VALUES.SPRITES, value);
                break;
            case ANIM_SFX_CAST:
            case ANIM_SFX_MAIN:
            case ANIM_SFX_IMPACT:
            case ANIM_SFX_RESOLVE:
            case ANIM_SFX_AFTEREFFECT:
                setValue(ANIM_VALUES.PARTICLE_EFFECTS, value);
                break;
        }
    }


    public enum ANIM_VALUES {
        PARTICLE_EFFECTS, SPRITES,
        DURATION, SCALE, COLOR, LIGHT_FOCUS, LIGHT_AMBIENT,
        FRAME_DURATION, MISSILE_SPEED

    }


}
