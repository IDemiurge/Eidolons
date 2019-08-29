package eidolons.libgdx.anims;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.anims.AnimData.ANIM_VALUES;
import eidolons.libgdx.anims.construct.AnimConstructor;
import main.content.VALUE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.VFX;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnit;

import java.awt.*;

/**
 * Created by JustMe on 1/12/2017.
 */
public class AnimData extends DataUnit<ANIM_VALUES> {


//    float duration;
//    float spriteDuration;
//    float emitterDuration;
//    String spriteImagePaths;
//    GenericEnums.VFX[] emitters; //other params?
//    Color[] emitterColors;
//    Point[] emitterOffsets;
//    int[] emitterScales;
//    int lightEmission;
//    Color lightColor;

    public AnimData(String data) {
        super(data);
    }

    public AnimData() {

    }

    @Override
    public void setValue(ANIM_VALUES name, String value) {
        if (!StringMaster.isEmpty(value)) {
            switch (name) {
                case SPRITES:
                    //                    shot in the leg!
                    // main.system.auxiliary.log.LogMaster.log(1,">>>>> SPRITES set" +value +
                    //                     "=>  " );
                    //                    value =
                    //                            StringMaster.addMissingPathSegments(value,
                    //                             PathFinder.getSpritesPathFull());
                    //                    main.system.auxiliary.log.LogMaster.log(1,value  );
                    break;
                case PARTICLE_EFFECTS:
                    //                    value = StringMaster.addMissingPathSegments(value, PathFinder.getSfxPath());
                    break;

            }
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
                value = getPath(val) + value;
                setValue(ANIM_VALUES.SPRITES, value);
                break;
            case ANIM_VFX_CAST:
            case ANIM_VFX_MAIN:
            case ANIM_MISSILE_VFX:
            case ANIM_VFX_IMPACT:
            case ANIM_VFX_RESOLVE:
            case ANIM_VFX_AFTEREFFECT:
                value = checkAlias(value);

                value = PathUtils.cropLastPathSegment(getPath(val)) + value;
                setValue(ANIM_VALUES.PARTICLE_EFFECTS, value);
                break;
        }
    }

    private String checkAlias(String value) {
        String res = "";
        for (String substring : ContainerUtils.openContainer(value)) {
            if (!substring.contains("/")) {
                VFX vfx = new EnumMaster<VFX>().retrieveEnumConst(VFX.class, substring);
                if (vfx != null) {
                    res += vfx.getPath() + ";";
                }
            } else res += substring + ";";
        }
        return res;
    }

    private static String getPath(VALUE val) {
        if (val.getName().toLowerCase().contains("vfx")) {
            return AnimConstructor.getPath(ANIM_VALUES.PARTICLE_EFFECTS);
        }
        return "";
    }

    public enum ANIM_VALUES {
        PARTICLE_EFFECTS, SPRITES,
        DURATION, SCALE, COLOR, LIGHT_FOCUS, LIGHT_AMBIENT,
        FRAME_DURATION, MISSILE_SPEED

    }


}
