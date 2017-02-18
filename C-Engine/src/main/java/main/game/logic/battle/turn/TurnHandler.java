package main.game.logic.battle.turn;

import main.swing.generic.components.Refreshable;

public interface TurnHandler extends Refreshable {

    void timerElapsed();

    void timerStarted();

    void updateGraphics();

}
