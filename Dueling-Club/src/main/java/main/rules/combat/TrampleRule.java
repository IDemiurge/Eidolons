package main.rules.combat;

import main.ability.conditions.shortcut.StdPassiveCondition;
import main.ability.conditions.special.AttackCondition;
import main.ability.conditions.special.RollCondition;
import main.ability.effects.Effects;
import main.ability.effects.SelfMoveEffect;
import main.ability.effects.oneshot.special.InstantDeathEffect;
import main.content.CONTENT_CONSTS.ROLL_TYPES;
import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.elements.conditions.Conditions;
import main.elements.conditions.NumericCondition;
import main.entity.Ref.KEYS;
import main.game.MicroGame;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.rules.DC_RuleImpl;

public class TrampleRule extends DC_RuleImpl {

    public TrampleRule(MicroGame game) {
        super(game);
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
                new StdPassiveCondition(STANDARD_PASSIVES.TRAMPLE, KEYS.EVENT_SOURCE),
                new AttackCondition(false),
                new NumericCondition("{source_total_weight}+{Strength}*2",
                        "{event_target_bludgeoning_resistance}/100*{event_target_total_weight}*2+{event_target_Strength}*6")
                // reflex roll?
                , new RollCondition(ROLL_TYPES.REFLEX)

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
	 * check
	 */
}
