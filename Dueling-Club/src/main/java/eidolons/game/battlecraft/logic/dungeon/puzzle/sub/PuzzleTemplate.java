package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleMaster;

public interface PuzzleTemplate {

    PuzzleMaster.PUZZLE_ACTION_BASE getActionType();
    PuzzleMaster.PUZZLE_SOLUTION getSolution();
}
