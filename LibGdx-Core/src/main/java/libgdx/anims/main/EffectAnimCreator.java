package libgdx.anims.main;

import com.badlogic.gdx.utils.ObjectMap;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.anims.*;
import libgdx.anims.ANIM_MODS.ANIM_MOD;
import libgdx.anims.ANIM_MODS.OBJ_ANIMS;
import libgdx.anims.AnimData.ANIM_VALUES;
import eidolons.content.consts.VisualEnums.ANIM_PART;
import libgdx.anims.std.ActionAnim;
import libgdx.anims.std.HitAnim;
import libgdx.bf.GridMaster;
import main.ability.effects.Effect;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 1/11/2017.
 */
public class EffectAnimCreator {

    private static final ObjectMap<Effect, Anim> map = new ObjectMap<>(125);
    private static boolean cachingOn;

    public static float getEffectAnimDelay(Effect e, Animation anim, ANIM_PART part) {

        Animation subAnim;
        if (anim instanceof CompositeAnim) {
            subAnim = ((CompositeAnim) anim).getMap().get(part);
        } else {
            subAnim = anim;
        }

        Coordinates destination = null;
        if (anim instanceof Anim) {
            destination = ((Anim) anim).getDestinationCoordinates();
        }
        Float distance =
                GridMaster.getDistance(destination,
                        e.getActiveObj().getOwnerUnit().getCoordinates()); //TODO from parent anim's origin!


        return distance / subAnim.getPixelsPerSecond();
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
        anim.setPart(VisualEnums.ANIM_PART.MISSILE); //TODO gotta be some way to generalize this
        anim.setMaster(AnimMaster.getInstance());
        return anim;
    }

    private static Anim createAnim(Effect e) {
        ActiveObj active = (ActiveObj) e.getActiveObj();
        return createEffectAnim(e, active, e.getClass());
    }

    private static Anim createEffectAnim(Effect e, ActiveObj active, Class<?> clazz) {

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
        return new AnimData();
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

    public static String getVfx(GenericEnums.DAMAGE_TYPE type) {
        switch (type) {
            case DEATH:
                return PathFinder.getVfxPath() + GenericEnums.VFX.necro_impact.getPath();
            case MAGICAL:
                return PathFinder.getVfxPath() + GenericEnums.VFX.pale_impact.getPath();
            case PSIONIC:
                return PathFinder.getVfxPath() + GenericEnums.VFX.warp_impact.getPath();
            case CHAOS:
//                return PathFinder.getVfxPath() +GenericEnums.VFX.chaos_impact.getPath();
                return PathFinder.getVfxPath() + GenericEnums.VFX.nether_impact3.getPath();
            case SHADOW:
                return PathFinder.getVfxPath() + GenericEnums.VFX.invert_impact.getPath();
        }
        return PathFinder.getVfxAtlasPath() + "spell/impact/"
                + type.toString() + " impact"
                ;
    }

    private static String getVfx(Effect e) {
        if (e instanceof DealDamageEffect) {
            return getVfx(((DealDamageEffect) e).getDamageType());

        }
        return null;
    }

    private static String getSprites(Effect e) {
//        if (e instanceof DealDamageEffect) {
//            String name = "";
//            switch (((DealDamageEffect) e).getDamageType()) {
//                case FIRE:
//                default:
//                    name = "fire 5 5";
//                    break;
//            }
//            return PathFinder.getSpellSpritesPath() + "damage/"
//                    + name
//                    + ".png";
//        }
        return null;
    }

    public static ANIM_PART getPartToAttachTo(Effect effect) {
        //        if (e instanceof  DealDamageEffect)
        return VisualEnums.ANIM_PART.IMPACT;

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
