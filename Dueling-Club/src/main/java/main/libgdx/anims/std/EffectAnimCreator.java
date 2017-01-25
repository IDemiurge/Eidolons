package main.libgdx.anims.std;

import main.ability.effects.DealDamageEffect;
import main.ability.effects.Effect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.data.filesys.PathFinder;
import main.entity.Ref.KEYS;
import main.entity.obj.top.DC_ActiveObj;
import main.libgdx.GdxColorMaster;
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
import main.libgdx.anims.ANIM_MODS.OBJ_ANIMS;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 1/11/2017.
 */
public class EffectAnimCreator {

    public static Anim getEffectAnim(Effect e) {
        DC_ActiveObj active = (DC_ActiveObj) e.getActiveObj();
        switch (e.getClass().getSimpleName().replace("Effect", "")) {
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
                 ()-> active.getRef().getObj(KEYS.SUMMONED).getImagePath(),
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
        if (e instanceof DealDamageEffect)
            return PathFinder.getSfxPath() + "damage\\"
                    + "fire"
//         + ((DealDamageEffect) e).getDamage_type().toString()
                    ;
        return null;
    }

    private static String getSprites(Effect e) {
        if (e instanceof DealDamageEffect)

            return PathFinder.getSpritesPath() + "damage\\"
                    + "fire"
//             +  ((DealDamageEffect) e).getDamage_type().toString()
                    + ".png";
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
