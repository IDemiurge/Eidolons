package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.meta.igg.pale.PaleAspect;
import main.elements.conditions.Condition;
import main.elements.triggers.Trigger;
import main.game.logic.event.Event;

public class PuzzleTrigger extends Trigger {
    Puzzle puzzle;
    PUZZLE_TRIGGER type;

    public enum PUZZLE_TRIGGER{
    ENTER,
    ACTION,
    PUNISH,
    SOLVE, EXIT,
}

    public PuzzleTrigger(Puzzle puzzle, PUZZLE_TRIGGER type, Event.EVENT_TYPE event, Condition checks, Runnable action) {
        super(event, checks);
        callback = action;
        this.puzzle = puzzle;
        this.type = type;
    }
    public boolean isRemoveOnReset() {
        return false;
    }
    @Override
    public boolean trigger() {
        return super.trigger();
    }

    @Override
    public boolean check(Event event) {
        if (puzzle.isPale()) {
            if (PaleAspect.ON) {
                if (type == PUZZLE_TRIGGER.ENTER) {
                    return false;
                }
            }
        }
        return super.check(event);
    }
    @Override
    public boolean isRemoveAfterTriggers(boolean result) {
        switch (type) {
            case ENTER:
                break;
            case ACTION:
                break;
            case PUNISH:
                return false;
            case SOLVE:
                break;
        }
        return super.isRemoveAfterTriggers(result);
    }

    @Override
    public String toString() {
        return type+ " trigger for " + puzzle;
    }

}
