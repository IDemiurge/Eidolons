package main.elements.conditions;

import main.game.core.game.Game;
import main.game.core.game.MicroGame;

public abstract class MicroCondition extends ConditionImpl {
    protected MicroGame game;

    public void setGame(Game game) {
        this.game = (MicroGame) game;
        super.setGame(game);
    }
}
