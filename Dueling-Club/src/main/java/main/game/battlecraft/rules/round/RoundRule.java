package main.game.battlecraft.rules.round;

import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;

public abstract class RoundRule {

    protected DC_Game game;

    public RoundRule(DC_Game game) {
        this.game = game;
    }

    public void newTurn() {
        for (Unit hero : game.getUnits()) {
            if (check(hero)) {
                apply(hero);
            }
        }
    }

    public abstract boolean check(Unit unit);

    public abstract void apply(Unit unit);

}
