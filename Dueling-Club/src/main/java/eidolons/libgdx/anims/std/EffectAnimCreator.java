package eidolons.libgdx.anims.std;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.anims.ANIM_MODS.ANIM_MOD;
import eidolons.libgdx.anims.ANIM_MODS.OBJ_ANIMS;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.AnimData.ANIM_VALUES;
import eidolons.libgdx.anims.Animation;
import eidolons.libgdx.anims.CompositeAnim;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.bf.GridMaster;
import main.ability.effects.Effect;
import main.data.filesys.PathFinder;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
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
          e.getActiveObj().getOwnerUnit().getCoordinates()); //TODO from parent anim's origin!
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
        anim.setPart(ANIM_PART.MISSILE); //TODO gotta be some way to generalize this
        anim.setMaster(AnimMaster.getInstance());
        return anim;
    }

    private static Anim createAnim(Effect e) {
        DC_ActiveObj active = (DC_ActiveObj) e.getActiveObj();
        return createEffectAnim(e, active, e.getClass());
    }

    private static Anim createEffectAnim(Effect e, DC_ActiveObj active, Class<?> clazz) {

        switch (clazz.getSimpleName().replace("Effect", "")) {
            case "DealDamage":
                return new HitAnim(active, getDamageAnimData((DealDamageEffect) e),
                 ((DealDamageEffect) e).getDamageType());
            case "Drain": // missile back?
            case "ModifyValue":
                ModifyValueEffect modEffect = (ModifyValueEffect) e;
                return new HitAnim(

                 active, getModValAnimData(modEffect)
                 , false, GdxColorMaster.getParamColor(modEffect.getParam()),
                 modEffect.getLastModValue(),
                 ImageManager.getValueIconPath(modEffect.getParam())
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
        data.setValue(ANIM_VALUES.PARTICLE_EFFECTS, getVfx(e));
        //        data.setValue(ANIM_VALUES.LIGHT_AMBIENT,        getLight
        //         (e));

        //        data.setValue(ANIM_VALUES.LIGHT_FOCUS,        getLight
        //         (e));

        return data;
    }

    private static String getLight(Effect e) {
        return null;
    }

    private static String getVfx(Effect e) {
        if (e instanceof DealDamageEffect) {

            return PathFinder.getVfxPath() + "spell/damage/"
             //                    + "fire"
             + ((DealDamageEffect) e).getDamageType().toString()
             ;
        }
        return null;
    }

    private static String getSprites(Effect e) {
        if (e instanceof DealDamageEffect) {
            String name = "";
            switch (((DealDamageEffect) e).getDamageType()) {
                case FIRE:
                default:
                    name = "fire 5 5";
                    break;
            }
            return PathFinder.getSpritesPath() + "damage/"
             + name
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
