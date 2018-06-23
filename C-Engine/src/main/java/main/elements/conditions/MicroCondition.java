package main.elements.conditions;

import main.game.core.game.Game;
import main.game.core.game.GenericGame;

public abstract class MicroCondition extends ConditionImpl {
    protected GenericGame game;

    public void setGame(Game game) {
        this.game = (GenericGame) game;
        super.setGame(game);
    }
}
