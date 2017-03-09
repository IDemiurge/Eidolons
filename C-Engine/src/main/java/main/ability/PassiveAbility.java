package main.ability;

import main.ability.effects.Effect;
import main.elements.targeting.Targeting;

//activates upon its containing object's creation
public class PassiveAbility extends AbilityImpl {

    public PassiveAbility(Targeting t, Effect effects) {
        super(t, effects);
        effects.setIrresistible(true);
        // this.effects = new
        // Effects(ContinuousEffect.transformEffectToContinuous(effects));
    }

    // @Override
    // public boolean resolve() {
    // main.system.auxiliary.LogMaster.log(0, "ABILITY_BEING_RESOLVED "
    // + getClass().getSimpleName());
    // Event event = new Event("ABILITY_BEING_RESOLVED", ref);
    // if (game.fireEvent(event)) {
    //
    // boolean result = true;
    // for (Effect effect : effects) {
    //
    // if (effect instanceof AttachmentEffect) {
    // if (applied)
    // continue;
    // Condition retainCondition = ConditionMaster
    // .getHasPassiveCondition(KEYS.SOURCE, getName());
    // ((AttachmentEffect) effect).setRetainCondition(retainCondition);
    //
    // // buff - set displayLevel(PASSIVE)
    // effect.setIrresistible(true);
    // }
    // result &= effect.apply(ref);
    // }
    // return result;
    //
    //
    // } else
    // return false;
    // }

}
