package main.rules;

import main.ability.effects.Effects;
import main.ability.effects.oneshot.special.AddStatusEffect;
import main.game.MicroGame;

public class StatusRule extends DC_RuleImpl { // wounded, panic, treason,
    // channeled

    public StatusRule(MicroGame game) {
        super(game);
//        initStatus();

    }

    @Override
    public void initEffects() {
        effects = new Effects();
//        effects.added(new AddStatusEffect(STATUS));
//        initAdditionalEffects();
    }

    @Override
    public void initConditions() {

//        initAdditionalConditions();

    }

    @Override
    public void initEventType() {
        // TODO Auto-generated method stub

    }

}
