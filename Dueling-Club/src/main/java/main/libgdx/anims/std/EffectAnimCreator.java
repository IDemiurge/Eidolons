package main.libgdx.anims.std;

import main.ability.effects.DealDamageEffect;
import main.ability.effects.Effect;
import main.data.filesys.PathFinder;
import main.entity.obj.top.DC_ActiveObj;
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;

/**
 * Created by JustMe on 1/11/2017.
 */
public class EffectAnimCreator {


    public static Anim getEffectAnim(Effect e) {
        DC_ActiveObj active = (DC_ActiveObj) e.getActiveObj();
        switch (e.getClass().getSimpleName().replace("Effect", "")) {
            case "DealDamage":
                return new HitAnim(active, getDamageAnimData((DealDamageEffect) e));
            case "ModifyValue":
            case "InstantDeath":
            case "AddBuff":
            case "OwnershipChange":
            case "Raise":
            case "Resurrect":
            case "Summon":
            case "Move":
            case "ChangeFacing":
            case "DurabilityReduction":


        }
        return null;
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
}
