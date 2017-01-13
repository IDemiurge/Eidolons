package main.libgdx.anims;

import main.ability.effects.DealDamageEffect;
import main.ability.effects.Effect;
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
import main.libgdx.anims.AnimData.ANIM_VALUES;

/**
 * Created by JustMe on 1/11/2017.
 */
public class EffectAnim extends Anim {
    public EffectAnim(Effect e, ANIM_MOD template) {
        super(null, null);
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

    AnimData getAnimData(Effect e) {
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

    private AnimData getDamageAnimData(DealDamageEffect e) {
        AnimData data = new AnimData();
        data.setValue(ANIM_VALUES.SPRITES, "damage\\" + ((DealDamageEffect) e).getDamage_type().toString());
        return data;
    }

    ANIM_MOD[] getAnims(Effect e) {

        return null;
    }
}
