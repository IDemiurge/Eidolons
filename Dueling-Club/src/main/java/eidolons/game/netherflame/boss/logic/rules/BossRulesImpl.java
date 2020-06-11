package eidolons.game.netherflame.boss.logic.rules;

import eidolons.game.netherflame.boss.BossHandler;
import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.BossModel;
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
