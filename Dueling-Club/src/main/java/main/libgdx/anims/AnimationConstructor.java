package main.libgdx.anims;

import main.ability.effects.Effect;
import main.content.VALUE;
import main.entity.obj.top.DC_ActiveObj;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by JustMe on 1/11/2017.
 */
public class AnimationConstructor {
    VALUE[] anim_vals = {
//     PROPS.ANIM_MODS,
//
//     PROPS.ANIM_SPRITE_CAST,
//     PROPS.ANIM_SPRITE_RESOLVE,
//     PROPS.ANIM_SPRITE_MAIN,
//     PROPS.ANIM_SPRITE_IMPACT,
//     PROPS.ANIM_SPRITE_AFTEREFFECT,
//     PROPS.ANIM_MODS_SPRITE,
//
//     PROPS.ANIM_SFX_CAST,
//     PROPS.ANIM_SFX_RESOLVE,
//     PROPS.ANIM_SFX_MAIN,
//     PROPS.ANIM_SFX_IMPACT,
//     PROPS.ANIM_SFX_AFTEREFFECT,
//     PROPS.ANIM_MODS_SFX,
//
//
//     PROPS.ANIM_SPRITE_COLOR,
//     PROPS.ANIM_SFX_COLOR,
//     PROPS.ANIM_LIGHT_COLOR,
//
//     PROPS.ANIM_MISSILE_SPRITE,
//     PROPS.ANIM_MISSILE_SFX,
//
//     PROPS.ANIM_LIGHT_CAST,
//     PROPS.ANIM_LIGHT_RESOLVE,
//     PROPS.ANIM_LIGHT_MAIN,
//     PROPS.ANIM_LIGHT_IMPACT,
//     PROPS.ANIM_LIGHT_AFTEREFFECT,
//
//     PARAMS.ANIM_LIGHT_MISSILE,
//     PARAMS.ANIM_LIGHT_CASTER,
//     PARAMS.ANIM_LIGHT_TARGET,
//
//     PARAMS.ANIM_MAGNITUDE,
//     PARAMS.ANIM_SPEED,
//     PARAMS.ANIM_SIZE,
    };
    Map<DC_ActiveObj, CompositeAnim> map;

    CompositeAnim construct(DC_ActiveObj active) {
        //re-construct sometimes?
        CompositeAnim anim = new CompositeAnim(active);
//        active.getActionGroup()

        Arrays.stream(ANIM_PART.values()).forEach(part -> {
            getAnimPart(active, part);
        });

        map.put(active, anim);
        return anim;
    }

    private Anim getEffectAnim(Effect e) {
        return null;
    }

    private Anim getAnimPart(DC_ActiveObj active, ANIM_PART part) {

        String sfx = "ANIM_SFX_" + part.name();
        String sprite = "ANIM_SPRITE_" + part.name();
        String light = "ANIM_LIGHT_" + part.name();
        active.getProperty(sfx);

        AnimData data = new AnimData();
        for (VALUE val : anim_vals) {
            data.add(val, active.getValue(val));
        }
        Anim anim = new Anim(active, data);

        return anim;
    }


    public enum ANIM_PART {
        CAST,
        RESOLVE,
        MAIN,
        IMPACT,
        AFTEREFFECT;
    }


}
