package main.libgdx.anims.std;

import main.ability.effects.DealDamageEffect;
import main.ability.effects.Effect;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.game.battlefield.Coordinates;
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.system.auxiliary.LogMaster;

/**
 * Created by JustMe on 1/11/2017.
 */
public class EffectAnim extends Anim {
    public EffectAnim(Effect e) {
        super((Entity) e.getRef().getActive(), getAnimData(e));
        main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG,this +" created: " +data);
        /*
        animation could be constructed from effects and action/spell-properties

construct while it resolves - add stuff onto queue from each effect that has been applied
via trigger etc
        e.g., Axe Swing:

        > counter attack
        > parry/block/armor

        > damage effect

        OR Fire Ball
        > casting anim on source
        > sprite missile from source to target
        > sprite/emitter impact on target
        > damage animation on each target + text
         */
    }

   public static AnimData getAnimData(Effect e) {
        AnimData data = new AnimData();
        switch (e.getClass().getSimpleName().replace("Effect", "")) {
            case "DealDamage":
                return getDamageAnimData((DealDamageEffect) e);
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
        return data;
    }

    @Override
    public ANIM_PART getPart() {
        return ANIM_PART.IMPACT;
    }

    @Override
    protected Coordinates getDestinationCoordinates() {
        return getRef().getTargetObj().getCoordinates();
    }

    @Override
    protected Coordinates getOriginCoordinates() {
        return getRef().getTargetObj().getCoordinates();
    }


    private static AnimData getDamageAnimData(DealDamageEffect e) {
        AnimData data = new AnimData();
        data.setValue(ANIM_VALUES.SPRITES,getSprites(e));
        data.setValue(ANIM_VALUES.PARTICLE_EFFECTS,getSfx(e));
//        data.setValue(ANIM_VALUES.LIGHT_AMBIENT,        getLight
//         (e));
//        data.setValue(ANIM_VALUES.LIGHT_FOCUS,        getLight
//         (e));

        return data;
    }

    private static String getLight(Effect e) {
        return null ;
    }

    private static String getSfx(Effect e) {
        if (e instanceof  DealDamageEffect)
        return PathFinder.getSfxPath()+ "damage\\"
         +         "fire"
//         + ((DealDamageEffect) e).getDamage_type().toString()
         ;
        return null;
    }

    private static String getSprites( Effect e) {
        if (e instanceof  DealDamageEffect)

            return   PathFinder.getSpritesPath()+   "damage\\"
             +"fire"
//             +  ((DealDamageEffect) e).getDamage_type().toString()
             + ".png";
        return null;
    }

    ANIM_MOD[] getAnims(Effect e) {

        return null;
    }

    public static ANIM_PART getPartToAttachTo(Effect effect) {
//        if (e instanceof  DealDamageEffect)
        return ANIM_PART.IMPACT;

//        return ANIM_PART.AFTEREFFECT;

    }
}
