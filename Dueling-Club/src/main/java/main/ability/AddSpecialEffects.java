package main.ability;

import main.ability.effects.Effect;
import main.ability.effects.containers.AbilityEffect;
import main.ability.effects.oneshot.MicroEffect;
import main.ability.effects.oneshot.common.AddTriggerEffect;
import main.elements.conditions.RefCondition;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_Obj;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.LogMaster;

public class AddSpecialEffects extends MicroEffect {
    private Effect effects;
    private SPECIAL_EFFECTS_CASE case_type;
    private String caseName;
    private String abilName;
    private AddTriggerEffect triggerEffect;

    public AddSpecialEffects(SPECIAL_EFFECTS_CASE case_type, Effect effects) {
        this.effects = effects;
        this.case_type = case_type;
        main.system.auxiliary.LogMaster.log(LogMaster.CORE_DEBUG, "add-special effect");

    }

    public AddSpecialEffects(String case_type, String abilName) {
        this.caseName = case_type;
        this.abilName = abilName;
    }

    @Override
    public boolean applyThis() {
        if (case_type == null) {
            case_type = new EnumMaster<SPECIAL_EFFECTS_CASE>().retrieveEnumConst(
                    SPECIAL_EFFECTS_CASE.class, caseName);
        }
        if (effects == null) {
            effects = new AbilityEffect(abilName).getEffects();
        }
        if (!(ref.getTargetObj() instanceof DC_Obj))
            return false;
        DC_Obj targetObj = (DC_Obj) ref.getTargetObj();

        targetObj.addSpecialEffect(case_type, effects);
        if (triggerEffect==null )
        {
            triggerEffect = getTriggerEffect();
            if (triggerEffect!=null )
            triggerEffect.apply(ref);
        }
        return true;
    }
    public void remove() {
        if (triggerEffect!=null )
            triggerEffect.remove();

    }
    private AddTriggerEffect getTriggerEffect() {


        switch (case_type){
            case ON_KILL:
            case ON_DEATH:
             return    new AddTriggerEffect(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED,
                 new RefCondition(
                  case_type== SPECIAL_EFFECTS_CASE.ON_DEATH?
                   KEYS.EVENT_TARGET:  KEYS.EVENT_SOURCE ,
                  KEYS.SOURCE, true),
                 new ActiveAbility(new FixedTargeting(KEYS.TARGET), getEffects())
                );
            case SPELL_IMPACT:
                break;
            case SPELL_HIT:
                break;
            case SPELL_RESISTED:
                break;
            case SPELL_RESIST:
                break;
            case MOVE:
                break;
            case NEW_TURN:
                break;
            case END_TURN:
                break;
        }
        return null;
    }

    public Effect getEffects() {
        return effects;
    }

    public SPECIAL_EFFECTS_CASE getCase_type() {
        return case_type;
    }

    public String getCaseName() {
        return caseName;
    }

    public String getAbilName() {
        return abilName;
    }

}
