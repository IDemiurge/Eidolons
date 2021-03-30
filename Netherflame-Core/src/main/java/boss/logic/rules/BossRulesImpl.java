package boss.logic.rules;

import boss.BossHandler;
import boss.BossManager;
import boss.BossModel;
import main.game.logic.event.Event;

public class BossRulesImpl <T extends BossModel> extends BossHandler<T> implements BossRules {
    public BossRulesImpl(BossManager<T> manager) {
        super(manager);
    }
    @Override
    public void handleEvent(Event e) {
/*
custom rules for extra attacks
 */
    }

    @Override
    public void newTurn() {

    }

    @Override
    public void respawned() {

    }

    @Override
    public void newHeroChosen() {

    }

    @Override
    public void death() {

    }
}
