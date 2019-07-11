package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleMaster;

public abstract class PuzzleTemplate {

    protected abstract PuzzleMaster.PUZZLE_ACTION_BASE getActionType();
    protected abstract PuzzleMaster.PUZZLE_SOLUTION getSolution();
}
