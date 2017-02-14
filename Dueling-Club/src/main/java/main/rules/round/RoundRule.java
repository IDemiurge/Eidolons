package main.rules.round;

import main.entity.obj.unit.DC_HeroObj;
import main.game.DC_Game;

public abstract class RoundRule {

    protected DC_Game game;

    public RoundRule(DC_Game game) {
        this.game = game;
    }

    public void newTurn() {
        for (DC_HeroObj hero : game.getUnits()) {
            if (check(hero)) {
                apply(hero);
            }
        }
    }

    public abstract boolean check(DC_HeroObj unit);

    public abstract void apply(DC_HeroObj unit);

}
