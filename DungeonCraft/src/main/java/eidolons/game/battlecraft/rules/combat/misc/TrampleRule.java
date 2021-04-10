package eidolons.game.battlecraft.rules.combat.misc;

import eidolons.ability.conditions.shortcut.StdPassiveCondition;
import eidolons.ability.conditions.special.AttackCondition;
import eidolons.ability.conditions.special.RollCondition;
import eidolons.ability.effects.oneshot.move.SelfMoveEffect;
import eidolons.game.battlecraft.rules.DC_RuleImpl;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleEnums.RULE;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.InstantDeathEffect;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.elements.conditions.Conditions;
import main.elements.conditions.NumericCondition;
import main.entity.Ref.KEYS;
import main.game.core.game.GenericGame;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

public class TrampleRule extends DC_RuleImpl {

    public TrampleRule(GenericGame game) {
        super(game);
    }


    protected RULE getRuleEnum() {
        return RuleEnums.RULE.TRAMPLE;
    }

    @Override
    public void initEffects() {
        effects = new Effects(
         // new RollCondition(ROLL_TYPES.REFLEX))
         new InstantDeathEffect(false, null),
         // conditional ( new SpaceCondition(),
         new SelfMoveEffect(){
             @Override
             public boolean applyThis() {
                 return super.applyThis();
             }
         });
        // new ConditionalEffect //isDead
    }

    @Override
    public void initConditions() {
        conditions = new Conditions(
         // make sure the *source* is correct ref!
         new StdPassiveCondition(UnitEnums.STANDARD_PASSIVES.TRAMPLE, KEYS.EVENT_SOURCE),
         new AttackCondition(false),
         new NumericCondition("{source_total_weight}+{Strength}*2",
          "{event_target_bludgeoning_resistance}/100*{event_target_total_weight}*2+{event_target_Strength}*6")
         // reflex roll?
         , new RollCondition(GenericEnums.RollType.reflex)

         // space
         // force
        );
        conditions.setFastFailOnCheck(true);
    }

    @Override
    public void initEventType() {
        event_type = STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_HIT;

    }
    /*
     *
	 * on hit...
	 * 
	 * preCheck
	 */
}
