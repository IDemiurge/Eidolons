package main.libgdx.anims.std;

import main.ability.effects.oneshot.DealDamageEffect;
import main.ability.effects.Effect;
import main.ability.effects.common.ModifyValueEffect;
import main.data.filesys.PathFinder;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.Obj;
import main.game.battlefield.Coordinates;
import main.libgdx.GdxColorMaster;
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
import main.libgdx.anims.ANIM_MODS.OBJ_ANIMS;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.Animation;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.CompositeAnim;
import main.libgdx.bf.GridMaster;
import main.system.images.ImageManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 1/11/2017.
 */
public class EffectAnimCreator {

    private static Map<Effect, Anim> map = new HashMap<>();
    private static boolean cachingOn;

    public static float getEffectAnimDelay(Effect e, Animation anim, ANIM_PART part) {

        Anim subAnim;
        if (anim instanceof CompositeAnim) {
            subAnim = ((CompositeAnim) anim).getMap().get(part);
        } else {
            subAnim = (Anim) anim;
        }

        Coordinates destination = null;
        if (anim instanceof Anim) {
            destination = ((Anim) anim).getDestinationCoordinates();
        }
        Float distance =
                GridMaster.getDistance(destination,
                        e.getActiveObj().getOwnerObj().getCoordinates()); //TODO from parent anim's origin!
        float delay = distance / subAnim.getPixelsPerSecond();


        return delay;
    }

    public static Anim getOrCreateEffectAnim(Effect e) {
        Anim anim = map.get(e);

        if (anim == null) {
            anim = createAnim(e);
            if (cachingOn) {
                map.put(e, anim);
            }
        }
        if (anim == null) {
            return null;
        }
        Obj target = e.getRef().getTargetObj();
        if (target != null) {
            anim.setForcedDestination(target.getCoordinates());
        }
        anim.setPart(ANIM_PART.MAIN); //TODO gotta be some way to generalize this
        return anim;
    }

    private static Anim createAnim(Effect e) {
        DC_ActiveObj active = (DC_ActiveObj) e.getActiveObj();
        return createEffectAnim(e, active, e.getClass());
    }

    private static Anim createEffectAnim(Effect e, DC_ActiveObj active, Class<?> clazz) {

        switch (clazz.getSimpleName().replace("Effect", "")) {
            case "DealDamage":
                return new HitAnim(active, getDamageAnimData((DealDamageEffect) e));
            case "Drain": // missile back?
            case "ModifyValue":
                ModifyValueEffect modEffect = (ModifyValueEffect) e;
                return new HitAnim(

                        active, getModValAnimData(modEffect)
                        , false, GdxColorMaster.getParamColor(modEffect.getParam()),
                        () -> modEffect.getLastModValue(),
                        () -> ImageManager.getValueIconPath(modEffect.getParam())
                );

            case "Raise":
            case "Resurrect":
            case "Summon":
                return new ActionAnim(active, new AnimData(),
                        () -> active.getRef().getObj(KEYS.SUMMONED).getImagePath(),
                        new ANIM_MOD[]{
                                OBJ_ANIMS.FADE_IN,
                        }
                );
            case "InstantDeath":
                //flash
            case "AddBuff":
            case "OwnershipChange":
            case "Move":
            case "ChangeFacing":
            case "DurabilityReduction": // show item?


        }
        Class<?> superclass = e.getClass().getSuperclass();
        if (superclass != null) {
            return createEffectAnim(e, active, superclass);
        }
        return null;
    }

    private static AnimData getModValAnimData(ModifyValueEffect e) {
        AnimData data = new AnimData();
        return data;
    }

    private static AnimData getDamageAnimData(DealDamageEffect e) {
        AnimData data = new AnimData();
        data.setValue(ANIM_VALUES.SPRITES, getSprites(e));
        data.setValue(ANIM_VALUES.PARTICLE_EFFECTS, getSfx(e));
//        data.setValue(ANIM_VALUES.LIGHT_AMBIENT,        getLight
//         (e));

//        data.setValue(ANIM_VALUES.LIGHT_FOCUS,        getLight
//         (e));

        return data;
    }

    private static String getLight(Effect e) {
        return null;
    }

    private static String getSfx(Effect e) {
        if (e instanceof DealDamageEffect) {
            return PathFinder.getSfxPath() + "damage\\"
                    + "fire"
//         + ((DealDamageEffect) e).getDamageType().toString()
                    ;
        }
        return null;
    }

    private static String getSprites(Effect e) {
        if (e instanceof DealDamageEffect)

        {
            return PathFinder.getSpritesPath() + "damage\\"
                    + "fire"
//             +  ((DealDamageEffect) e).getDamageType().toString()
                    + ".png";
        }
        return null;
    }

    public static ANIM_PART getPartToAttachTo(Effect effect) {
//        if (e instanceof  DealDamageEffect)
        return ANIM_PART.IMPACT;

//        return ANIM_PART.AFTEREFFECT;

    }

    ANIM_MOD[] getAnims(Effect e) {

        return null;
    }


    public enum ANIMATED_EFFECT {
        BUFF,
        SUMMON,
        TURN,
        DISPLACE,
        MODIFY_VALUE,
        DISPEL,
    }
}
