package eidolons.ability;

import eidolons.ability.effects.attachment.AddTriggerEffect;
import eidolons.ability.effects.containers.AbilityEffect;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.ability.ActiveAbility;
import main.ability.effects.Effect;
import main.ability.effects.MicroEffect;
import main.elements.conditions.Conditions;
import main.elements.conditions.RefCondition;
import main.elements.conditions.standard.CustomCondition;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref;
import main.game.logic.event.Event;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.LogMaster;

public class AddSpecialEffects extends MicroEffect {
    private Effect effects;
    private SPECIAL_EFFECTS_CASE case_type;
    private String caseName;
    private String abilName;
    private AddTriggerEffect triggerEffect;

    public AddSpecialEffects(SPECIAL_EFFECTS_CASE case_type, Effect effects) {
        this.effects = effects;
        this.case_type = case_type;
        LogMaster.log(LogMaster.CORE_DEBUG, "add-special effect");

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
        if (!(ref.getTargetObj() instanceof DC_Obj)) {
            return false;
        }
        DC_Obj targetObj = (DC_Obj) ref.getTargetObj();

        targetObj.addSpecialEffect(case_type, effects);
        if (triggerEffect == null) {
            triggerEffect = getTriggerEffect();
            //TODO might be easier to use direct hard-code for those cases
            if (triggerEffect != null) {
                triggerEffect.apply(ref);
            }
        }
        return true;
    }

    public void remove() {
        if (triggerEffect != null) {
            triggerEffect.remove();
        }

    }

    private AddTriggerEffect getTriggerEffect() {
//TODO MISSING CASES
        FixedTargeting targeting = new FixedTargeting(Ref.KEYS.SOURCE);
        Event.STANDARD_EVENT_TYPE event=null ;
        Conditions conditions=new Conditions();
        switch (case_type) {
            case ON_KILL:
            case ON_DEATH:
                // now in unit.kill()
//                return new AddTriggerEffect(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED,
//                        new RefCondition(
//                                case_type == SPECIAL_EFFECTS_CASE.ON_DEATH ?
//                                        KEYS.EVENT_TARGET : KEYS.EVENT_SOURCE,
//                                KEYS.SOURCE, true),
//                        new ActiveAbility(new FixedTargeting(KEYS.TARGET), getEffects())
//                );
            case SPELL_IMPACT:
                conditions.add(new RefCondition(Ref.KEYS.SOURCE, Ref.KEYS.EVENT_SOURCE));
                event = Event.STANDARD_EVENT_TYPE.SPELL_RESOLVED;
                break;
            case SPELL_HIT:
                targeting = new FixedTargeting(Ref.KEYS.SOURCE); //TODO igg demo fix
                conditions.add(new RefCondition(Ref.KEYS.SOURCE, Ref.KEYS.EVENT_TARGET));
                event = Event.STANDARD_EVENT_TYPE.SPELL_RESOLVED;
                break;
            case SPELL_RESISTED:
            case SPELL_RESIST:
                event = Event.STANDARD_EVENT_TYPE.SPELL_RESOLVED;
                break;
            case ON_TURN:
                conditions.add(new CustomCondition() {
                    @Override
                    public boolean check(Ref ref) {
                        return !ExplorationMaster.isExplorationOn();
                    }
                }) ;
                event = Event.STANDARD_EVENT_TYPE.UNIT_HAS_CHANGED_FACING;
                //TODO facing change
                break;
            case MOVE:
                conditions.add(new CustomCondition() {
                    @Override
                    public boolean check(Ref ref) {
                        return !ExplorationMaster.isExplorationOn();
                    }
                }) ;

                event = Event.STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING;
                //TODO facing change
                break;
            case ON_COMBAT_END:
                event = Event.STANDARD_EVENT_TYPE.COMBAT_ENDS;
                break;
            case ON_COMBAT_START:
                event = Event.STANDARD_EVENT_TYPE.COMBAT_STARTS;
                //TODO combat end / start
                break;
            case  NEW_TURN:
                event = Event.STANDARD_EVENT_TYPE.UNIT_NEW_ROUND_STARTED;
                break;
            case  END_TURN:
                event = Event.STANDARD_EVENT_TYPE.ROUND_ENDS;
                //TODO combat end / start
                break;
        }
        if (event == null) {
            return null;
        }
        ActiveAbility abils = new ActiveAbility(targeting, effects);
        return new AddTriggerEffect(event, conditions, abils);
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
