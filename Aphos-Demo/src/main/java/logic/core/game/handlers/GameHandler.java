package logic.core.game.handlers;

import logic.core.game.Game;

public abstract class GameHandler {
    protected Game game;

    public GameHandler(Game game) {
        this.game = game;
    }
}
