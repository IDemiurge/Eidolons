package main.libgdx.anims;

import main.ability.effects.Effect;
import main.content.CONTENT_CONSTS2.SFX;
import main.content.PROPS;
import main.content.VALUE;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.top.DC_ActiveObj;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.particles.ParticleEmitter;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.*;

import static com.sun.jmx.snmp.EnumRowStatus.active;

/**
 * Created by JustMe on 1/11/2017.
 */
public class AnimationConstructor {
    VALUE[] anim_vals = {
//     PROPS.ANIM_MODS,
//
            PROPS.ANIM_SPRITE_CAST,
            PROPS.ANIM_SPRITE_RESOLVE,
            PROPS.ANIM_SPRITE_MAIN,
            PROPS.ANIM_SPRITE_IMPACT,
            PROPS.ANIM_SPRITE_AFTEREFFECT,
            PROPS.ANIM_MISSILE_SPRITE,
            PROPS.ANIM_MODS_SPRITE,
            PROPS.ANIM_MISSILE_SFX,
//
            PROPS.ANIM_SFX_CAST,
            PROPS.ANIM_SFX_RESOLVE,
            PROPS.ANIM_SFX_MAIN,
            PROPS.ANIM_SFX_IMPACT,
            PROPS.ANIM_SFX_AFTEREFFECT,
            PROPS.ANIM_MODS_SFX,
//
//
//     PROPS.ANIM_SPRITE_COLOR,
//     PROPS.ANIM_SFX_COLOR,
//     PROPS.ANIM_LIGHT_COLOR,
//
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
    Map<DC_ActiveObj, CompositeAnim> map = new HashMap<>();
    boolean reconstruct = true;

    public CompositeAnim getOrCreate(ActiveObj active) {
        CompositeAnim anim = map.get(active);
        if (!reconstruct)
            if (anim != null) return anim;
        return construct((DC_ActiveObj) active);
    }

    public CompositeAnim getOrCreate(Ref ref, AnimData data) {
        CompositeAnim anim = map.get(active);
        if (anim != null) return anim;
        return construct(ref, data, null);
    }

    CompositeAnim construct(DC_ActiveObj active) {
        return construct(active.getRef(), null, active);
    }

    private CompositeAnim construct(Ref ref, AnimData data, DC_ActiveObj active) {

        //re-construct sometimes?
        CompositeAnim anim = new CompositeAnim(active);
//        active.getActionGroup()

        Arrays.stream(ANIM_PART.values()).forEach(part -> {
            Anim animPart = getAnimPart(active, part);
            if (animPart != null)
                anim.add(part, animPart);
        });

        map.put(active, anim);
        return anim;
    }

    private Anim getEffectAnim(Effect e) {
        return null;
    }

    private Anim getAnimPart(DC_ActiveObj active, ANIM_PART part) {
//        active.getProperty(sfx);
        AnimData data = new AnimData();
        for (VALUE val : anim_vals) {
            if (StringMaster.contains(val.getName(), part.toString()))
                data.add(val, active.getValue(val));
        }
        return getAnimPart(data, active, part);
    }

    private Anim getAnimPart(AnimData data, DC_ActiveObj active, ANIM_PART part) {
        boolean exists = false;
        List<SpriteAnimation> sprites = new LinkedList<>();
        for (String path :
                StringMaster.openContainer(data.getValue(ANIM_VALUES.SPRITES))) {
            sprites.add(new SpriteAnimation(path));
            exists = true;
        }
        List<ParticleEmitter> list = new LinkedList<>();
        for (String path :
                StringMaster.openContainer(data.getValue(ANIM_VALUES.PARTICLE_EFFECTS))) {
            ParticleEmitter emitter = null;
            try {
                emitter = new ParticleEmitter(new EnumMaster<SFX>().
                        retrieveEnumConst(SFX.class, path));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (emitter != null)
                list.add(emitter
                );
            exists = true;
        }
        if (!exists) return null;
        Anim anim = new Anim(active, data);
        anim.setPart(part);
        anim.setSprites(sprites);
        anim.setEmitterList(list);
        return anim;
    }


    public enum ANIM_PART {
        CAST,
        RESOLVE,
        MAIN, //flying missile
        IMPACT,
        AFTEREFFECT
    }


}
