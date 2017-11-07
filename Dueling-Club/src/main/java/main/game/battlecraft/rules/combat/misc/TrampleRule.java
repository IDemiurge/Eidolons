package main.game.battlecraft.rules.combat.misc;

import main.ability.conditions.shortcut.StdPassiveCondition;
import main.ability.conditions.special.AttackCondition;
import main.ability.conditions.special.RollCondition;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.InstantDeathEffect;
import main.ability.effects.oneshot.move.SelfMoveEffect;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.elements.conditions.Conditions;
import main.elements.conditions.NumericCondition;
import main.entity.Ref.KEYS;
import main.game.battlecraft.rules.DC_RuleImpl;
import main.game.battlecraft.rules.RuleMaster.RULE;
import main.game.core.game.MicroGame;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

public class TrampleRule extends DC_RuleImpl {

    public TrampleRule(MicroGame game) {
        super(game);
    }


    protected RULE getRuleEnum() {
        return RULE.TRAMPLE ;
    }
    @Override
    public void initEffects() {
        effects = new Effects(
                // new RollCondition(ROLL_TYPES.REFLEX))
                new InstantDeathEffect(false, null),
                // conditional ( new SpaceCondition(),
                new SelfMoveEffect());
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
                , new RollCondition(GenericEnums.ROLL_TYPES.REFLEX)

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
