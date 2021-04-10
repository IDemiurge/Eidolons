package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

public interface PuzzleTemplate {

    PuzzleEnums.PUZZLE_ACTION_BASE getActionType();
    PuzzleEnums.PUZZLE_SOLUTION getSolution();
}
