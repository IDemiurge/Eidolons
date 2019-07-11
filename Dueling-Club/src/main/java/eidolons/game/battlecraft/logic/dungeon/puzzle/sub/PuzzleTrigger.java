package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import main.elements.conditions.Condition;
import main.game.logic.event.Event;

public class PuzzleTrigger {
    Puzzle puzzle;
    Event.EVENT_TYPE event;
    Condition checks;
    Runnable action;

    public PuzzleTrigger(Puzzle puzzle, Event.EVENT_TYPE event, Condition checks, Runnable action) {
        this.puzzle = puzzle;
        this.event = event;
        this.checks = checks;
        this.action = action;
    }
}
