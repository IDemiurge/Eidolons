package main.rules;

import main.game.MicroGame;

public class DC_TriggerRules {

    private TriggerRule[] rules;
    private MicroGame game;

    public DC_TriggerRules(MicroGame game) {
        this.game = game;
        rules = new TriggerRule[]{

        };
    }

    public void reset() {
        removeAll();
        init();
    }

    private void removeAll() {
        for (TriggerRule rule : rules) {
            rule.removeFrom(game);
        }
    }

    public void init() {
        for (TriggerRule rule : rules) {
            if (!rule.isDisabled()) {
                rule.init(game);
            }
        }
    }
}
